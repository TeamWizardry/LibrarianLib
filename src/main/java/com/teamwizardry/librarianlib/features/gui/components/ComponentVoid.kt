package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d

class ComponentVoid : GuiComponent {

    constructor(posX: Int, posY: Int, width: Int, height: Int) : super(posX, posY, width, height)
    constructor(posX: Int, posY: Int) : super(posX, posY)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

}
