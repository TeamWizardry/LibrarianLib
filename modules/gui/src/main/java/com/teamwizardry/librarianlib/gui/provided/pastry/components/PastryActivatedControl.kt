package com.teamwizardry.librarianlib.gui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

abstract class PastryActivatedControl: PastryControl {
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    private var activatingKey: Int = 0
    private var justFocused = false

    protected abstract fun activate()
    protected open fun activationEnd() {}

    @Hook
    private fun focused(e: GuiComponentEvents.FocusEvent) {
        justFocused = true
    }

    @Hook
    private fun keyDown(e: GuiComponentEvents.KeyDownEvent) {
        if(!isFocused) return
        if(e.keyCode == Keyboard.KEY_RETURN || e.keyCode == Keyboard.KEY_NUMPADENTER) {
            activatingKey = e.keyCode
            activate()
        }
        if(e.keyCode == Keyboard.KEY_TAB && !justFocused) {
            if(GuiScreen.isShiftKeyDown())
                focusPrevious()
            else
                focusNext()
        }
        justFocused = false
    }

    @Hook
    private fun keyUp(e: GuiComponentEvents.KeyUpEvent) {
        if(activatingKey != 0 && e.keyCode == activatingKey) {
            activatingKey = 0
            activationEnd()
        }
    }
}