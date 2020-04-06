package com.teamwizardry.librarianlib.gui.component

import com.teamwizardry.librarianlib.utilities.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.utilities.eventbus.Event

/**
 * Order of events when rendering:
 * - **Update/Tick**
 * - |-- Calls [GuiLayer.tick]
 * - |-- Fires [GuiLayerEvents.Tick]
 * - |-- Calls [GuiLayer.update]
 * - |-- Fires [GuiLayerEvents.Update]
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
 * (TODO: this is a good spot for the update event. Right after input but before rendering)
 * - **Update cursor icon (GuiComponent)**
 * - **Render**
 * - |-- Calls [GuiLayer.drawDebugBoundingBox]
 * - |-- Applies layer transform
 * - |-- Pushes clipping
 * - |-- Calls [GuiLayer.draw]
 * - |-- Renders children
 * - |-- Pops clipping
 * - |-- Draws debug bounding box, if enabled
 */
object GuiLayerEvents {
    class Update : Event()
    class Tick : Event()
    class PreFrameEvent : Event()
    class AddChildEvent(val child: GuiLayer) : CancelableEvent()
    class RemoveChildEvent(val child: GuiLayer) : CancelableEvent()
    class AddToParentEvent(val parent: GuiLayer) : CancelableEvent()
    class RemoveFromParentEvent(val parent: GuiLayer) : CancelableEvent()
    class LayoutChildren : Event()
}
