package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestRect : GuiBase(100, 100) {
    init {

        val c = ComponentRect(25, 25, 50, 50)
        c.color = Color(255, 0, 0, 127)
        mainComponents.add(c)

    }
}
