package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.math.BoundingBox2D
import com.teamwizardry.librarianlib.math.Vec2d

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
class ComponentList(posX: Int, posY: Int) : GuiComponent<ComponentList>(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        var y = 0

        for (component in components) {
            component.pos = Vec2d(component.pos.x, y.toDouble())
            val bb = component.getLogicalSize()
            if(bb != null) y = bb.max.yi
        }
    }
}
