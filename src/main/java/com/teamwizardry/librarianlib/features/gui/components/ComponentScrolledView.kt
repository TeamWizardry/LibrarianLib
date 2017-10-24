package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.HandlerList
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

class ComponentScrolledView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentScrolledView>(posX, posY, width, height) {

    val scroll = HandlerList<(ComponentScrolledView, Vec2d, Vec2d?) -> Vec2d?>()

    init {
        ScissorMixin.scissor(this)

        BUS.hook(GuiComponentEvents.LogicalSizeEvent::class.java) { event ->
            if (event.box != null)
                event.box = contentSize
        }
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

    /**
     * Moves the view to a specific childTranslation.
     */
    fun scrollTo(scroll: Vec2d) {
        val newScroll = Vec2d.min(maxScroll, scroll)
        if (newScroll != childTranslation) {
            this.scroll.fireModifier(newScroll, { h, v -> h(this, childTranslation, v) })
            childTranslation = newScroll
        }
    }

    /**
     * Moves the view by the passed vector.
     */
    fun scrollOffset(scroll: Vec2d) {
        scrollTo(childTranslation.add(scroll))
    }

    /**
     * Moves the view to a specified value (0-1) based upon the size of the contained components.
     */
    fun scrollToPercent(scroll: Vec2d) {
        scrollTo(maxScroll.mul(scroll))
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        GlStateManager.translate(-childTranslation.x, -childTranslation.y, 0.0)
        super.draw(mousePos, partialTicks)
        GlStateManager.translate(childTranslation.x, childTranslation.y, 0.0)
    }

    val maxScroll: Vec2d
        get() {
            var l = super.getLogicalSize()
            if (l == null)
                return Vec2d.ZERO
            return l.max.sub(pos).sub(size)
        }

    @FunctionalInterface
    interface IScrollEvent<T> {
        fun handle(component: T, oldScroll: Vec2d, newScroll: Vec2d): Vec2d
    }

}
