package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.gui.GuiComponent

class ButtonMixin(val component: GuiComponent<*>,
                  private val normal: () -> Unit, private val hover: () -> Unit, private val disabled: () -> Unit, private val handler: () -> Unit) {
    var state = EnumButtonState.NORMAL


    init {

        if (!component.hasTag(TAG))
            apply()
        else
            LibrarianLog.warn("Component already has button mixin!")
    }

    private fun apply() {

        component.addTag(TAG)

        component.preDraw.add({ c, pos, partialTicks ->
            var newState = state
            if (!c.isEnabled)
                newState = EnumButtonState.DISABLED
            else if (c.mouseOverThisFrame)
                newState = EnumButtonState.HOVER
            else
                newState = EnumButtonState.NORMAL
            if (newState != state) {
                state = newState
                when (state) {
                    ButtonMixin.EnumButtonState.NORMAL -> normal.changeState()
                    ButtonMixin.EnumButtonState.HOVER -> hover.changeState()
                    ButtonMixin.EnumButtonState.DISABLED -> disabled.changeState()
                    else -> {
                    }
                }
            }
        })

        component.mouseClick.add({ c, pos, button ->
            if (state != EnumButtonState.DISABLED)
                handler.handle()
            state != EnumButtonState.DISABLED
        })

        normal.changeState()
    }

    enum class EnumButtonState {
        NORMAL, DISABLED, HOVER
    }

    companion object {
        val TAG = "HasButtonMixin"
    }
}
