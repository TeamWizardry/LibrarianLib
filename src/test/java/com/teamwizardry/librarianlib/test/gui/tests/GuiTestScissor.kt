package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestScissor : GuiBase(100, 100) {
    init {

        val bg = ComponentRect(0, 0, 200, 200)
        val c = ComponentRect(50, 50, 100, 100)
        val c2 = ComponentRect(50, 50, 100, 100)
        val c3 = ComponentRect(-500, -500, 1000, 1000)


        bg.color.setValue(Color.GRAY)
        c.color.setValue(Color.RED)
        c2.color.setValue(Color.GREEN)
        c3.color.setValue(Color.BLUE)


        val scissor = ComponentVoid(0, 0, 50, 50)
        c.add(scissor)
        c.transform.scale = 2.0
        scissor.add(c2)
        scissor.add(c3)
        scissor.clipToBounds = true

//        scissor.BUS.hook(GuiComponentEvents.ComponentTickEvent::class.java) {
//            val progress = (scissor.animationTicks % 100)/100.0
//
//            scissor.pos = vec(progress*100, progress*100)
//        }

        mainComponents.add(bg)
        mainComponents.add(c)

    }
}
