package com.teamwizardry.librarianlib.features.gui.mixin

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d

class DragMixin(protected var component: GuiComponent, protected var constraints: (Vec2d) -> Vec2d) {

    /**
     * Called when the component is picked up
     *
     * @property component The component this mixin is for
     * @property button The button clicked
     * @property mousePos The position of the mouse in the component
     */
    class DragPickupEvent(@JvmField val component: GuiComponent, val button: EnumMouseButton, val mousePos: Vec2d) : EventCancelable()
    /**
     * Called when the component is dropped up
     *
     * @property component The component this mixin is for
     * @property button The button released
     * @property previousPos The position of the component when it was picked up
     */
    class DragDropEvent(@JvmField val component: GuiComponent, val button: EnumMouseButton, val previousPos: Vec2d) : EventCancelable()
    /**
     * Called when the component is about to move
     *
     * @property component The component this mixin is for
     * @property button The button released
     * @property pos The current component position
     * @property newPos What the component's position will be set to after this event
     */
    class DragMoveEvent(@JvmField val component: GuiComponent, val button: EnumMouseButton, val pos: Vec2d, var newPos: Vec2d) : Event()

    var mouseDown: EnumMouseButton? = null
    var dragOffset = Vec2d.ZERO
    var previousPos = Vec2d.ZERO

    init {
        init()
    }

    private fun init() {
        component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            if (mouseDown == null && event.component.mouseOver && !component.BUS.fire(DragPickupEvent(event.component, event.button, event.mousePos)).isCanceled()) {
                mouseDown = event.button
                dragOffset = event.component.transformToParentContext(event.mousePos) - event.component.pos
                previousPos = event.component.pos
                event.cancel()
            }
        }
        component.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (mouseDown == event.button && !component.BUS.fire(DragDropEvent(event.component, event.button, previousPos)).isCanceled()) {
                mouseDown = null
                event.cancel()
            }
        }

        component.BUS.hook(GuiComponentEvents.PreMouseOverEvent::class.java) { event ->
            val mouseButton = mouseDown
            if (mouseButton != null) {
                val newPos = constraints(event.parentMousePos - dragOffset)

                if (newPos != event.component.pos) {
                    event.component.pos = event.component.BUS.fire(
                            DragMoveEvent(event.component, mouseButton, event.component.pos, newPos)
                    ).newPos
                }
            }
        }
    }
}
