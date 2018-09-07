package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.gui.IMValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d
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
     * Draw this component, don't override in subclasses unless you know what you're doing.
     *
     * @param mousePos Mouse position relative to the position of this component
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    fun draw(mousePos: Vec2d, partialTicks: Float)

    /**
     * Draw late stuff this component, like tooltips. This method is executed in the root context
     *
     * @param mousePos Mouse position in the root context
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    fun drawLate(mousePos: Vec2d, partialTicks: Float)
}

/**
 * TODO: Document file ComponentRenderHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentRenderHandler: IComponentRender {
    lateinit var component: GuiComponent

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
            val parent = component.parent
            if (parent == null)
                return field
            else
                return parent.cursor
        }
        set(value) {
            val parent = component.parent
            if (parent == null)
                field = value
            else
                parent.cursor = value
        }

    override var animator: Animator
        get() {
            var a = animatorStorage ?: component.parent?.animator
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

    private var wasMouseOver = false

    /**
     * Adds animations to [animator]
     */
    override fun add(vararg animations: Animation<*>) {
        animator.add(*animations)
    }

    /**
     * Draw this component, don't override in subclasses unless you know what you're doing.
     *
     * @param mousePos Mouse position relative to the position of this component
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        val transformedPos = component.transformFromParentContext(mousePos)
        val components = component.relationships.components
        components.sortBy { it.zIndex }
        if (!component.isVisible) return

        components.removeAll { e ->
            var b = e.isInvalid
            if (component.BUS.fire(GuiComponentEvents.RemoveChildEvent(component, e)).isCanceled())
                b = false
            if (e.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(e, component)).isCanceled())
                b = false
            if (b) {
                e.relationships.parent = null
            }
            b
        }

        if (wasMouseOver != component.mouseOver) {
            if (component.mouseOver) {
                component.BUS.fire(GuiComponentEvents.MouseInEvent(component, transformedPos))
            } else {
                component.BUS.fire(GuiComponentEvents.MouseOutEvent(component, transformedPos))
            }
        }
        wasMouseOver = component.mouseOver
        if (component.mouseOver && hoverCursor != null) {
            cursor = hoverCursor
        }

        component.BUS.fire(GuiComponentEvents.PreTransformEvent(component, transformedPos, partialTicks))

        GlStateManager.pushMatrix()

        component.transform.glApply()

        component.clipping.pushEnable()

        component.BUS.fire(GuiComponentEvents.PreDrawEvent(component, transformedPos, partialTicks))

        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        component.drawComponent(transformedPos, partialTicks)

        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
            GlStateManager.disableTexture2D()
            GlStateManager.color(1f, 0f, 1f)
            if (component.mouseOverNoOcclusion) GlStateManager.color(0.75f, 0.75f, 0.75f)
            if (component.mouseOver) GlStateManager.color(1f, 1f, 1f)
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            vb.pos(0.0, 0.0, 0.0).endVertex()
            vb.pos(component.size.x, 0.0, 0.0).endVertex()
            vb.pos(component.size.x, component.size.y, 0.0).endVertex()
            vb.pos(0.0, component.size.y, 0.0).endVertex()
            vb.pos(0.0, 0.0, 0.0).endVertex()
            tessellator.draw()

            val big = 1000.0
            vb.begin(GL_LINES, DefaultVertexFormats.POSITION)
            vb.pos(0.0, 0.0, 0.0).endVertex()
            vb.pos(0.0, 0.0, -big).endVertex()
            vb.pos(component.size.x, 0.0, 0.0).endVertex()
            vb.pos(component.size.x, 0.0, -big).endVertex()
            vb.pos(component.size.x, component.size.y, 0.0).endVertex()
            vb.pos(component.size.x, component.size.y, -big).endVertex()
            vb.pos(0.0, component.size.y, 0.0).endVertex()
            vb.pos(0.0, component.size.y, -big).endVertex()
            tessellator.draw()

            GlStateManager.color(0f, 1f, 1f)
            vb.begin(GL_LINES, DefaultVertexFormats.POSITION)
            vb.pos(transformedPos.x, transformedPos.y, 0.0).endVertex()
            vb.pos(transformedPos.x, transformedPos.y, big).endVertex()
            tessellator.draw()
        }
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.pushAttrib()

        component.BUS.fire(GuiComponentEvents.PreChildrenDrawEvent(component, transformedPos, partialTicks))
        component.forEachChild { it.draw(transformedPos, partialTicks) }

        GlStateManager.popAttrib()

        component.BUS.fire(GuiComponentEvents.PostDrawEvent(component, transformedPos, partialTicks))

        component.clipping.popDisable()

        GlStateManager.popMatrix()
    }

    /**
     * Draw late stuff this component, like tooltips. This method is executed in the root context
     *
     * @param mousePos Mouse position in the root context
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    override fun drawLate(mousePos: Vec2d, partialTicks: Float) {
        if (!component.isVisible) return
        if (component.mouseOver) {
            val tt = tooltip
            if (tt?.isNotEmpty() == true) {
                GuiUtils.drawHoveringText(tt, mousePos.xi, mousePos.yi, component.root.size.xi, component.root.size.yi, -1,
                        tooltipFont ?: Minecraft.getMinecraft().fontRenderer)
            }
        }

        component.forEachChild { it.drawLate(mousePos, partialTicks) }
    }

}
