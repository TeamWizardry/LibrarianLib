package com.teamwizardry.librarianlib.test.neogui.tests.windows

import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryColorPicker
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryWindow
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@UseExperimental(ExperimentalBitfont::class)
class GuiTestPastryColorPicker: PastryWindow(100, 60) {
    init {
        val button = PastryButton("Color", 10, 10, 45) {
            PastryColorPicker().open()
        }
        content.add(button)
    }
}