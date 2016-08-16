package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.gui.EnumMouseButton
import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.HandlerList
import com.teamwizardry.librarianlib.math.Vec2d

import java.util.function.Function

class DragMixin<T : GuiComponent<*>>(protected var component: T, protected var constraints: (Vec2d) -> Vec2d) {

    val pickup = HandlerList<IDragCancelableEvent<T>>()
    val drop = HandlerList<IDragCancelableEvent<T>>()
    val drag = HandlerList<IDragEvent<T>>()
    var mouseDown = false
    var clickPos = Vec2d.ZERO

    init {
        init()
    }

    private fun init() {
        component.mouseDown.add { c, pos, button ->
            if (!mouseDown && c.isMouseOver(pos) && !pickup.fireCancel { h -> h(component, button, pos) }) {
                mouseDown = true
                clickPos = pos
                return@add true
            }
            false
        }
        component.mouseUp.add({ c, pos, button ->
            if (mouseDown && !drop.fireCancel { h -> h.handle(component, button, pos) })
                mouseDown = false
            false
        })
        component.preDraw.add({ c, pos, partialTicks ->
            if (mouseDown) {
                val newPos = constraints.apply(c.pos.add(pos).sub(clickPos))

                if (newPos != c.pos) {
                    c.pos = newPos
                    drag.fireAll { h -> h.handle(component, newPos) }
                }
            }
        })
    }

    @FunctionalInterface
    interface IDragEvent<T> {
        fun handle(component: T, pos: Vec2d)
    }

    @FunctionalInterface
    interface IDragCancelableEvent<T> {
        fun handle(component: T, button: EnumMouseButton, pos: Vec2d): Boolean
    }
}
