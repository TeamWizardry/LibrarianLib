package com.teamwizardry.librarianlib.gui.component.supporting

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.component.GuiLayerFilter
import com.teamwizardry.librarianlib.gui.layers.MaskLayer
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.fastCos
import com.teamwizardry.librarianlib.math.fastSin
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import kotlin.math.PI
import kotlin.math.max

interface ILayerRendering {
    val tooltip_im: IMValue<List<String>?>

    /**
     * An opacity value in the range [0, 1]. If this is not equal to 1 the layer will be rendered to an FBO and drawn
     * to a texture. This process clips the layer to its bounds.
     */
    val opacity_rm: RMValueDouble
    var opacity: Double

    /**
     * How to apply [MaskLayer] masks (docs todo)
     */
    var maskMode: MaskMode

    /**
     * What technique to use to render this layer
     */
    var renderMode: RenderMode

    /**
     * What scaling factor to use when rasterizing this layer using [RenderMode.RENDER_TO_QUAD]
     */
    var rasterizationScale: Int

    /**
     * A filter to apply to this layer. If this is not null this layer will be drawn to a texture and passed to the
     * filter.
     */
    var layerFilter: GuiLayerFilter?

    /**
     * Sorts the layers by zIndex
     */
    fun sortChildren()

    /**
     * Renders this layer and its sublayers. This method handles the internals of rendering a layer, to simply render
     * content in a layer use [GuiLayer.draw]
     */
    fun renderLayer(context: GuiDrawContext)

    fun renderSkeleton(context: GuiDrawContext)

    /**
     * Draws a flat colored box over this layer, rounding corners as necessary
     */
    fun drawLayerOverlay(context: GuiDrawContext)

    /**
     * Draws a bounding box around the edge of this component
     */
    fun drawDebugBoundingBox(context: GuiDrawContext)

    /**
     * Creates a series of points defining the path the debug bounding box follows. For culling reasons this list
     * must be in clockwise order
     */
    fun createDebugBoundingBoxPoints(context: GuiDrawContext): List<Vec2d>

    fun shouldDrawSkeleton(): Boolean

