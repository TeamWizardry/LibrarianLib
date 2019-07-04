package com.teamwizardry.librarianlib.features.facade.component.supporting

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.fastCos
import com.teamwizardry.librarianlib.features.kotlin.fastSin
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.LinkedList
import kotlin.math.PI

interface ILayerRendering {
    val tooltip_im: IMValue<List<String>?>

    /**
     * An opacity value in the range [0, 1]. If this is not equal to 1 the layer will be rendered to an FBO and drawn
     * to a texture. This process clips the layer to its bounds.
     */
    val opacity_rm: RMValueDouble
    var opacity: Double

    /**
     * Sorts the layers by zIndex
     */
    fun sortChildren()

    /**
     * Renders this layer and its sublayers. This method handles the internals of rendering a layer, to simply render
     * content in a layer use [GuiLayer.draw]
     * @param mousePos Mouse position relative to the position of this component
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    fun renderLayer(partialTicks: Float)

    fun renderSkeleton()

    /**
     * Draws a flat colored box over this layer, rounding corners as necessary
     */
    fun drawLayerOverlay()

    /**
     * Draws a bounding box around the edge of this component
     */
    fun drawDebugBoundingBox()

    /**
     * Creates a series of points defining the path the debug bounding box follows. For culling reasons this list
     * must be in clockwise order
     */
    fun createDebugBoundingBoxPoints(): List<Vec2d>

    fun shouldDrawSkeleton(): Boolean

    companion object {
        @JvmStatic
        fun glStateGuarantees() {
            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.enableBlend()
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 1/255f)
            GlStateManager.disableLighting()
        }
    }
}

class LayerRenderHandler: ILayerRendering {
    lateinit var layer: GuiLayer

    override val tooltip_im: IMValue<List<String>?> = IMValue()

    override val opacity_rm: RMValueDouble = RMValueDouble(1.0)
    override var opacity: Double by opacity_rm

    override fun sortChildren() {
        val components = layer.relationships.subLayers
        components.sortBy { it.zIndex }
        components.forEach { it.sortChildren() }
    }

