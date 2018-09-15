package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_LINE_STRIP

interface IComponentRender {
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
     * Draw late stuff this component, like tooltips. This method is executed in the root context
     *
     * @param mousePos Mouse position in the root context
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    fun drawLate(partialTicks: Float)
}

/**
 * TODO: Document file ComponentRenderHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentRenderHandler: IComponentRender {
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
                e.relationships.parent = null
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
        if (layer.mouseOver && hoverCursor != null) {
            cursor = hoverCursor
        }
        GlStateManager.pushMatrix()

        layer.BUS.fire(GuiLayerEvents.PreTransformEvent(partialTicks))

        layer.transform.glApply()

        layer.BUS.fire(GuiLayerEvents.PostTransformEvent(partialTicks))

        layer.clipping.pushEnable()

        layer.BUS.fire(GuiLayerEvents.PreDrawEvent(partialTicks))

        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        layer.draw(partialTicks)

        drawDebugInfo()
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.pushAttrib()

        layer.BUS.fire(GuiLayerEvents.PreChildrenDrawEvent(partialTicks))
        layer.forEachChild { it.renderLayer(partialTicks) }

        GlStateManager.popAttrib()

        layer.BUS.fire(GuiLayerEvents.PostDrawEvent(partialTicks))

        layer.clipping.popDisable()

        GlStateManager.popMatrix()
    }

    private fun drawDebugInfo() {
        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
            GlStateManager.disableTexture2D()
            GlStateManager.color(1f, 0f, 1f)
            if (layer.mouseOverNoOcclusion) GlStateManager.color(0.75f, 0.75f, 0.75f)
            if (layer.mouseOver) GlStateManager.color(1f, 1f, 1f)
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            vb.pos(0.0, 0.0, 0.0).endVertex()
            vb.pos(layer.size.x, 0.0, 0.0).endVertex()
            vb.pos(layer.size.x, layer.size.y, 0.0).endVertex()
            vb.pos(0.0, layer.size.y, 0.0).endVertex()
            vb.pos(0.0, 0.0, 0.0).endVertex()
            tessellator.draw()

            val big = 1000.0
            vb.begin(GL_LINES, DefaultVertexFormats.POSITION)
            vb.pos(0.0, 0.0, 0.0).endVertex()
            vb.pos(0.0, 0.0, -big).endVertex()
            vb.pos(layer.size.x, 0.0, 0.0).endVertex()
            vb.pos(layer.size.x, 0.0, -big).endVertex()
            vb.pos(layer.size.x, layer.size.y, 0.0).endVertex()
            vb.pos(layer.size.x, layer.size.y, -big).endVertex()
            vb.pos(0.0, layer.size.y, 0.0).endVertex()
            vb.pos(0.0, layer.size.y, -big).endVertex()
            tessellator.draw()

            GlStateManager.color(0f, 1f, 1f)
            vb.begin(GL_LINES, DefaultVertexFormats.POSITION)
            vb.pos(layer.mousePos.x, layer.mousePos.y, 0.0).endVertex()
            vb.pos(layer.mousePos.x, layer.mousePos.y, big).endVertex()
            tessellator.draw()
        }
    }


    /**
     * Draw late stuff this component, like tooltips. This method is executed in the root context
     *
     * @param mousePos Mouse position in the root context
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    override fun drawLate(partialTicks: Float) {
        if (!layer.isVisible) return
        if (layer.mouseOver) {
            val tt = tooltip
            if (tt?.isNotEmpty() == true) {
                val rootPos = layer.thisPosToOtherContext(null, layer.mousePos)
                GuiUtils.drawHoveringText(tt, rootPos.xi, rootPos.yi, layer.root.size.xi, layer.root.size.yi, -1,
                        tooltipFont ?: Minecraft.getMinecraft().fontRenderer)
            }
        }

        layer.forEachChild { it.drawLate(partialTicks) }
    }

}
