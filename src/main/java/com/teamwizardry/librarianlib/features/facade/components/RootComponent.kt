package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.supporting.MouseHit

abstract class RootComponent(x: Int, y: Int, width: Int, height: Int): GuiComponent(x, y, width, height) {
    var topMouseHit: MouseHit? = null
    var focusedComponent: GuiComponent? = null
    override val root: GuiComponent
        get() = this
}