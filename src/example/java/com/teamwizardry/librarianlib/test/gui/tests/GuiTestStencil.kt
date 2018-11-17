package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestStencil : GuiBase(0, 0) {
    init {
        val wrapper = ComponentVoid(0, 0)
        val clipping = ComponentRect(0, 0, 100, 100)
        val clipped = ComponentRect(50, 50, 100, 100)
        val marker = ComponentRect(49, 49, 102, 102)

        clipping.color.setValue(Color(0f, 1f, 0f, 0.5f))
        clipped.color.setValue(Color.BLUE)
        marker.color.setValue(Color.RED)

        wrapper.add(marker)
        wrapper.add(clipping)
        clipping.add(clipped)

        clipping.clipping.clipToBounds = true
        clipping.clipping.cornerRadius = 15.0
        clipping.clipping.cornerPixelSize = 2

        wrapper.transform.rotate = Math.toRadians(45.0)
        wrapper.transform.postTranslate = vec(-50, -50)

        val anim = BasicAnimation(clipping, "clipping.cornerRadius")
        anim.from = 0
        anim.to = 50
        anim.duration = 2 * 20f
        anim.shouldReverse = true
        anim.repeatCount = -1
        clipping.animator.add(anim)

        mainComponents.add(wrapper)
    }
}
