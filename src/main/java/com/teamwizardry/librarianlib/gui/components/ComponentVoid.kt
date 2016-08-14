package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.math.Vec2d

class ComponentVoid : GuiComponent<ComponentVoid> {

    constructor(posX: Int, posY: Int, width: Int, height: Int) : super(posX, posY, width, height) {
    }

    constructor(posX: Int, posY: Int) : super(posX, posY) {
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // NOOP
    }

}
