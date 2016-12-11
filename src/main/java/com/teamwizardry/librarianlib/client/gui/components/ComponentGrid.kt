package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.vec

class ComponentGrid(posX: Int, posY: Int, var cellWidth: Int, var cellHeight: Int, var gridColumns: Int) : GuiComponent<ComponentGrid>(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        var x = 0
        var y = 0
        for (component in components) {
            component.pos = vec(x * cellWidth, y * cellHeight)

            x++
            if (x == gridColumns) {
                x = 0
                y++
            }
        }
        super.draw(mousePos, partialTicks)
    }

}
