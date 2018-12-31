package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

class GuiTestPastryWindow: PastryWindow(100, 100) {
    val headerLayer = ColorLayer(Color.GREEN, 0, 0, 0, 0)

    init {
        header.add(headerLayer)
        content.add(ColorLayer(Color.RED, 0, 0, 100, 100))
    }

    override fun layoutChildren() {
        super.layoutChildren()
        headerLayer.pos = vec(0, 0)
        headerLayer.size = header.size
    }
}