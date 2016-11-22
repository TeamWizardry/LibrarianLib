package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.vec

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
