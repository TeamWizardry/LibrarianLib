package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color
import kotlin.math.PI

/**
 * Created by TheCodeWarrior
 */
class GuiTestClippedMouseOver : GuiBase() {

    init {
        main.size = vec(500, 200)
        val background = ColorLayer(Color.WHITE, 0, 0, 500, 200)
        main.add(background)

        rectMouseOver(10, 10, 75, 75, Color.GREEN) { rect ->
            rect.clipToBounds = true
            rect.cornerRadius = 25.0
        }

        rectMouseOver(100, 10, 75, 75, Color.GREEN) { rect ->
            rect.clipToBounds = true
            rect.cornerRadius = 25.0
            rect.cornerPixelSize = 3
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

        configure(rect)

        this.main.add(rect)
    }

}
