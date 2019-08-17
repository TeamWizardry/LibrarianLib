package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
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
