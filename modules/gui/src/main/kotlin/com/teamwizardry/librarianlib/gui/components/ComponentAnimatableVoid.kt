package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.value.RMValueDouble

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
@Deprecated("Why does this even exist?")
open class ComponentAnimatableVoid(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {
    val animX_rm = RMValueDouble(0.0)
    var animX: Double by animX_rm
}
