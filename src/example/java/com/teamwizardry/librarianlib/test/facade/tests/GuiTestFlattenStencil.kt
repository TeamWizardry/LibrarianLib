package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.copy
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestFlattenStencil : GuiBase() {
    init {
        main.size = vec(0, 100)

        val normal = createTestLayer(0.5f)
        val flat = createTestLayer(1.0f)
        flat.opacity = 0.5

        normal.pos = vec(-110, 0)
        flat.pos = vec(10, 0)

        main.add(normal, flat)
    }

    fun createTestLayer(colorAlpha: Float): GuiLayer {
        val outer = GuiLayer(0, 0, 100, 100)
        val background = RectLayer(Color.red.copy(alpha = colorAlpha), 0, 0, 75, 75)
        val foreground = RectLayer(Color.blue.copy(alpha = colorAlpha), 25, 25, 75, 75)
        val clipped = RectLayer(Color.green.copy(alpha = colorAlpha), 10, 25, 30, 100)
        outer.add(background, foreground)
        foreground.add(clipped)
        foreground.clipToBounds = true

        foreground.cornerRadius_rm.animate(0.0, 20.0, 40f, Easing.easeInOutCubic).repeat(-1).reverseOnRepeat()
        clipped.pos_rm.animate(vec(10, 25), vec(10, -50), 30f, Easing.easeInOutCubic).repeat(-1).reverseOnRepeat()

        return outer
    }
}
