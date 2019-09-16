package com.teamwizardry.librarianlib.features.facade.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.facade.EnumMouseButton
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.math.Vec2d
import kotlin.math.floor
import kotlin.math.sign

/**
 * See [GuiLayerEvents] for a breakdown of when events fire while rendering
 */
object GuiComponentEvents {
    /** Fired when a request is made that this component gain focus. Set [allow] to true to allow this. */
    class RequestFocusEvent(var allow: Boolean = false) : Event()

    /** Fired when a request is made that this component release focus. Set [allow] to false to prevent this. */
    class RequestBlurEvent(var allow: Boolean = true) : Event()

    /** Fired when this component gains focus */
    class FocusEvent() : Event()
    /** Fired when this component loses focus */
    class BlurEvent() : Event()

    /** Called when the mouse position is being calculated to allow event handlers to modify it */
    class CalculateMousePositionEvent(var mousePos: Vec2d): Event()

    /** Fired whenever the mouse is pressed */
    class MouseDownEvent(val button: EnumMouseButton) : Event()

    /** Fired whenever the mouse is released */
    class MouseUpEvent(val button: EnumMouseButton) : Event()

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
    open class MouseClickAnyEvent(val button: EnumMouseButton) : Event()

    /** Fired when the mouse is clicked within the component (mouse goes both down and up inside the component) */
    class MouseClickEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    class MouseClickOutsideEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    class MouseClickDragInEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    class MouseClickDragOutEvent(button: EnumMouseButton) : MouseClickAnyEvent(button)

    /** Fired when a key is pressed */
    class KeyDownEvent(val key: Char, val keyCode: Int) : Event()

    /** Fired when a key is released */
    class KeyUpEvent(val key: Char, val keyCode: Int) : Event()

    /** Fired when a key repeat is triggered */
    class KeyRepeatEvent(val key: Char, val keyCode: Int) : Event()

    /**
     * Fired when the mouse wheel is moved. A negative scroll value indicates that the _content_ should move in the
     * negative Y direction.
     *
     * **NOTE:** *Do not perform discrete actions every time this is fired. See [accumulated] for info.*
     */
    class MouseWheelEvent(
        /**
         * The scroll offset in pixels. This is only the amount scrolled _in this frame,_ so if your scroll operation
         * operates in discrete steps, use [accumulated].
         */
        val amount: Double
    ) : Event() {
        /**
         * The sum of all wheel movements. Before each hook is called, [amount] is added to this value. This value
         * persists independently for each event hook.
         *
         * This value is not reset automatically, so it is up to the event hook to keep it in check. For the best
         * experience when working with discrete scroll steps, subtract your step value instead of setting this to zero.
         * The helper method [consumeStep] is provided to simplify this common operation.
         */
        var accumulated: Double = 0.0

        /**
         * Tests for and "consumes" discrete scroll steps. If the [accumulated] scroll distance exceeds ±[size],
         * this method "consumes" up to [max] steps (or as many as possible if [max] is not positive), and returns
         * the number of steps consumed (a negative count if [accumulated] is negative).
         *
         * A negative [size] inverts the direction, returning a positive count if [accumulated] is negative and vice
         * versa.
         *
         * @param size The step size. Must be nonzero
         * @param max The maximum number of steps to consume, or unlimited if this value is not positive
         * @return the number of steps consumed, negative if reversed
         */
        @JvmOverloads
        fun consumeStep(size: Double, max: Int = 1): Int {
            var count = (accumulated / size).toInt()
            if(max > 0) {
                count = count.clamp(-max, max)
            }
            accumulated -= size * count
            return count
        }

        override fun initializeHookState() {
            accumulated = hookData as Double? ?: 0.0
            accumulated += amount
        }

        override fun finalizeHookState() {
            hookData = accumulated
        }
    }
}
