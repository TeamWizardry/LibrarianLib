package com.teamwizardry.librarianlib.features.neogui.components

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.supporting.MouseHit

abstract class RootComponent(x: Int, y: Int, width: Int, height: Int): GuiComponent(x, y, width, height) {
    var topMouseHit: MouseHit? = null
    var focusedComponent: GuiComponent? = null
    override val root: GuiComponent
        get() = this
}