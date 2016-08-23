package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.math.Vec2d

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
