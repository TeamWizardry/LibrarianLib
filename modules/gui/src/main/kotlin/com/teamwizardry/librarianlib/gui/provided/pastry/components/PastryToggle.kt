package com.teamwizardry.librarianlib.gui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

abstract class PastryToggle(posX: Int, posY: Int, width: Int, height: Int): PastryActivatedControl(posX, posY, width, height) {
    protected var fixedSize = vec(width, height)
    override var size: Vec2d
        get() = fixedSize
        set(value) { /* nop */ }

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

    override fun activate() {
        if(!BUS.fire(BeginToggleEvent()).isCanceled()) {
            pressed = true
            updateVisualState()
        }
    }

    override fun activationEnd() {
        val state = state
        pressed = false
        if(!BUS.fire(StateWillChangeEvent(!state)).isCanceled()) {
            this.state = !state
            BUS.fire(StateChangedEvent())
        }
        updateVisualState()
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
        if(mouseDown && !BUS.fire(StateWillChangeEvent(!state)).isCanceled()) {
            this.state = !state
            updateVisualState()
            BUS.fire(StateChangedEvent())
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
     * Called after a state change is committed.
     */
    class StateChangedEvent(): Event()

    /**
     * Called before a state change is committed. Cancel this event to prevent the state change
     */
    class StateWillChangeEvent(val newState: Boolean): EventCancelable()

    /**
     * Called before a toggle interaction begins. Cancel this event to prevent the interaction from starting.
     */
    class BeginToggleEvent: EventCancelable()
}
