package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
@Deprecated("Use StackLayout")
class ComponentList(posX: Int, posY: Int, var rowHeight: Int) : GuiComponent(posX, posY) {

    override fun draw(partialTicks: Float) {
        var y = 0

        for (component in children.asSequence().filter(GuiLayer::isVisible)) {
            val h = rowHeight
            component.pos = vec(component.pos.x, y)
            y += h
        }
    }
}
