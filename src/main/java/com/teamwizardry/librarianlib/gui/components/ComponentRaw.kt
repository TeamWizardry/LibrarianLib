package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.math.Vec2d

import java.util.function.Consumer

class ComponentRaw : GuiComponent<ComponentRaw> {

    var func: Consumer<ComponentRaw>

    constructor(posX: Int, posY: Int, func: Consumer<ComponentRaw>) : super(posX, posY) {
        this.func = func
    }

    constructor(posX: Int, posY: Int, width: Int, height: Int, func: Consumer<ComponentRaw>) : super(posX, posY, width, height) {
        this.func = func
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        func.accept(this)
    }

}
