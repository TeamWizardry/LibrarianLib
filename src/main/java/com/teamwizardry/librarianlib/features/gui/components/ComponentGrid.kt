package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

class ComponentGrid(posX: Int, posY: Int, var cellWidth: Int, var cellHeight: Int, var gridColumns: Int) : GuiComponent(posX, posY) {

    override fun draw(partialTicks: Float) {
        var x = 0
        var y = 0
        for (component in children) {
            component.pos = vec(x * cellWidth, y * cellHeight)

            x++
            if (x == gridColumns) {
                x = 0
                y++
            }
        }
    }

}
