package com.teamwizardry.librarianlib.test.animator.tests

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiBasicAnimation : GuiBase(200, 100) {
    val animator = Animator()

    init {


        val background = ComponentRect(0, 0, 200, 100)
        background.color.setValue(Color(127, 127, 127))
        mainComponents.add(background)

        val bouncer = ComponentRect(0, 0, 25, 15)
        bouncer.color.setValue(Color(255, 0, 0))
        mainComponents.add(bouncer)

        val bouncer2 = ComponentRect(-50, 95, 50, 5)
        bouncer2.color.setValue(Color(0, 0, 255))
        mainComponents.add(bouncer2)

        val anim = BasicAnimation(bouncer2, AnimatableProperty.get(GuiComponent::class.java, "pos.x"))
        anim.duration = 40f
        anim.to = 200
        anim.repeatCount = -1
        animator.add(anim)

        mainComponents.BUS.hook(GuiComponent.EVENTS.MouseClickEvent) {
            val anim = BasicAnimation(bouncer, AnimatableProperty.get(GuiComponent::class.java, "pos.x"))
            anim.duration = 20f
            anim.to = 175
            anim.shouldReverse = true
            anim.repeatCount = 5

            animator.add(anim)
        }

    }

    override fun doesGuiPauseGame(): Boolean {
        return true
    }
}