    /**
     * Draw this component, don't override in subclasses unless you know what you're doing.
     *
     * @param mousePos Mouse position relative to the position of this component
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    override fun renderLayer(partialTicks: Float) {
        layer.runLayoutIfNeeded()

        if(!layer.isVisible) {
            renderSkeleton()
            return
        }

        layer.BUS.fire(GuiLayerEvents.PreTransformEvent(partialTicks))

        layer.glApplyTransform(false)

        layer.BUS.fire(GuiLayerEvents.PostTransformEvent(partialTicks))

        layer.clipping.pushEnable()

        if(opacity < 1.0) {
            var scale = Client.guiScaleFactor
            var effectiveSize = layer.size * scale
            while(scale > 1 && (effectiveSize.x > framebufferSize || effectiveSize.y > framebufferSize)) {
                scale--
                effectiveSize = layer.size * scale
            }

            val fbo = useFramebuffer(scale.toDouble()) {
                drawContent(partialTicks)
            }
            fbo.bindFramebufferTexture()
            val uSize = effectiveSize.x / fbo.framebufferTextureWidth
            val vSize = effectiveSize.y / fbo.framebufferTextureHeight
            val size = layer.size
            val color = Color(1f, 1f, 1f, opacity.toFloat())

            GlStateManager.enableTexture2D()
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
            vb.pos(0.0, size.y, 0.0).tex(0.0, 1.0-vSize).color(color).endVertex()
            vb.pos(size.x, size.y, 0.0).tex(uSize, 1.0-vSize).color(color).endVertex()
            vb.pos(size.x, 0.0, 0.0).tex(uSize, 1.0).color(color).endVertex()
            vb.pos(0.0, 0.0, 0.0).tex(0.0, 1.0).color(color).endVertex()
            tessellator.draw()
        } else {
            drawContent(partialTicks)
        }

        layer.clipping.popDisable()

        if (GuiLayer.showDebugBoundingBox) {
            GlStateManager.glLineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color(.75f, 0f, .75f)
            layer.drawDebugBoundingBox()
        }
        if (GuiLayer.showLayoutOverlay && layer.didLayout) {
            GlStateManager.color(1f, 0f, 0f, 0.1f)
            layer.drawLayerOverlay()
        }
        layer.didLayout = false

        layer.glApplyTransform(true)
    }

    private fun drawContent(partialTicks: Float) {
        layer.glApplyContentsOffset(false)

        GlStateManager.pushMatrix()
        layer.BUS.fire(GuiLayerEvents.PreDrawEvent(partialTicks))

        ILayerRendering.glStateGuarantees()
        layer.draw(partialTicks)
        ILayerRendering.glStateGuarantees()

        GlStateManager.popMatrix()

        layer.BUS.fire(GuiLayerEvents.PreChildrenDrawEvent(partialTicks))
        layer.forEachChild { it.renderLayer(partialTicks) }

        GlStateManager.pushMatrix()
        layer.BUS.fire(GuiLayerEvents.PostDrawEvent(partialTicks))
        GlStateManager.popMatrix()

        layer.glApplyContentsOffset(true)
    }

    override fun shouldDrawSkeleton(): Boolean = false

    override fun renderSkeleton() {
        layer.runLayoutIfNeeded()

        layer.glApplyTransform(false)

        layer.glApplyContentsOffset(false)

        layer.forEachChild { it.render.renderSkeleton() }

        layer.glApplyContentsOffset(true)

        if (GuiLayer.showDebugBoundingBox &&
            GuiLayer.showDebugTilt && layer.shouldDrawSkeleton()) {
            GlStateManager.glLineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color(.75f, 0f, .75f)
            GL11.glEnable(GL11.GL_LINE_STIPPLE)
            GL11.glLineStipple(2, 0b0011_0011_0011_0011.toShort())
            layer.drawDebugBoundingBox()
            GL11.glDisable(GL11.GL_LINE_STIPPLE)
        }

        layer.glApplyTransform(true)
    }

    override fun drawLayerOverlay() {
        GlStateManager.disableTexture2D()
        val points = createDebugBoundingBoxPoints()
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)
        val size = layer.size
        vb.pos(size.x/2, size.y/2, 0.0).endVertex()
        points.reversed().forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
    }

    override fun drawDebugBoundingBox() {
        GlStateManager.disableTexture2D()
        val points = createDebugBoundingBoxPoints()
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        points.forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
        GlStateManager.color(0f, 0f, 0f, 0.15f)
        if(GuiLayer.showDebugTilt) {
            vb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
            points.forEach {
                vb.pos(it.x, it.y, -100.0).endVertex()
                vb.pos(it.x, it.y, 0.0).endVertex()
            }
            tessellator.draw()
        }
    }

    override fun createDebugBoundingBoxPoints(): List<Vec2d> {
        val list = mutableListOf<Vec2d>()
        if(layer.clipToBounds && layer.cornerRadius != 0.0) {
            val rad = layer.cornerRadius
            val d = (PI / 2) / 16

            // top-left
            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastCos(angle) * rad, fastSin(angle) * rad)
                list.add(vec(rad - diff.x, rad - diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastSin(angle) * rad, fastCos(angle) * rad)
                list.add(vec(layer.size.x - rad + diff.x, rad - diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastCos(angle) * rad, fastSin(angle) * rad)
                list.add(vec(layer.size.x - rad + diff.x, layer.size.y - rad + diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastSin(angle) * rad, fastCos(angle) * rad)
                list.add(vec(rad - diff.x, layer.size.y - rad + diff.y))
            }

            list.add(vec(0.0, rad))
        } else {
            list.add(vec(0.0, 0.0))
            list.add(vec(layer.size.x, 0.0))
            list.add(vec(layer.size.x, layer.size.y))
            list.add(vec(0.0, layer.size.y))
            list.add(vec(0.0, 0.0))
        }
        return list
    }

    companion object {
        val maxFramebufferCount = 16
        var createdBuffers = 0
        val framebufferSize = 3 * 512
        val buffers = LinkedList<Framebuffer>()

        val bufferStack = LinkedList<Framebuffer>()
        val currentFramebuffer: Framebuffer? = bufferStack.peekFirst()

        fun pushFramebuffer(): Framebuffer {

            val fbo = buffers.pollFirst() ?: createFramebuffer()
            bufferStack.addFirst(fbo)

            GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT)

//            if(ClientTickHandler.ticks % 40 == 0)
            fbo.framebufferClear()
            fbo.bindFramebuffer(true)


            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.pushMatrix()
            GlStateManager.loadIdentity()
            GlStateManager.ortho(0.0, framebufferSize.toDouble(), framebufferSize.toDouble(), 0.0, 1000.0, 3000.0)

            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.pushMatrix()
            GlStateManager.loadIdentity()
//            GlStateManager.scale(1f, -1f, 1f)
            GlStateManager.translate(0.0f, 0.0f, -2000.0f)

            return fbo
        }

        fun popFramebuffer() {
            buffers.addFirst(bufferStack.removeFirst())
            val newFbo = currentFramebuffer
            if(newFbo == null) {
                Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)
            } else {
                newFbo.bindFramebuffer(true)
            }

            GL11.glPopAttrib()
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.popMatrix()
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.popMatrix()
        }

        inline fun useFramebuffer(scale: Double, callback: () -> Unit): Framebuffer {
            val stencilLevel = StencilUtil.currentStencil
            val fbo = pushFramebuffer()
            StencilUtil.clear()
            try {
                GlStateManager.scale(scale, scale, 1.0)
                callback()
            } finally {
                popFramebuffer()
                StencilUtil.resetTest(stencilLevel)
            }
            return fbo
        }

        fun createFramebuffer(): Framebuffer {
            if(createdBuffers == maxFramebufferCount)
                throw IllegalStateException("Exceeded maximum of $maxFramebufferCount nested framebuffers")
            val fbo = Framebuffer(framebufferSize, framebufferSize, true)
            fbo.enableStencil()
            fbo.framebufferColor = floatArrayOf(0f, 0f, 0f, 0f)
            createdBuffers++
            return fbo
        }
    }
}
