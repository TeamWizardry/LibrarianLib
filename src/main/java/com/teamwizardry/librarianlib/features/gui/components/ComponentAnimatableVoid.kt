package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent

/**
 * ## Facade equivalent: None
 */
//@Deprecated("As of version 4.20 this has been superseded by Facade")
open class ComponentAnimatableVoid(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {
    var animX: Double = 0.0
}
