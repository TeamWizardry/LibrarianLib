package com.teamwizardry.librarianlib.features.facade.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable

/**
 * Order of events when rendering:
 * - **Sort children**
 * - **Run layout if needed**
 * - |-- Calls [GuiLayer.runLayoutIfNeeded]
 * - |-- Fires [GuiLayerEvents.LayoutChildren]
 * - **Pre frame**
 * - |-- Fires [GuiLayerEvents.PreFrameEvent]
 * - **Update mouse (GuiComponent)**
 * - |-- Fires [GuiComponentEvents.CalculateMousePositionEvent]
 * - |-- Fires [GuiComponentEvents.MouseDragEvent]
 * - |-- Fires [GuiComponentEvents.MouseMoveEvent]
 * - **Update hits (GuiComponent)**
 * - |-- Checks [GuiComponent.disableMouseCollision]
 * - |-- Calls [GuiLayer.isPointInBounds]
 * - **Propagate hits (GuiComponent)**
 * - |-- Fires [GuiComponentEvents.MouseDragLeaveEvent]
 * - |-- Fires [GuiComponentEvents.MouseLeaveEvent]
 * - |-- Fires [GuiComponentEvents.MouseDragEnterEvent]
 * - |-- Fires [GuiComponentEvents.MouseEnterEvent]
 * - |-- Fires [GuiComponentEvents.MouseDragOutEvent]
 * - |-- Fires [GuiComponentEvents.MouseMoveOutEvent]
 * - |-- Fires [GuiComponentEvents.MouseDragInEvent]
 * - |-- Fires [GuiComponentEvents.MouseMoveInEvent]
 * - **Update cursor icon (GuiComponent)**
 * - **Render**
 * - |-- Calls [GuiLayer.drawDebugBoundingBox]
 * - |-- Fires [GuiLayerEvents.PreTransformEvent]
 * - |-- Applies layer transform
 * - |-- Fires [GuiLayerEvents.PostTransformEvent]
 * - |-- Pushes clipping
 * - |-- Applies contents offset
 * - |-- Fires [GuiLayerEvents.PreDrawEvent]
 * - |-- Calls [GuiLayer.draw]
 * - |-- Fires [GuiLayerEvents.PreChildrenDrawEvent]
 * - |-- Renders children
 * - |-- Fires [GuiLayerEvents.PostDrawEvent]
 * - |-- Reverses contents offset
 * - |-- Pops clipping clipping
 * - |-- Draws debug bounding box, if enabled
 * - |-- Reverses layer transform
 */
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
    class LayoutChildren : Event()
}
