package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents

public abstract class PastryToggle(posX: Int, posY: Int, width: Int, height: Int): PastryActivatedControl(posX, posY, width, height) {
    public var state: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateVisualState()
            }
        }
    private var clickStarted = false
    private var previewToggle = false
    private var visualState = false
        set(value) {
            if(field != value) {
                field = value
                visualStateChanged(value)
            }
        }

    private fun updateVisualState() {
        visualState = if(previewToggle) !state else state
    }

    public abstract fun visualStateChanged(visualState: Boolean)

    override fun activate() {
        if (!BUS.fire(BeginToggleEvent()).isCanceled()) {
            previewToggle = true
            updateVisualState()
        }
    }

    override fun activationEnd() {
        val newState = !state
        if (!BUS.fire(StateWillChangeEvent(newState)).isCanceled()) {
            state = newState
            BUS.fire(StateChangedEvent())
        }
        previewToggle = false
        updateVisualState()
    }

    @Hook
    private fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if (mouseOver && !BUS.fire(BeginToggleEvent()).isCanceled()) {
            clickStarted = true
            previewToggle = true
            updateVisualState()
        }
    }

    @Hook
    private fun mouseLeave(e: GuiLayerEvents.MouseMoveOff) {
        if (clickStarted) {
            previewToggle = false
            updateVisualState()
        }
    }

    @Hook
    private fun mouseEnter(e: GuiLayerEvents.MouseMoveOver) {
        if (clickStarted) {
            previewToggle = true
            updateVisualState()
        }
    }

    @Hook
    private fun click(e: GuiLayerEvents.MouseClick) {
        val newState = !state
        if (!BUS.fire(StateWillChangeEvent(newState)).isCanceled()) {
            state = newState
            BUS.fire(StateChangedEvent())
        }
        clickStarted = false
        previewToggle = false
        updateVisualState()
    }

    @Hook
    private fun mouseUp(e: GuiLayerEvents.MouseUp) {
        if (mouseOver && clickStarted) {
            clickStarted = false
            previewToggle = false
            updateVisualState()
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
