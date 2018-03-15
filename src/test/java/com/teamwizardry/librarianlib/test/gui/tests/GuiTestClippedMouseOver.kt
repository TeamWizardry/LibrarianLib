package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.awt.Color
import kotlin.math.PI

/**
 * Created by TheCodeWarrior
 */
class GuiTestClippedMouseOver : GuiBase(0, 0) {

    init {
        rectMouseOver(-200, 0, 100, 100, Color.RED)
        rectMouseOver(-100, 0, 100, 100, Color.BLUE) { rect ->
            val child = ComponentRect(-20, -20, 40, 40)
            child.color.setValue(Color.GREEN)
            child.transform.rotate = PI / 4
            rect.clipping.clipToBounds = true
            rect.add(child)
        }
        rectMouseOver(0, 0, 100, 100, Color.GREEN) { rect ->
            val child = ComponentRect(-20, -20, 40, 40)
            child.color.setValue(Color.BLUE)
            child.transform.rotate = PI / 4
            rect.clipping.clipToBounds = true
            rect.clipping.cornerRadius = 10.0
            rect.clipping.cornerPixelSize = 2
            rect.add(child)
        }
        rectMouseOver(100, 0, 100, 100, Color.BLUE) { rect ->
            val child = ComponentRect(-20, -20, 40, 40)
            child.color.setValue(Color.GREEN)
            child.transform.rotate = PI / 4
            rect.clipping.clipToBounds = true
            rect.clipping.cornerRadius = 10.0
            rect.add(child)
        }
        rectMouseOver(200, 0, 100, 100, Color.RED) { rect ->
            val child = ComponentRect(-20, -20, 40, 40)
            child.color.setValue(Color.BLUE)
            child.transform.rotate = PI / 4
            rect.clipping.clipToBounds = true
            rect.clipping.cornerRadius = 10.0
            rect.clipping.cornerPixelSize = 2
            rect.transform.rotate = PI / 4
            rect.add(child)
        }
    }

    fun rectMouseOver(posX: Int, posY: Int, width: Int, height: Int, color: Color, configure: (ComponentRect) -> Unit = {}) {
        val rect = ComponentRect(posX, posY, width, height)
        rect.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) { event ->
            if(rect.mouseOver) {
                rect.color.setValue(color)
            } else {
                rect.color.setValue(color.darker())
            }
        }

        configure(rect)

        this.mainComponents.add(rect)
    }

}
