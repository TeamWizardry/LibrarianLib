package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * ## Facade equivalent: [GuiComponent][com.teamwizardry.librarianlib.features.facade.GuiComponent]
 */
//@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentVoid : GuiComponent {

    constructor(posX: Int, posY: Int, width: Int, height: Int) : super(posX, posY, width, height)
    constructor(posX: Int, posY: Int) : super(posX, posY)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

}
