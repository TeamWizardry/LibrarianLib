package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.value.GuiAnimator
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class GuiTestImplicitAnimation : GuiBase() {
    init {
        main.size = vec(100, 100)

        val c = ComponentRect(25, 25, 50, 50)
        c.color = Color.RED
        main.add(c)

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
