package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestRect : GuiBase() {
    init {
        main.size = vec(100, 100)

        val c = ComponentRect(25, 25, 50, 50)
        c.color = Color(255, 0, 0, 127)
        main.add(c)

    }
}
