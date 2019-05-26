package com.teamwizardry.librarianlib.test.neogui.tests.windows

import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

class GuiTestPastryWindow: PastryWindow(100, 100) {
    val headerLayer = ColorLayer(Color.GREEN, 0, 0, 0, 0)

    init {
        header.add(headerLayer)
        content.add(ColorLayer(Color.RED, 0, 0, 100, 100))

        minSize = vec(50, 50)
        maxSize = vec(150, 150)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        headerLayer.pos = vec(0, 0)
        headerLayer.size = header.size
    }
}