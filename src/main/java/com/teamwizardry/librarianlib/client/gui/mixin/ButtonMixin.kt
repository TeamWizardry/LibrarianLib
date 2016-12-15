package com.teamwizardry.librarianlib.client.gui.mixin

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.gui.EnumMouseButton
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.Vec2d

class ButtonMixin<T : GuiComponent<T>>(val component: GuiComponent<T>, init: Runnable) {

    constructor(component: GuiComponent<T>, init: () -> Unit) : this(component, Runnable(init))

    class ButtonClickEvent<out T : GuiComponent<*>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class ButtonStateChangeEvent<out T : GuiComponent<*>>(val component: T, val mousePos: Vec2d, val state: EnumButtonState, var newState: EnumButtonState) : Event()

    var state = EnumButtonState.NORMAL

    init {
        if (!component.hasTag(TAG))
            apply()
        else
            LibrarianLog.warn("Component already has button mixin!")
        init.run()
    }

    private fun apply() {

        component.addTag(TAG)

        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) { event ->
            val newState = if (!event.component.enabled) EnumButtonState.DISABLED
            else if (event.component.mouseOver) EnumButtonState.HOVER
            else EnumButtonState.NORMAL

            state = component.BUS.fire(ButtonStateChangeEvent(component, event.mousePos, state, newState)).newState
        }

        component.BUS.hook(GuiComponent.MouseClickEvent::class.java) { event ->
            if (state != EnumButtonState.DISABLED)
                component.BUS.fire(ButtonClickEvent(event.component, event.mousePos, event.button))
            state != EnumButtonState.DISABLED
        }
    }

    enum class EnumButtonState {
        NORMAL, DISABLED, HOVER
    }

    companion object {
        val TAG = "HasButtonMixin"
    }
}
