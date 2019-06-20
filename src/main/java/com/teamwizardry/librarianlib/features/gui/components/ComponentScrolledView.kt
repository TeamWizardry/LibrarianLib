package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.HandlerList
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * ## Facade equivalent: [ScrollPane][com.teamwizardry.librarianlib.features.facade.provided.pastry.components.ScrollPane]
 */
//@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentScrolledView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    val scroll = HandlerList<(ComponentScrolledView, Vec2d, Vec2d?) -> Vec2d?>()

    init {
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

    /**
     * Moves the view to a specific transform.postTranslate.
     */
    fun scrollTo(scroll: Vec2d) {
        val newScroll = Vec2d.min(maxScroll, scroll)
        if (newScroll != transform.postTranslate) {
            this.scroll.fireModifier(newScroll, { h, v -> h(this, transform.postTranslate, v) })
            transform.postTranslate = newScroll
        }
    }

    /**
     * Moves the view by the passed vector.
     */
    fun scrollOffset(scroll: Vec2d) {
        scrollTo(transform.postTranslate.add(scroll))
    }

    /**
     * Moves the view to a specified value (0-1) based upon the size of the contained components.
     */
    fun scrollToPercent(scroll: Vec2d) {
        scrollTo(maxScroll.mul(scroll))
    }

    val maxScroll: Vec2d
        get() {
            return vec(0, 0)
        }

    @FunctionalInterface
    interface IScrollEvent<T> {
        fun handle(component: T, oldScroll: Vec2d, newScroll: Vec2d): Vec2d
    }

}
