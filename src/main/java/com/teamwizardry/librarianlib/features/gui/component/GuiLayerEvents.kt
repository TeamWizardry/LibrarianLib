package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.math.Vec2d

object GuiLayerEvents {
    class PreFrameEvent : Event()
    class PreTransformEvent(val partialTicks: Float) : Event()
    class PostTransformEvent(val partialTicks: Float) : Event()
    class PreDrawEvent(val partialTicks: Float) : Event()
    class PostDrawEvent(val partialTicks: Float) : Event(true)
    class PreChildrenDrawEvent(val partialTicks: Float) : Event()
    class AddChildEvent(val child: GuiLayer) : EventCancelable()
    class RemoveChildEvent(val child: GuiLayer) : EventCancelable()
    class AddToParentEvent(val parent: GuiLayer) : EventCancelable()
    class RemoveFromParentEvent(val parent: GuiLayer) : EventCancelable()

    class MouseInEvent : Event()
    class MouseOutEvent : Event()
    class AdjustMousePosition : Event()
    class LayOutChildren : Event()
}
