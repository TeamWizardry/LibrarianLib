package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryColorPicker
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.awt.Color

class GuiTestPastryColorPicker: PastryWindow(100, 60) {
    init {
        val button = PastryButton("Color", 10, 10, 45)
        content.add(button)
        button.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            PastryColorPicker().open()
        }
    }
}