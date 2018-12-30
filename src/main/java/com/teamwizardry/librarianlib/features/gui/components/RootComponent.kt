package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.supporting.MouseHit

abstract class RootComponent(x: Int, y: Int, width: Int, height: Int): GuiComponent(x, y, width, height) {
    var topMouseHit: MouseHit? = null
    override val root: GuiLayer
        get() = this
}