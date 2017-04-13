package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
class ComponentList(posX: Int, posY: Int) : GuiComponent<ComponentList>(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        var y = 0

        for (component in components) {
            component.pos = vec(component.pos.x, y)
            val bb = component.getLogicalSize()
            if (bb != null) y = bb.max.yi
        }
    }
}
