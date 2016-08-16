package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.gui.GuiComponent

class ButtonMixin<T : GuiComponent<T>>(val component: GuiComponent<T>,
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
            state = if (!c.enabled) EnumButtonState.DISABLED
            else if (c.mouseOverThisFrame) EnumButtonState.HOVER
            else EnumButtonState.NORMAL

            when (state) {
                ButtonMixin.EnumButtonState.NORMAL -> normal()
                ButtonMixin.EnumButtonState.HOVER -> hover()
                ButtonMixin.EnumButtonState.DISABLED -> disabled()
            }
        })

        component.mouseClick.add({ c, pos, button ->
            if (state != EnumButtonState.DISABLED)
                handler()
            state != EnumButtonState.DISABLED
        })

        normal()
    }

    enum class EnumButtonState {
        NORMAL, DISABLED, HOVER
    }

    companion object {
        val TAG = "HasButtonMixin"
    }
}
