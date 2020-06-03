package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW.*

abstract class PastryActivatedControl: PastryControl {
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    private var activatingKey: Int = 0
    private var justFocused = false

    protected abstract fun activate()
    protected open fun activationEnd() {}

    @Hook
    private fun focused(e: GuiLayerEvents.FocusEvent) {
        justFocused = true
    }

    @Hook
    private fun keyDown(e: GuiLayerEvents.KeyDown) {
        if(!isFocused) return
        if(e.keyCode == GLFW_KEY_ENTER || e.keyCode == GLFW_KEY_KP_ENTER) {
            activatingKey = e.keyCode
            activate()
        }
        if(e.keyCode == GLFW_KEY_TAB && !justFocused) {
            if(Screen.hasShiftDown())
                focusPrevious()
            else
                focusNext()
        }
        justFocused = false
    }

    @Hook
    private fun keyUp(e: GuiLayerEvents.KeyUp) {
        if(activatingKey != 0 && e.keyCode == activatingKey) {
            activatingKey = 0
            activationEnd()
        }
    }
}