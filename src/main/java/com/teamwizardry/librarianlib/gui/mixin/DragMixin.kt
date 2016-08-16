package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.gui.EnumMouseButton
import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.HandlerList
import com.teamwizardry.librarianlib.math.Vec2d

import java.util.function.Function

class DragMixin<T : GuiComponent<T>>(protected var component: T, protected var constraints: (Vec2d) -> Vec2d) {

    val pickup = HandlerList<(T, EnumMouseButton, Vec2d) -> Boolean>()
    val drop = HandlerList<(T, EnumMouseButton, Vec2d) -> Boolean>()
    val drag = HandlerList<(T, Vec2d) -> Unit>()
    var mouseDown = false
    var clickPos = Vec2d.ZERO

    init {
        init()
    }

    private fun init() {
        component.mouseDown.add { c, pos, button ->
            if (!mouseDown && c.isMouseOver(pos) && !pickup.fireCancel { it(component, button, pos) }) {
                mouseDown = true
                clickPos = pos
                return@add true
            }
            false
        }
        component.mouseUp.add({ c, pos, button ->
            if (mouseDown && !drop.fireCancel { it(component, button, pos) })
                mouseDown = false
            false
        })
        component.preDraw.add({ c, pos, partialTicks ->
            if (mouseDown) {
                val newPos = constraints(c.pos.add(pos).sub(clickPos))

                if (newPos != c.pos) {
                    c.pos = newPos
                    drag.fireAll { it(component, newPos) }
                }
            }
        })
    }
}
