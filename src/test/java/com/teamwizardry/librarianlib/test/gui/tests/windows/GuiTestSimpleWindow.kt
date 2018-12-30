package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryButton
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.gui.windows.SimpleWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

class GuiTestSimpleWindow: SimpleWindow(100, 100) {
    init {
        add(ColorLayer(Color.RED, 0, 0, 100, 100))
    }
}