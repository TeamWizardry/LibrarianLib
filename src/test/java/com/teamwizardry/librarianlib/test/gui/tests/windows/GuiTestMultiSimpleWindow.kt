package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryButton
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.gui.windows.SimpleWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import java.awt.Color

class GuiTestMultiSimpleWindow: SimpleWindow(100, 100) {
    init {
        add(ColorLayer(Color.RED, 0, 0, 100, 100))
        val button = PastryButton("Open", 50, 50, 50)
        button.anchor = vec(0.5, 0.5)
        button.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            val window = Secondary()
            window.open()
            window.pos += vec(50, -25)
        }
        add(button)
    }

    class Secondary: SimpleWindow(100, 100) {
        init {
            add(ColorLayer(Color.GREEN, 0, 0, 100, 100))

            val button = PastryButton("Close", 75, 10, 50)
            button.anchor = vec(0.5, 0.5)
            button.BUS.hook<GuiComponentEvents.MouseClickEvent> {
                this.close()
            }
            add(button)
        }
    }
}