package com.teamwizardry.librarianlib.test.neogui.tests.windows

import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.neogui.windows.GuiWindow
import java.awt.Color

class GuiTestSingleWindow: GuiWindow(100, 100) {
    init {
        add(ColorLayer(Color.RED, 0, 0, 100, 100))
    }
}