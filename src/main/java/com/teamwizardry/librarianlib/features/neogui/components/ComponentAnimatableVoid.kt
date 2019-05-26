package com.teamwizardry.librarianlib.features.neogui.components

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.value.RMValueDouble

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
open class ComponentAnimatableVoid(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {
    val animX_rm = RMValueDouble(0.0)
    var animX: Double by animX_rm
}
