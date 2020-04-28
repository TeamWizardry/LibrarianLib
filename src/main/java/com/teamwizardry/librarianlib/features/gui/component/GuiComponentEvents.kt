package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * ## Facade equivalent: [GuiComponentEvents][com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents]
 */
@Deprecated("As of version 4.20 this has been superseded by Facade")
object GuiComponentEvents {
    /** Fired each tick while the component is a part of a screen */
    class ComponentTickEvent(@JvmField val component: GuiComponent) : Event()

    /** Fired each frame before applying OpenGL transforms */
    class PreTransformEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val partialTicks: Float) : Event()

    /** Fired each frame before the component has been drawn, but after most computed properties update */
    class PreDrawEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val partialTicks: Float) : Event()

    /** Fired each frame after the component has been drawn but before children have been drawn */
    class PreChildrenDrawEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val partialTicks: Float) : Event()

    /** Fired each frame after the component and its children have been drawn */
    class PostDrawEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val partialTicks: Float) : Event(true)

    /** Fired whenever the mouse is pressed */
    class MouseDownEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is released */
    class MouseUpEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired whenever the mouse is moved while a button is being pressed */
    class MouseDragEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired in addition to any of [MouseClickEvent], [MouseClickOutsideEvent], [MouseClickDragInEvent], or [MouseClickDragOutEvent] */
    open class MouseClickAnyEvent(@JvmField val component: GuiComponent, val mouseDownPos: Vec2d, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    /** Fired when the mouse is clicked within the component (mouse goes both down and up inside the component) */
    class MouseClickEvent(component: GuiComponent, mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(component, mouseDownPos, mousePos, button)

    /** Fired when the mouse is clicked outside the component (mouse goes both down and up outside the component) */
    class MouseClickOutsideEvent(component: GuiComponent, mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(component, mouseDownPos, mousePos, button)

    /** Fired when the mouse is clicked within the component (mouse goes down outside and up inside the component) */
    class MouseClickDragInEvent(component: GuiComponent, mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(component, mouseDownPos, mousePos, button)

    /** Fired when the mouse is clicked within the component (mouse goes down inside and up outside the component) */
    class MouseClickDragOutEvent(component: GuiComponent, mouseDownPos: Vec2d, mousePos: Vec2d, button: EnumMouseButton) : MouseClickAnyEvent(component, mouseDownPos, mousePos, button)

    /** Fired when a key is pressed */
    class KeyDownEvent(@JvmField val component: GuiComponent, val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when a key is released */
    class KeyUpEvent(@JvmField val component: GuiComponent, val key: Char, val keyCode: Int) : EventCancelable()

    /** Fired when the mouse is moved into the component */
    class MouseInEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d) : Event()

    /** Fired when the mouse is moved out of the component */
    class MouseOutEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d) : Event()

    /** Fired when the mouse wheel is moved */
    class MouseWheelEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val direction: MouseWheelDirection) : EventCancelable()

    enum class MouseWheelDirection(@JvmField val ydirection: Int) {
        UP(+1), DOWN(-1);

        companion object {
            @JvmStatic
            fun fromSign(dir: Int): MouseWheelDirection {
                return if (dir >= 0) UP else DOWN
            }
        }
    }

    /** Fired before a child is added to the component */
    class AddChildEvent(@JvmField val component: GuiComponent, val child: GuiComponent) : EventCancelable()

    /** Fired when a child is removed from the component */
    class RemoveChildEvent(@JvmField val component: GuiComponent, val child: GuiComponent) : EventCancelable()

    /** Fired when the component is added as a child to another component */
    class AddToParentEvent(@JvmField val component: GuiComponent, val parent: GuiComponent) : EventCancelable()

    /** Fired when the component is removed from its parent */
    class RemoveFromParentEvent(@JvmField val component: GuiComponent, val parent: GuiComponent) : EventCancelable()

    /** Fired before data is set */
    class SetDataEvent<D>(@JvmField val component: GuiComponent, val clazz: Class<D>, val key: String, val value: D) : EventCancelable()

    /** Fired before data is removed */
    class RemoveDataEvent<D>(@JvmField val component: GuiComponent, val clazz: Class<D>, val key: String, val value: D?) : EventCancelable()

    /** Fired when data is queried */
    class GetDataEvent<D>(@JvmField val component: GuiComponent, val clazz: Class<D>, val key: String, val value: D?) : Event()

    /** Fired when the data key set is queried */
    class GetDataKeysEvent<D>(@JvmField val component: GuiComponent, val clazz: Class<D>, val value: MutableSet<String>) : Event()

    /** Fired when the data class set is queried */
    class GetDataClassesEvent(@JvmField val component: GuiComponent, val value: MutableSet<Class<*>>) : Event()

    /** Fired when the component is checked for a tag */
    class HasTagEvent(@JvmField val component: GuiComponent, val tag: Any, var hasTag: Boolean) : Event()

    /** Fired before a tag is added to the component */
    class AddTagEvent(@JvmField val component: GuiComponent, val tag: Any) : EventCancelable()

    /** Fired before a tag is removed from a component */
    class RemoveTagEvent(@JvmField val component: GuiComponent, val tag: Any) : EventCancelable()

    /** Fired before checking if the mouse is over this component */
    class PreMouseOverEvent(@JvmField val component: GuiComponent, val parentMousePos: Vec2d) : Event()

    /** Fired when checking if the mouse is over this component */
    class MouseOverEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, var isOver: Boolean, var isOverNoOcclusion: Boolean) : Event()
}
