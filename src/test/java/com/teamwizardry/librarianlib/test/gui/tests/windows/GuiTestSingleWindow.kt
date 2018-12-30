package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import java.awt.Color

class GuiTestSingleWindow: GuiWindow(100, 100) {
    init {
        add(ColorLayer(Color.RED, 0, 0, 100, 100))
    }
}