package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.HandlerList
import com.teamwizardry.librarianlib.client.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

open class ComponentScrolledView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentScrolledView>(posX, posY, width, height) {

    val scroll = HandlerList<(ComponentScrolledView, Vec2d, Vec2d?) -> Vec2d?>()

    protected var offset = Vec2d.ZERO

    init {
        ScissorMixin.scissor(this)
        BUS.hook(ChildMouseOffsetEvent::class.java) { event ->
            event.offset = event.offset.add(offset)
        }

        BUS.hook(LogicalSizeEvent::class.java) { event ->
            if (event.box != null)
                event.box = contentSize
        }
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

    fun scrollTo(scroll: Vec2d) {
        val newScroll = Vec2d.min(maxScroll, scroll)
        if (newScroll != offset) {
            this.scroll.fireModifier(newScroll, { h, v -> h(this, offset, v) })
            offset = newScroll
        }
    }

    fun scrollOffset(scroll: Vec2d) {
        scrollTo(offset.add(scroll))
    }

    fun scrollToPercent(scroll: Vec2d) {
        scrollTo(maxScroll.mul(scroll))
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        GlStateManager.translate(-offset.x, -offset.y, 0.0)
        super.draw(mousePos, partialTicks)
        GlStateManager.translate(offset.x, offset.y, 0.0)
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
