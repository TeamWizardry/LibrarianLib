package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.FixedSizeComponent
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

abstract class PastryToggle(posX: Int, posY: Int, width: Int, height: Int): FixedSizeComponent(posX, posY, width, height) {
    private var mouseDown = false
    private var pressed = false
    private var visualState = false
    var stateStorage = false
    var state: Boolean
        get() = stateStorage
        set(value) {
            if(stateStorage != value) {
                stateStorage = value
                updateVisualState()
                stateChanged(value)
            } else {
                stateStorage = value
            }
        }

    private fun updateVisualState() {
        val visualState = pressed != state
        if(visualState != this.visualState) {
            this.visualState = visualState
            visualStateChanged(visualState)
        }
    }

    abstract fun stateChanged(state: Boolean)
    abstract fun visualStateChanged(visualState: Boolean)

    init {
        this.hoverCursor = LibCursor.POINT
    }

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(this.mouseOver) {
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
        stateStorage = !stateStorage
        pressed = false
        mouseDown = false
        updateVisualState()
        stateChanged(stateStorage)
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseClickDragOutEvent) {
        pressed = false
        mouseDown = false
        updateVisualState()
    }

}
