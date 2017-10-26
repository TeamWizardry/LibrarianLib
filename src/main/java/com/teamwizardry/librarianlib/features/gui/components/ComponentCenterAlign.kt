package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d

@Deprecated("Use `component.transform.postTranslate = -component.size/2` instead")
class ComponentCenterAlign(posX: Int, posY: Int, var centerHorizontal: Boolean, var centerVertical: Boolean) : GuiComponent(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }

    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        if (centerHorizontal || centerVertical) {
            for (component in components) {
                val componentCenter = component.transformToParentContext(component.size/2)
                var adjustedPos = (size/2) - componentCenter
                if (!centerHorizontal)
                    adjustedPos = adjustedPos.setX(0.0)
                if (!centerVertical)
                    adjustedPos = adjustedPos.setY(0.0)
                component.pos += adjustedPos
            }
        }

        super.draw(mousePos, partialTicks)
    }

}
