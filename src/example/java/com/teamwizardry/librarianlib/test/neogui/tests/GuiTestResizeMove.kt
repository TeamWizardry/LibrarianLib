package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestResizeMove : GuiBase() {
    init {
        main.size = vec(100, 100)

        val c = ComponentRect(25, 25, 50, 50)
        c.color = Color.RED
        main.add(c)

        c.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            c.pos += vec(1, 1)
            c.size += vec(1, 0)
        }

    }
}
