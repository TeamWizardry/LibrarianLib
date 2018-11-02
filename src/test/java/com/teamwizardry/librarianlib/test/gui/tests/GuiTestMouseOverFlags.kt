package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.supporting.MousePropagationType
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

class GuiTestMouseOverFlags : GuiBase() {
    init {
        main.size = vec(0, 0)

        val background = ComponentRect(-55, -25, 110, 50)
        background.color = Color.WHITE
        val normal = rect(5, -35, 50, 50, Color.RED)
        val noOcclude = rect(30, -25, 50, 50, Color.BLUE)
        val noOccludePropagate = rect(55, -15, 50, 50, Color.GREEN)
        val propagate = rect(80, -5, 50, 50, Color.PINK)

        noOcclude.isOpaqueToMouse = false
        noOccludePropagate.isOpaqueToMouse = false
        noOccludePropagate.mousePropagationType = MousePropagationType.OVER
        propagate.mousePropagationType = MousePropagationType.OVER

        val normalLabel = ComponentText(57, -35)
        val noOccludeLabel = ComponentText(82, -25)
        val noOccludePropagateLabel = ComponentText(107, -15)
        val propagateLabel = ComponentText(132, -5)

        normalLabel.text = "occlude = true, propagate = false"
        noOccludeLabel.text = "occlude = false, propagate = false"
        noOccludePropagateLabel.text = "occlude = false, propagate = true"
        propagateLabel.text = "occlude = true, propagate = true"

        background.add(
                normal, normalLabel,
                noOcclude, noOccludeLabel,
                noOccludePropagate, noOccludePropagateLabel,
                propagate, propagateLabel
        )
        main.add(background)
    }

    private fun rect(x: Int, y: Int, w: Int, h: Int, color: Color): GuiComponent {
        val component = ComponentRect(x, y, w, h)
        component.color = color
        return component
    }
}
