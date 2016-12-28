package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestRect : GuiBase(100, 100) {
    init {

        val c = ComponentRect(25, 25, 50, 50)
        c.color.setValue(Color(255, 0, 0, 127))
        mainComponents.add(c)

    }
}
