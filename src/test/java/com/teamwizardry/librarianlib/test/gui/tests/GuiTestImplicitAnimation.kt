package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.value.GuiAnimator
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class GuiTestImplicitAnimation : GuiBase(100, 100) {
    init {

        val c = ComponentRect(25, 25, 50, 50)
        c.color = Color.RED
        mainComponents.add(c)

        c.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            fun rand() = ThreadLocalRandom.current().nextDouble(-20.0, 30.0)
            val anim = GuiAnimator.animate(20f, Easing.linear) {
                c.size += vec(rand(), rand())
                c.color = Color.GREEN
            }
            anim.shouldReverse = true
            c.add(anim)
        }

    }
}
