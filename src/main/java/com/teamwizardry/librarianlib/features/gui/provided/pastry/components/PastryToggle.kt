package com.teamwizardry.librarianlib.features.gui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.FixedSizeComponent
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

abstract class PastryToggle(posX: Int, posY: Int, width: Int, height: Int): FixedSizeComponent(posX, posY, width, height) {
    private var mouseDown = false
    private var pressed = false
    private var visualState = false
    var state: Boolean = false
        set(value) {
            if(field != value) {
                field = value
                updateVisualState()
            } else {
                field = value
            }
        }

    private fun updateVisualState() {
        val visualState = pressed != state
        if(visualState != this.visualState) {
            this.visualState = visualState
            visualStateChanged(visualState)
        }
    }

    abstract fun visualStateChanged(visualState: Boolean)

    init {
        this.cursor = LibCursor.POINT
    }

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(this.mouseOver && !BUS.fire(BeginToggleEvent()).isCanceled()) {
            pressed = true
            mouseDown = true
            updateVisualState()
        }
    }

    @Hook
    private fun mouseLeave(e: GuiComponentEvents.MouseLeaveEvent) {
        if(mouseDown) {
            pressed = false
            updateVisualState()
        }
    }
    @Hook
    private fun mouseEnter(e: GuiComponentEvents.MouseEnterEvent) {
        if(mouseDown) {
            pressed = true
            updateVisualState()
        }
    }

    @Hook
    private fun click(e: GuiComponentEvents.MouseClickEvent) {
        val state = state
        pressed = false
        if(mouseDown && !BUS.fire(StateChangeEvent(!state)).isCanceled()) {
            this.state = !state
            updateVisualState()
        }
        mouseDown = false
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseClickDragOutEvent) {
        pressed = false
        if(mouseDown) {
            updateVisualState()
        }
        mouseDown = false
    }

    /**
     * Called before a state change is committed. Cancel this event to prevent the state change
     */
    class StateChangeEvent(val newState: Boolean): EventCancelable()

    /**
     * Called before a toggle interaction begins. Cancel this event to prevent the interaction from starting.
     */
    class BeginToggleEvent: EventCancelable()
}
