package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.neogui.layers.ArcLayer
import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import java.awt.Color
import java.lang.Math.PI
import kotlin.math.atan2

/**
 * Created by TheCodeWarrior
 */
class GuiTestArc : GuiBase() {
    init {
        main.size = vec(230, 120)

        val bg = ColorLayer(Color.GRAY, 0, 0, 230, 100)
        main.add(bg)

        val arc = ArcLayer(Color.BLACK, 60, 60, 100, 100)

        arc.endAngle_im.animateKeyframes(0.0)
            .add(60f, PI*2, Easing.easeInOutSine)
            .add(60f, PI*2)
            .finish().repeatCount = -1
        arc.startAngle_im.animateKeyframes(0.0)
            .add(60f, 0.0)
            .add(60f, PI*2, Easing.easeInOutSine)
            .finish().repeatCount = -1

        val mouseArc = ArcLayer(Color.BLACK, 170, 60, 100, 100)
        val arcComponent = mouseArc.componentWrapper()
        arcComponent.BUS.hook<GuiLayerEvents.PreDrawEvent> {
            val center = arcComponent.bounds.size/2
            val delta = arcComponent.mousePos - center
            val angle = atan2(-delta.x, delta.y) + PI
            mouseArc.endAngle = angle
        }

        main.add(arc.componentWrapper(), arcComponent)
    }
}
