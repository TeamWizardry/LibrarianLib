package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec

public abstract class PastryToggle(posX: Int, posY: Int, width: Int, height: Int): PastryActivatedControl(posX, posY, width, height) {
    private var mouseDown = false
    private var pressed = false
    private var visualState = false
    public var state: Boolean = false
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

    public abstract fun visualStateChanged(visualState: Boolean)

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
    private fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if(this.mouseOver && !BUS.fire(BeginToggleEvent()).isCanceled()) {
            pressed = true
            mouseDown = true
            updateVisualState()
        }
    }

    @Hook
    private fun mouseLeave(e: GuiLayerEvents.MouseMoveOff) {
        if(mouseDown) {
            pressed = false
            updateVisualState()
        }
    }
    @Hook
    private fun mouseEnter(e: GuiLayerEvents.MouseMoveOver) {
        if(mouseDown) {
            pressed = true
            updateVisualState()
        }
    }

    @Hook
    private fun click(e: GuiLayerEvents.MouseClick) {
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
    private fun mouseUp(e: GuiLayerEvents.MouseUp) {
        if(mouseDown) {
            pressed = false
            if (mouseDown) {
                updateVisualState()
            }
            mouseDown = false
        }
    }

    /**
     * Called after a state change is committed.
     */
    public class StateChangedEvent(): Event()

    /**
     * Called before a state change is committed. Cancel this event to prevent the state change
     */
    public class StateWillChangeEvent(public val newState: Boolean): CancelableEvent()

    /**
     * Called before a toggle interaction begins. Cancel this event to prevent the interaction from starting.
     */
    public class BeginToggleEvent: CancelableEvent()
}
