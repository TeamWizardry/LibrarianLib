package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.value.RMValueDouble

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
@Deprecated("Why does this even exist?")
open class ComponentAnimatableVoid(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {
    val animX_rm = RMValueDouble(0.0)
    var animX: Double by animX_rm
}
