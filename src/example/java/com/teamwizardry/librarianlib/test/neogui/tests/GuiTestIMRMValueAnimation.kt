package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class GuiTestIMRMValueAnimation : GuiBase() {
    init {
        main.size = vec(100, 100)

        val c = ComponentRect(25, 25, 50, 50)
        c.color = Color.RED
        main.add(c)

        c.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            fun rand() = ThreadLocalRandom.current().nextDouble(-20.0, 30.0)
            c.size_rm.animate(c.size + vec(rand(), rand()), 20f)
            c.size_rm.animate(c.size, 20f, Easing.linear, 20f)
            c.rotation_rm.animate(Math.PI/2, 20f)
            c.rotation_rm.animate(0.0, 20f, Easing.linear, 20f)
        }

    }
}
