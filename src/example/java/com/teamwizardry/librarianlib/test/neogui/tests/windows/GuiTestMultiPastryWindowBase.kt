package com.teamwizardry.librarianlib.test.neogui.tests.windows

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryWindowBase
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color

@UseExperimental(ExperimentalBitfont::class)
class GuiTestMultiPastryWindowBase: PastryWindowBase(100, 100) {
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

    class Secondary: PastryWindowBase(100, 100) {
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