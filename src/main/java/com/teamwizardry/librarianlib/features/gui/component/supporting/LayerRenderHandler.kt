package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

interface ILayerRendering {
    val tooltip_im: IMValue<List<String>?>
    var tooltip: List<String>?
    var tooltipFont: FontRenderer?
    var animator: Animator

    /**
     * Adds animations to [animator]
     */
    fun add(vararg animations: Animation<*>)

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
            GlStateManager.disableLighting()
        }
    }
}

class LayerRenderHandler: ILayerRendering {
    lateinit var layer: GuiLayer

    override val tooltip_im: IMValue<List<String>?> = IMValue()
    override var tooltip: List<String>? by tooltip_im
    override var tooltipFont: FontRenderer? = null

    override var animator: Animator
        get() {
            var a = animatorStorage ?: layer.parent?.animator
            if (a == null) {
                a = Animator()
                animatorStorage = a
            }
            return a
        }
        set(value) {
            animatorStorage = value
        }

    private var animatorStorage: Animator? = null

    /**
     * Adds animations to [animator]
     */
    override fun add(vararg animations: Animation<*>) {
        animator.add(*animations)
    }

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
        layer.clipping.popDisable()

        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
            GlStateManager.glLineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color(.75f, 0f, .75f)
            layer.drawDebugBoundingBox()
        }

        layer.glApplyTransform(true)
    }

    override fun shouldDrawSkeleton(): Boolean = false

    override fun renderSkeleton() {
        layer.runLayoutIfNeeded()

        layer.glApplyTransform(false)

        layer.glApplyContentsOffset(false)

        layer.forEachChild { it.render.renderSkeleton() }

        layer.glApplyContentsOffset(true)

        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox &&
            GuiLayer.isDebugMode && layer.shouldDrawSkeleton()) {
            GlStateManager.glLineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color(.75f, 0f, .75f)
            GL11.glEnable(GL11.GL_LINE_STIPPLE)
            GL11.glLineStipple(2, 0b0011_0011_0011_0011.toShort())
            layer.drawDebugBoundingBox()
            GL11.glDisable(GL11.GL_LINE_STIPPLE)
        }

        layer.glApplyTransform(true)
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
        if(GuiLayer.isDebugMode) {
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
            val d = (Math.PI / 2) / 16

            // top-left
            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(Math.cos(angle) * rad, Math.sin(angle) * rad)
                list.add(vec(rad - diff.x, rad - diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(Math.sin(angle) * rad, Math.cos(angle) * rad)
                list.add(vec(layer.size.x - rad + diff.x, rad - diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(Math.cos(angle) * rad, Math.sin(angle) * rad)
                list.add(vec(layer.size.x - rad + diff.x, layer.size.y - rad + diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(Math.sin(angle) * rad, Math.cos(angle) * rad)
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
