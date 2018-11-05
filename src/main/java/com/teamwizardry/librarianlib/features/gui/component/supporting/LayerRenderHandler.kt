package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
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
    /**
     * If nonnull, the cursor will switch to this when hovering.
     */
    val hoverCursor_im: IMValue<LibCursor?>
    var hoverCursor: LibCursor?
    var cursor: LibCursor?
    var animator: Animator

    /**
     * Adds animations to [animator]
     */
    fun add(vararg animations: Animation<*>)

    /**
     * Cleans up invalid layers. Runs before [updateMouseBeforeRender].
     */
    fun cleanUpChildren()

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

    /**
     * Draw a bounding box around
     */
    fun drawDebugBoundingBox()
}

class LayerRenderHandler: ILayerRendering {
    lateinit var layer: GuiLayer

    override val tooltip_im: IMValue<List<String>?> = IMValue()
    override var tooltip: List<String>? by tooltip_im
    override var tooltipFont: FontRenderer? = null
    /**
     * If nonnull, the cursor will switch to this when hovering.
     */
    override val hoverCursor_im: IMValue<LibCursor?> = IMValue()
    override var hoverCursor: LibCursor? by hoverCursor_im

    override var cursor: LibCursor? = null
        get() {
            val parent = layer.parent
            if (parent == null)
                return field
            else
                return parent.cursor
        }
        set(value) {
            val parent = layer.parent
            if (parent == null)
                field = value
            else
                parent.cursor = value
        }

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

    override fun cleanUpChildren() {
        val components = layer.relationships.subLayers
        components.removeAll { e ->
            var b = e.isInvalid
            e.clearInvalid()
            if (!b) return@removeAll false
            if (layer.BUS.fire(GuiLayerEvents.RemoveChildEvent(e)).isCanceled())
                b = false
            if (e.BUS.fire(GuiLayerEvents.RemoveFromParentEvent(layer)).isCanceled())
                b = false
            if (b) {
                e.parent = null
            }
            return@removeAll b
        }

        components.forEach {
            it.cleanUpChildren()
        }
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
        if(layer.needsLayout) {
            layer.layoutChildren()
            layer.BUS.fire(GuiLayerEvents.LayoutChildren())
            layer.needsLayout = false
        }

        if(!layer.isVisible) return;

        layer.BUS.fire(GuiLayerEvents.PreTransformEvent(partialTicks))

        layer.glApplyTransform(false)

        layer.BUS.fire(GuiLayerEvents.PostTransformEvent(partialTicks))

        layer.clipping.pushEnable()

        layer.glApplyContentsOffset(false)

        GlStateManager.pushMatrix()
        layer.BUS.fire(GuiLayerEvents.PreDrawEvent(partialTicks))

        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        layer.draw(partialTicks)
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.popMatrix()

        layer.BUS.fire(GuiLayerEvents.PreChildrenDrawEvent(partialTicks))
        layer.forEachChild { it.renderLayer(partialTicks) }

        GlStateManager.pushMatrix()
        layer.BUS.fire(GuiLayerEvents.PostDrawEvent(partialTicks))
        GlStateManager.popMatrix()

        layer.glApplyContentsOffset(true)
        layer.clipping.popDisable()

        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
            GlStateManager.glLineWidth(1f)
            GlStateManager.color(.75f, 0f, .75f)
            layer.drawDebugBoundingBox()
        }

        layer.glApplyTransform(true)
    }

    override fun drawDebugBoundingBox() {
        GlStateManager.disableTexture2D()
        val points = createBoundingBoxPoints()
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

    private fun createBoundingBoxPoints(): List<Vec2d> {
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
