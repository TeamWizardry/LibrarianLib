package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.math.BoundingBox2D
import com.teamwizardry.librarianlib.math.Vec2d

class ComponentCenterAlign(posX: Int, posY: Int, var centerHorizontal: Boolean, var centerVertical: Boolean) : GuiComponent<ComponentCenterAlign>(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // noop
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        if (centerHorizontal || centerVertical) {
            for (component in components) {
                val compPos = component.pos
                val bb = component.getLogicalSize()
                bb ?: continue
                val posOffsetFromBB = compPos.sub(bb.min)
                val centerPos = bb.max.sub(bb.min).mul((1f / 2f).toDouble()).sub(posOffsetFromBB)
                var adjustedPos = centerPos.mul(-1.0)
                if (!centerHorizontal)
                    adjustedPos = adjustedPos.setX(compPos.x)
                if (!centerVertical)
                    adjustedPos = adjustedPos.setY(compPos.y)
                component.pos = adjustedPos
            }
        }

        super.draw(mousePos, partialTicks)
    }

}
