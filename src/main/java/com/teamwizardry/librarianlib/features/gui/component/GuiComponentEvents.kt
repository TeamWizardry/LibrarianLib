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
    class MouseDownEvent(val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is released */
    class MouseUpEvent(val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is moved while a button is being pressed */
    class MouseDragEvent(val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is moved out of this component (when mouseInside goes from true to false) */
    class MouseMoveOutEvent(val previousMousePos: Vec2d, val mousePos: Vec2d) : Event()

    /** Fired whenever the mouse is moved into this component (when mouseInside goes from false to true) */
    class MouseMoveInEvent(val previousMousePos: Vec2d, val mousePos: Vec2d) : Event()

    /** Fired whenever the mouse is moved off this component (when mouseOver goes from true to false) */
    class MouseEnterEvent(val previousMousePos: Vec2d, val mousePos: Vec2d) : Event()

    /** Fired whenever the mouse is moved over this component (when mouseOver goes from false to true) */
    class MouseLeaveEvent(val previousMousePos: Vec2d, val mousePos: Vec2d) : Event()


    /** Fired in addition to any of [MouseClickEvent], [MouseClickOutsideEvent], [MouseClickDragInEvent], or [MouseClickDragOutEvent] */
    open class MouseClickAnyEvent(val mouseDownPos: Vec2d, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired when the mouse is clicked within the component (mouse goes both down and up inside the component) */
    class MouseClickEvent(mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(mouseDownPos, mousePos, button)

    /** Fired when the mouse is clicked outside the component (mouse goes both down and up outside the component) */
    class MouseClickOutsideEvent(mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(mouseDownPos, mousePos, button)

    /** Fired when the mouse is clicked within the component (mouse goes down outside and up inside the component) */
    class MouseClickDragInEvent(mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(mouseDownPos, mousePos, button)

    /** Fired when the mouse is clicked within the component (mouse goes down inside and up outside the component) */
    class MouseClickDragOutEvent(mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(mouseDownPos, mousePos, button)

    /** Fired when a key is pressed */
    class KeyDownEvent(val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when a key is released */
    class KeyUpEvent(val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when the mouse wheel is moved */
    class MouseWheelEvent(val mousePos: Vec2d, val direction: MouseWheelDirection) : EventCancelable()

    enum class MouseWheelDirection(@JvmField val ydirection: Int) {
        UP(+1), DOWN(-1);

        companion object {
            @JvmStatic
            fun fromSign(dir: Int): MouseWheelDirection {
                return if (dir >= 0) UP else DOWN
            }
        }
    }

    /** Fired before data is set */
    class SetDataEvent<D>(val clazz: Class<D>, val key: String, val value: D) : EventCancelable()

    /** Fired before data is removed */
    class RemoveDataEvent<D>(val clazz: Class<D>, val key: String, val value: D?) : EventCancelable()

    /** Fired when data is queried */
    class GetDataEvent<D>(val clazz: Class<D>, val key: String, val value: D?) : Event()

    /** Fired when the data key set is queried */
    class GetDataKeysEvent<D>(val clazz: Class<D>, val value: MutableSet<String>) : Event()

    /** Fired when the data class set is queried */
    class GetDataClassesEvent(val value: MutableSet<Class<*>>) : Event()

    /** Fired when the component is checked for a tag */
    class HasTagEvent(val tag: Any, var hasTag: Boolean) : Event()

    /** Fired before a tag is added to the component */
    class AddTagEvent(val tag: Any) : EventCancelable()

    /** Fired before a tag is removed from a component */
    class RemoveTagEvent(val tag: Any) : EventCancelable()
}
