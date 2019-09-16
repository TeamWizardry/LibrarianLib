package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestAutoSizeScale : GuiBase() {
    init {
        main.size = vec(100, 500)

        val c = ComponentRect(25, 0, 50, 500)
        c.color = Color.RED
        main.add(c)

    }
}
