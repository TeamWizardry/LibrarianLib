package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d

import java.util.function.Consumer

/**
 * ## Facade equivalent: None
 */
@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentRaw : GuiComponent {

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
