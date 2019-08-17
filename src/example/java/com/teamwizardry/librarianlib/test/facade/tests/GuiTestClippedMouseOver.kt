package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestClippedMouseOver : GuiBase() {

    init {
        main.size = vec(500, 200)
        val background = RectLayer(Color.WHITE, 0, 0, 500, 200)
        main.add(background)

        rectMouseOver(10, 10, 75, 75, Color.GREEN) { rect ->
            rect.clipToBounds = true
            rect.cornerRadius = 25.0
        }

        rectMouseOver(100, 10, 75, 75, Color.GREEN) { rect ->
            rect.clipToBounds = true
            rect.cornerRadius = 25.0
            rect.cornerPixelSize = 3

            val child = ComponentRect(50, 50, 50, 50)
            child.color = Color.BLUE
            rect.add(child)
        }
    }

    fun rectMouseOver(posX: Int, posY: Int, width: Int, height: Int, color: Color, configure: (ComponentRect) -> Unit = {}) {
        val rect = ComponentRect(posX, posY, width, height)
        rect.BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) { event ->
            if(rect.mouseOver) {
                rect.color = color
            } else {
                rect.color = color.darker()
            }
        }
        rect.translateZ = 20.0

        configure(rect)

        this.main.add(rect)
    }

}
