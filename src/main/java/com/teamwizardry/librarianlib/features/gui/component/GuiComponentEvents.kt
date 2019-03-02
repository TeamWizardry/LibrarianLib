package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.math.Vec2d

object GuiComponentEvents {
    /** Fired when a request is made that this component gain focus. Set [allow] to true to allow this. */
    class RequestFocusEvent(var allow: Boolean = false) : Event()

    /** Fired when a request is made that this component release focus. Set [allow] to false to prevent this. */
    class RequestBlurEvent(var allow: Boolean = true) : Event()

    /** Fired when this component gains focus */
    class FocusEvent() : Event()
    /** Fired when this component loses focus */
    class BlurEvent() : Event()

    /** Fired each tick while the component is a part of a screen */
    class ComponentTickEvent : Event()

    /** Fired each frame after input has been processed and before the GUI renders */
    class ComponentUpdateEvent : Event()

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

    /** Fired whenever the mouse is moved out of this component (when mouseHit goes from nonnull to null) */
    class MouseMoveOutEvent() : Event()
    /** Fired whenever the mouse is moved out of this component (when mouseHit goes from nonnull to null) and one or
     * more buttons are pressed */
    class MouseDragOutEvent() : Event()

    /** Fired whenever the mouse is moved into this component (when mouseHit goes from null to nonnull) */
    class MouseMoveInEvent() : Event()
    /** Fired whenever the mouse is moved into this component (when mouseHit goes from null to nonnull) and one or
     * more buttons are pressed*/
    class MouseDragInEvent() : Event()

    /** Fired whenever the mouse is moved over this component (when mouseOver goes from false to true) */
    class MouseEnterEvent() : Event()
    /** Fired whenever the mouse is moved over this component (when mouseOver goes from false to true) and one or more
     * buttons are pressed */
    class MouseDragEnterEvent() : Event()

    /** Fired whenever the mouse is moved off this component (when mouseOver goes from true to false) */
    class MouseLeaveEvent() : Event()
    /** Fired whenever the mouse is moved off this component (when mouseOver goes from true to false) and one or more
    * buttons are pressed */
    class MouseDragLeaveEvent() : Event()

    /** Fired in addition to any of [MouseClickEvent], [MouseClickOutsideEvent], [MouseClickDragInEvent], or [MouseClickDragOutEvent] */
    open class MouseClickAnyEvent(val button: EnumMouseButton) : EventCancelable()

    /** Fired when the mouse is clicked within the component (mouse goes both down and up inside the component) */
    class MouseClickEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    class MouseClickOutsideEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    class MouseClickDragInEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    class MouseClickDragOutEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    /** Fired when a key is pressed */
    class KeyDownEvent(val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when a key is released */
    class KeyUpEvent(val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when a key repeat is triggered */
    class KeyRepeatEvent(val key: Char, val keyCode: Int) : EventCancelable()

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
