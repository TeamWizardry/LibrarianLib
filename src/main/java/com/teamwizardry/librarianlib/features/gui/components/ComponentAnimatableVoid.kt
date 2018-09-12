package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.value.RMValueDouble

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
open class ComponentAnimatableVoid(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {
    var animX: Double by RMValueDouble(0.0)
}
