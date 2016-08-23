package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.math.Vec2d

class ComponentVoid : GuiComponent<ComponentVoid> {

    constructor(posX: Int, posY: Int, width: Int, height: Int) : super(posX, posY, width, height)
    constructor(posX: Int, posY: Int) : super(posX, posY)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

}
