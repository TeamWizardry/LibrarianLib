package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.client.gui.mixin.ScissorMixin
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestScissor : GuiBase(100, 100) {
    init {

        val c = ComponentRect(-1000, -1000, 10000, 10000)
        c.color.setValue(Color.RED)
        val scissor = ComponentVoid(0, 0, 50, 50)
        ScissorMixin.scissor(scissor)
        scissor.add(c)
        mainComponents.add(scissor)

    }
}
