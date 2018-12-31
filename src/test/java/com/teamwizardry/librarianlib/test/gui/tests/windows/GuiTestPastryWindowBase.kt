package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryWindowBase
import java.awt.Color

class GuiTestPastryWindowBase: PastryWindowBase(100, 100) {
    init {
        add(ColorLayer(Color.RED, 0, 0, 100, 100))
    }
}