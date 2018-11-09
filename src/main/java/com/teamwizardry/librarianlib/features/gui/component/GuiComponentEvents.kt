package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.math.Vec2d

object GuiComponentEvents {
    /** Fired each tick while the component is a part of a screen */
    class ComponentTickEvent : Event()

    /** Called when the mouse position is being calculated to allow event handlers to modify it */
    class CalculateMousePositionEvent(var mousePos: Vec2d): Event()

    /** Fired whenever the mouse is pressed */
    class MouseDownEvent(val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is released */
    class MouseUpEvent(val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is moved while a button is being pressed */
    class MouseMoveEvent() : Event()

    /** Fired whenever the mouse is moved while a button is being pressed */
    class MouseDragEvent() : Event()

    /** Fired whenever the mouse is moved out of this component (when mouseInside goes from true to false) */
    class MouseMoveOutEvent() : Event()

    /** Fired whenever the mouse is moved into this component (when mouseInside goes from false to true) */
    class MouseMoveInEvent() : Event()

    /** Fired whenever the mouse is moved off this component (when mouseOver goes from true to false) */
    class MouseEnterEvent() : Event()

    /** Fired whenever the mouse is moved over this component (when mouseOver goes from false to true) */
    class MouseLeaveEvent() : Event()


    /** Fired in addition to any of [MouseClickEvent], [MouseClickOutsideEvent], [MouseClickDragInEvent], or [MouseClickDragOutEvent] */
    open class MouseClickAnyEvent(val button: EnumMouseButton) : EventCancelable()

    /** Fired when the mouse is clicked within the component (mouse goes both down and up inside the component) */
    class MouseClickEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    /** Fired when the mouse is clicked outside the component (mouse goes both down and up outside the component) */
    class MouseClickOutsideEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    /** Fired when the mouse is clicked within the component (mouse goes down outside and up inside the component) */
    class MouseClickDragInEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    /** Fired when the mouse is clicked within the component (mouse goes down inside and up outside the component) */
    class MouseClickDragOutEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    /** Fired when a key is pressed */
    class KeyDownEvent(val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when a key is released */
    class KeyUpEvent(val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when the mouse wheel is moved */
    class MouseWheelEvent(val direction: MouseWheelDirection) : EventCancelable()

    enum class MouseWheelDirection(@JvmField val ydirection: Int) {
        UP(+1), DOWN(-1);

        companion object {
            @JvmStatic
            fun fromSign(dir: Int): MouseWheelDirection {
                return if (dir >= 0) UP else DOWN
            }
        }
    }
}
