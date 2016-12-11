package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestResizeMove : GuiBase(100, 100) {
    init {

        val c = ComponentRect(25, 25, 50, 50)
        c.color.setValue(Color.RED)
        mainComponents.add(c)

        c.BUS.hook(GuiComponent.MouseClickEvent::class.java) { e ->
            c.pos += vec(1, 1)
            c.size += vec(1, 0)
        }

    }
}
