package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * ## Facade equivalent: [StackLayout][com.teamwizardry.librarianlib.features.facade.layout.StackLayout]
 */
@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentCenterAlign(posX: Int, posY: Int, var centerHorizontal: Boolean, var centerVertical: Boolean) : GuiComponent(posX, posY) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        if (centerHorizontal || centerVertical) {
            for (component in children) {
                val componentCenter = component.transformToParentContext(component.size / 2)
                var adjustedPos = (size / 2) - componentCenter
                if (!centerHorizontal)
                    adjustedPos = adjustedPos.setX(0.0)
                if (!centerVertical)
                    adjustedPos = adjustedPos.setY(0.0)
                component.pos += adjustedPos
            }
        }
    }

}
