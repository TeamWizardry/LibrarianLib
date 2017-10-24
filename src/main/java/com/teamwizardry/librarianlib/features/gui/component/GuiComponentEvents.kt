package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.math.BoundingBox2D

/**
 * ## Default events
 *
 * ### Common Events
 * - [ComponentTickEvent] - Fired each tick while the component is a part of a screen
 * - [PreDrawEvent] - Fired each frame before the component has been drawn
 * - [PreChildrenDrawEvent] - Fired each frame after the component has been drawn but before children have been drawn
 * - [PostDrawEvent] - Fired each frame after the component and its children have been drawn
 * - ---
 * - [MouseDownEvent] - Fired whenever the mouse is pressed
 * - [MouseUpEvent] - Fired whenever the mouse is released
 * - [MouseDragEvent] - Fired whenever the mouse is moved while a button is being pressed
 * - [MouseClickEvent] - Fired when the mouse is clicked within the component
 * - ---
 * - [KeyDownEvent] - Fired when a key is pressed
 * - [KeyUpEvent] - Fired when a key is released
 * - ---
 * - [MouseInEvent] - Fired when the mouse is moved into the component
 * - [MouseOutEvent] - Fired when the mouse is moved out of the component
 * - [MouseWheelEvent] - Fired when the mouse wheel is moved
 * - ---
 * - [FocusEvent] - Fired when the component gains focus
 * - [BlurEvent] - Fired when the component loses focus
 * - ---
 * - [EnableEvent] - Fired when this component is enabled
 * - [DisableEvent] - Fired when this component is disabled
 *
 * ### Seldom used events
 * - [AddChildEvent] - Fired before a child is added to the component
 * - [RemoveChildEvent] - Fired when a child is removed from the component
 * - [AddToParentEvent] - Fired when the component is added as a child to another component
 * - [RemoveFromParentEvent] - Fired when the component is removed from its parent
 * - ---
 * - [SetDataEvent] - Fired before data is set
 * - [RemoveDataEvent] - Fired before data is removed
 * - [GetDataEvent] - Fired when data is queried
 * - [GetDataKeysEvent] - Fired when the data key set is queried
 * - [GetDataClassesEvent] - Fired when the data class set is queried
 * - ---
 * - [HasTagEvent] - Fired when the component is checked for a tag
 * - [AddTagEvent] - Fired before a tag is added to the component
 * - [RemoveTagEvent] - Fired before a tag is removed from a component
 *
 * ### Advanced events
 * - [LogicalSizeEvent] - Fired when the logical size is queried
 * - [MouseOverEvent] - Fired when checking if the mouse is over this component
 */
object GuiComponentEvents {
    class ComponentTickEvent<T : GuiComponent<T>>(val component: T) : Event()

    class PreDrawEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val partialTicks: Float) : Event()
    class PostDrawEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val partialTicks: Float) : Event(true)
    class PreChildrenDrawEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val partialTicks: Float) : Event()

    class MouseDownEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseUpEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseDragEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseClickEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    class KeyDownEvent<T : GuiComponent<T>>(val component: T, val key: Char, val keyCode: Int) : EventCancelable()
    class KeyUpEvent<T : GuiComponent<T>>(val component: T, val key: Char, val keyCode: Int) : EventCancelable()

    class MouseInEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d) : Event()
    class MouseOutEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d) : Event()
    class MouseWheelEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val direction: MouseWheelDirection) : EventCancelable()
    enum class MouseWheelDirection(@JvmField val ydirection: Int) {
        UP(+1), DOWN(-1);

        companion object {
            @JvmStatic
            fun fromSign(dir: Int): MouseWheelDirection {
                return if (dir >= 0) UP else DOWN
            }
        }
    }

    class FocusEvent<T : GuiComponent<T>>(val component: T) : Event()
    class BlurEvent<T : GuiComponent<T>>(val component: T) : Event()
    class EnableEvent<T : GuiComponent<T>>(val component: T) : Event()
    class DisableEvent<T : GuiComponent<T>>(val component: T) : Event()

    class AddChildEvent<T : GuiComponent<T>>(val component: T, val child: GuiComponent<*>) : EventCancelable()
    class RemoveChildEvent<T : GuiComponent<T>>(val component: T, val child: GuiComponent<*>) : EventCancelable()
    class AddToParentEvent<out T : GuiComponent<*>>(val component: T, val parent: GuiComponent<*>) : EventCancelable()
    class RemoveFromParentEvent<out T : GuiComponent<*>>(val component: T, val parent: GuiComponent<*>) : EventCancelable()

    class SetDataEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val key: String, val value: D) : EventCancelable()
    class RemoveDataEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val key: String, val value: D?) : EventCancelable()
    class GetDataEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val key: String, val value: D?) : Event()
    class GetDataKeysEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val value: MutableSet<String>) : Event()
    class GetDataClassesEvent<T : GuiComponent<T>>(val component: T, val value: MutableSet<Class<*>>) : Event()

    class HasTagEvent<T : GuiComponent<T>>(val component: T, val tag: Any, var hasTag: Boolean) : Event()
    class AddTagEvent<T : GuiComponent<T>>(val component: T, val tag: Any) : EventCancelable()
    class RemoveTagEvent<T : GuiComponent<T>>(val component: T, val tag: Any) : EventCancelable()

    class LogicalSizeEvent<T : GuiComponent<T>>(val component: T, var box: BoundingBox2D?) : Event()
    class MouseOverEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, var isOver: Boolean) : Event()

    class MessageArriveEvent<T : GuiComponent<T>>(val component: T, val from: GuiComponent<*>, val message: Message) : Event()

    data class Message(val component: GuiComponent<*>, val data: Any, val rippleType: EnumRippleType)
    enum class EnumRippleType { NONE, UP, DOWN, ALL }
}
