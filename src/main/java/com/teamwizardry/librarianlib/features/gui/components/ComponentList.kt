package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * ## Facade equivalent: [StackLayout][com.teamwizardry.librarianlib.features.facade.layout.StackLayout]
 */
//@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentList(posX: Int, posY: Int, var rowHeight: Int) : GuiComponent(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        var y = 0

        for (component in children.asSequence().filter(GuiComponent::isVisible)) {
            val h = rowHeight
            component.pos = vec(component.pos.x, y)
            y += h
        }
    }
}
