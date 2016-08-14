package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.HandlerList
import com.teamwizardry.librarianlib.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.math.BoundingBox2D
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

class ComponentScrolledView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentScrolledView>(posX, posY, width, height) {

    val scroll = HandlerList<IScrollEvent<ComponentScrolledView>>()

    protected var offset = Vec2d.ZERO

    init {
        ScissorMixin.scissor(this)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // noop
    }

    override fun transformChildPos(child: GuiComponent<*>, pos: Vec2d): Vec2d {
        return super.transformChildPos(child, pos).add(offset)
    }

    fun scrollTo(scroll: Vec2d) {
        var scroll = scroll
        scroll = Vec2d.min(maxScroll, scroll)
        if (scroll != offset) {
            this.scroll.fireModifier<Vec2d>(scroll, { h, v -> h.handle(this, offset, v) })
            offset = scroll
        }
    }

    fun scrollOffset(scroll: Vec2d) {
        scrollTo(offset.add(scroll))
    }

    fun scrollToPercent(scroll: Vec2d) {
        scrollTo(maxScroll.mul(scroll))
    }

    override fun getLogicalSize(): BoundingBox2D {
        return contentSize
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        GlStateManager.translate(-offset.x, -offset.y, 0.0)
        super.draw(mousePos, partialTicks)
        GlStateManager.translate(offset.x, offset.y, 0.0)
    }

    val maxScroll: Vec2d
        get() = super.getLogicalSize().max.sub(pos).sub(size)

    @FunctionalInterface
    interface IScrollEvent<T> {
        fun handle(component: T, oldScroll: Vec2d, newScroll: Vec2d): Vec2d
    }

}
