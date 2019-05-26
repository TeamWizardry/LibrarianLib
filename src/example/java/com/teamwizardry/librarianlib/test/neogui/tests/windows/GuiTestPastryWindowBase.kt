package com.teamwizardry.librarianlib.test.neogui.tests.windows

import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryWindowBase
import java.awt.Color

class GuiTestPastryWindowBase: PastryWindowBase(100, 100) {
    init {
        add(ColorLayer(Color.RED, 0, 0, 100, 100))
    }
}