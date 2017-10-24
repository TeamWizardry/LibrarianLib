package com.teamwizardry.librarianlib.features.gui.mixin

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d

class DragMixin(protected var component: GuiComponent, protected var constraints: (Vec2d) -> Vec2d) {

    class DragPickupEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class DragDropEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val button: EnumMouseButton, val previousPos: Vec2d) : EventCancelable()
    class DragMoveEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val clickedPoint: Vec2d, val pos: Vec2d, var newPos: Vec2d, val button: EnumMouseButton) : Event()

    var mouseDown: EnumMouseButton? = null
    var clickPos = Vec2d.ZERO

    init {
        init()
    }

    private fun init() {
        component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            if (mouseDown == null && event.component.mouseOver && !component.BUS.fire(DragPickupEvent(event.component, event.mousePos, event.button)).isCanceled()) {
                mouseDown = event.button
                clickPos = event.mousePos
                event.cancel()
            }
        }
        component.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (mouseDown == event.button && !component.BUS.fire(DragDropEvent(event.component, event.mousePos, event.button, clickPos)).isCanceled()) {
                mouseDown = null
                event.cancel()
            }
        }

        component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) { event ->
            val mouseButton = mouseDown
            if (mouseButton != null) {
                val newPos = constraints(event.component.pos.add(event.mousePos).sub(clickPos))

                if (newPos != event.component.pos) {
                    event.component.pos = event.component.BUS.fire(
                            DragMoveEvent(event.component, event.mousePos, clickPos, event.component.pos, newPos, mouseButton)
                    ).newPos
                }
            }
        }
    }
}