    companion object {
        //TODO: Remove once we've safely ported old code that depends on these guarantees.
        @JvmStatic
        fun glStateGuarantees() {
            GlStateManager.enableTexture()
            GlStateManager.color4f(1f, 1f, 1f, 1f)
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
    override var maskMode: MaskMode = MaskMode.NONE
    override var renderMode: RenderMode = RenderMode.DIRECT
    override var rasterizationScale: Int = 1
    override var layerFilter: GuiLayerFilter? = null

    override fun sortChildren() {
        val components = layer.relationships.subLayers
        components.sortBy { it.zIndex }
        components.forEach { it.sortChildren() }
    }

    fun actualRenderMode(): RenderMode {
        if(renderMode != RenderMode.DIRECT)
            return renderMode
        if(opacity < 1.0 || maskMode != MaskMode.NONE || layerFilter != null)
            return RenderMode.RENDER_TO_FBO
        return RenderMode.DIRECT
    }

    override fun renderLayer(context: GuiDrawContext) {
        context.matrix.push()

        if(!layer.isVisible) {
            renderSkeleton(context)
            return
        }

        context.matrix *= layer.matrix

        layer.clipping.pushEnable(context)

        val renderMode = actualRenderMode()
        if(renderMode != RenderMode.DIRECT) {
            TODO("Waiting on shaders")
            /*
            var maskFBO: Framebuffer? = null
            var layerFBO: Framebuffer? = null
            try {
                 layerFBO = GuiLayerFilter.useFramebuffer(renderMode == RenderMode.RENDER_TO_QUAD, max(1, rasterizationScale)) {
                    drawContent(partialTicks) {
                        // draw all the non-mask children to the current FBO (layerFBO)
                        layer.forEachChild {
                            if (it !is MaskLayer)
                                it.renderLayer(partialTicks)
                        }

                        // draw all the mask children to an FBO, if needed
                        if (maskMode != MaskMode.NONE)
                            maskFBO = GuiLayerFilter.useFramebuffer(false, 1) {
                                layer.forEachChild {
                                    if (it is MaskLayer)
                                        it.renderLayer(partialTicks)
                                }
                            }
                    }
                }

                layerFilter?.filter(this.layer, layerFBO, maskFBO)

                // load the shader
                LayerToTextureShader.alphaMultiply = opacity.toFloat()
                LayerToTextureShader.maskMode = maskMode
                LayerToTextureShader.renderMode = renderMode
                ShaderHelper.useShader(LayerToTextureShader)

                LayerToTextureShader.bindTextures(layerFBO.framebufferTexture, maskFBO?.framebufferTexture)

                val size = layer.size
                val maxU = (size.x * rasterizationScale) / Client.minecraft.displayWidth
                val maxV = (size.y * rasterizationScale) / Client.minecraft.displayHeight

                val tessellator = Tessellator.getInstance()
                val vb = tessellator.buffer
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                vb.pos(0.0, size.y, 0.0).tex(0.0, 1.0 - maxV).endVertex()
                vb.pos(size.x, size.y, 0.0).tex(maxU, 1.0 - maxV).endVertex()
                vb.pos(size.x, 0.0, 0.0).tex(maxU, 1.0).endVertex()
                vb.pos(0.0, 0.0, 0.0).tex(0.0, 1.0).endVertex()
                tessellator.draw()

                ShaderHelper.releaseShader()
            } finally {
                layerFBO?.also { GuiLayerFilter.releaseFramebuffer(it) }
                maskFBO?.also { GuiLayerFilter.releaseFramebuffer(it) }
            }
            */
        } else {
            context.matrix.assertEvenDepth {
                ILayerRendering.glStateGuarantees()
                layer.draw(context)
            }
            layer.forEachChild {
                if(it !is MaskLayer)
                    it.renderLayer(context)
            }
        }

        layer.clipping.popDisable(context)

        if (GuiLayer.showDebugBoundingBox && !layer.isInMask) {
            GlStateManager.lineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color4f(.75f, 0f, .75f, 1f)
            layer.drawDebugBoundingBox(context)
        }
        if (GuiLayer.showLayoutOverlay && layer.didLayout && !layer.isInMask) {
            GlStateManager.color4f(1f, 0f, 0f, 0.1f)
            layer.drawLayerOverlay(context)
        }
        layer.didLayout = false
    }

    override fun shouldDrawSkeleton(): Boolean = false

    override fun renderSkeleton(context: GuiDrawContext) {
        layer.forEachChild { it.render.renderSkeleton(context) }

        if (GuiLayer.showDebugBoundingBox && !layer.isInMask &&
            GuiLayer.showDebugTilt && layer.shouldDrawSkeleton()) {
            GlStateManager.lineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color4f(.75f, 0f, .75f, 1f)
            GL11.glEnable(GL11.GL_LINE_STIPPLE)
            GL11.glLineStipple(2, 0b0011_0011_0011_0011.toShort())
            layer.drawDebugBoundingBox(context)
            GL11.glDisable(GL11.GL_LINE_STIPPLE)
        }
    }

    override fun drawLayerOverlay(context: GuiDrawContext) {
        GlStateManager.disableTexture()
        val points = createDebugBoundingBoxPoints(context)
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)
        val size = layer.size
        vb.pos(size.x/2, size.y/2, 0.0).endVertex()
        points.reversed().forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
    }

    override fun drawDebugBoundingBox(context: GuiDrawContext) {
        GlStateManager.disableTexture()
        val points = createDebugBoundingBoxPoints(context)
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        points.forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
        GlStateManager.color4f(0f, 0f, 0f, 0.15f)
        if(GuiLayer.showDebugTilt) {
            vb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
            points.forEach {
                vb.pos(it.x, it.y, -100.0).endVertex()
                vb.pos(it.x, it.y, 0.0).endVertex()
            }
            tessellator.draw()
        }
    }

    override fun createDebugBoundingBoxPoints(context: GuiDrawContext): List<Vec2d> {
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

}
