package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.AddChildEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.AddTagEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.AddToParentEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.BlurEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.ComponentTickEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.DisableEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.EnableEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.FocusEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.GetDataClassesEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.GetDataEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.GetDataKeysEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.HasTagEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.KeyDownEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.KeyUpEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.LogicalSizeEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseClickEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseDownEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseDragEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseInEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseOutEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseOverEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseUpEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.MouseWheelEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.PostDrawEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.PreChildrenDrawEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.PreDrawEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.RemoveChildEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.RemoveDataEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.RemoveFromParentEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.RemoveTagEvent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents.SetDataEvent
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d

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
    class ComponentTickEvent(@JvmField val component: GuiComponent<*>) : Event()

    class PreDrawEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val partialTicks: Float) : Event()
    class PostDrawEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val partialTicks: Float) : Event(true)
    class PreChildrenDrawEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val partialTicks: Float) : Event()

    class MouseDownEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseUpEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseDragEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseClickEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    class KeyDownEvent(@JvmField val component: GuiComponent<*>, val key: Char, val keyCode: Int) : EventCancelable()
    class KeyUpEvent(@JvmField val component: GuiComponent<*>, val key: Char, val keyCode: Int) : EventCancelable()

    class MouseInEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d) : Event()
    class MouseOutEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d) : Event()
    class MouseWheelEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, val direction: MouseWheelDirection) : EventCancelable()
    enum class MouseWheelDirection(@JvmField val ydirection: Int) {
        UP(+1), DOWN(-1);

        companion object {
            @JvmStatic
            fun fromSign(dir: Int): MouseWheelDirection {
                return if (dir >= 0) UP else DOWN
            }
        }
    }

    class FocusEvent(@JvmField val component: GuiComponent<*>) : Event()
    class BlurEvent(@JvmField val component: GuiComponent<*>) : Event()
    class EnableEvent(@JvmField val component: GuiComponent<*>) : Event()
    class DisableEvent(@JvmField val component: GuiComponent<*>) : Event()

    class AddChildEvent(@JvmField val component: GuiComponent<*>, val child: GuiComponent<*>) : EventCancelable()
    class RemoveChildEvent(@JvmField val component: GuiComponent<*>, val child: GuiComponent<*>) : EventCancelable()
    class AddToParentEvent(@JvmField val component: GuiComponent<*>, val parent: GuiComponent<*>) : EventCancelable()
    class RemoveFromParentEvent(@JvmField val component: GuiComponent<*>, val parent: GuiComponent<*>) : EventCancelable()

    class SetDataEvent<D>(@JvmField val component: GuiComponent<*>, val clazz: Class<D>, val key: String, val value: D) : EventCancelable()
    class RemoveDataEvent<D>(@JvmField val component: GuiComponent<*>, val clazz: Class<D>, val key: String, val value: D?) : EventCancelable()
    class GetDataEvent<D>(@JvmField val component: GuiComponent<*>, val clazz: Class<D>, val key: String, val value: D?) : Event()
    class GetDataKeysEvent<D>(@JvmField val component: GuiComponent<*>, val clazz: Class<D>, val value: MutableSet<String>) : Event()
    class GetDataClassesEvent(@JvmField val component: GuiComponent<*>, val value: MutableSet<Class<*>>) : Event()

    class HasTagEvent(@JvmField val component: GuiComponent<*>, val tag: Any, var hasTag: Boolean) : Event()
    class AddTagEvent(@JvmField val component: GuiComponent<*>, val tag: Any) : EventCancelable()
    class RemoveTagEvent(@JvmField val component: GuiComponent<*>, val tag: Any) : EventCancelable()

    class LogicalSizeEvent(@JvmField val component: GuiComponent<*>, var box: BoundingBox2D?) : Event()
    class MouseOverEvent(@JvmField val component: GuiComponent<*>, val mousePos: Vec2d, var isOver: Boolean) : Event()

    class MessageArriveEvent(@JvmField val component: GuiComponent<*>, val from: GuiComponent<*>, val message: Message) : Event()

    data class Message(@JvmField val component: GuiComponent<*>, val data: Any, val rippleType: EnumRippleType)
    enum class EnumRippleType { NONE, UP, DOWN, ALL }
}
