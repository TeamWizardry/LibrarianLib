package com.teamwizardry.librarianlib.gui.component

import com.teamwizardry.librarianlib.utilities.eventbus.Event
import com.teamwizardry.librarianlib.gui.EnumMouseButton
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.utilities.eventbus.CancelableEvent

/**
 * Order of events:
 * * **Input phase:**
 * * Computes mouse position
 * * todo: mouse events
 * * todo: key events
 * * **Update phase:**
 * * Fires [Update]
 * * Updates yoga layout
 * * Fires [LayoutChildren]
 * * **Draw phase:**
 * *
 */
object GuiComponentEvents {
    /**
     * Called after input events and before layout is calculated, this is the best place for general-purpose updates
     */
    class Update : Event()

    class AddChildEvent(val child: GuiComponent) : CancelableEvent()
    class RemoveChildEvent(val child: GuiComponent) : CancelableEvent()
    class AddToParentEvent(val parent: GuiComponent) : CancelableEvent()
    class RemoveFromParentEvent(val parent: GuiComponent) : CancelableEvent()

    /**
     *
     */
    class LayoutChildren : Event()
}
