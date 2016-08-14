package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.math.Vec2d

class ComponentGrid(posX: Int, posY: Int, var cellWidth: Int, var cellHeight: Int, var gridColumns: Int) : GuiComponent<ComponentGrid>(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // NOOP
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        var x = 0
        var y = 0
        for (component in components) {
            component.pos = Vec2d((x * cellWidth).toDouble(), (y * cellHeight).toDouble())

            x++
            if (x == gridColumns) {
                x = 0
                y++
            }
        }
        super.draw(mousePos, partialTicks)
    }

}
