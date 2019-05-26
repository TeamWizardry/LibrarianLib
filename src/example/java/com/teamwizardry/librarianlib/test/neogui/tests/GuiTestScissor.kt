package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestScissor : GuiBase() {
    init {
        main.size = vec(100, 100)

        val bg = ComponentRect(0, 0, 200, 200)
        val c = ComponentRect(50, 50, 100, 100)
        val c2 = ComponentRect(50, 50, 100, 100)
        val c3 = ComponentRect(-500, -500, 1000, 1000)


        bg.color = Color.GRAY
        c.color = Color.RED
        c2.color = Color.GREEN
        c3.color = Color.BLUE


        val scissor = GuiComponent(0, 0, 50, 50)
        c.add(scissor)
        c.scale = 2.0
        scissor.add(c2)
        scissor.add(c3)
        scissor.clipToBounds = true

//        scissor.BUS.hook(GuiComponentEvents.ComponentTickEvent::class.java) {
//            val progress = (scissor.animationTicks % 100)/100.0
//
//            scissor.pos = vec(progress*100, progress*100)
//        }

        main.add(bg)
        main.add(c)

    }
}
