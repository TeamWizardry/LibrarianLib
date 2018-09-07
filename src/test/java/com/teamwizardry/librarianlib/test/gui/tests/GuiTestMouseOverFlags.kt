package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import java.awt.Color

class GuiTestMouseOverFlags : GuiBase(0, 0) {
    init {
        val background = ComponentRect(-55, -25, 110, 50)
        background.color = Color.WHITE
        val normal = rect(5, -35, 50, 50, Color.RED)
        val noOcclude = rect(30, -25, 50, 50, Color.BLUE)
        val noOccludeNoPropagate = rect(55, -15, 50, 50, Color.GREEN)
        val noPropagate = rect(80, -5, 50, 50, Color.PINK)

        noOcclude.componentOccludesMouseOver = false
        noOccludeNoPropagate.componentOccludesMouseOver = false
        noOccludeNoPropagate.componentPropagatesMouseOverToParent = false
        noPropagate.componentPropagatesMouseOverToParent = false

        val normalLabel = ComponentText(57, -35)
        val noOccludeLabel = ComponentText(82, -25)
        val noOccludeNoPropagateLabel = ComponentText(107, -15)
        val noPropagateLabel = ComponentText(132, -5)

        normalLabel.text = "occlude = true, propagate = true"
        noOccludeLabel.text = "occlude = false, propagate = true"
        noOccludeNoPropagateLabel.text = "occlude = false, propagate = false"
        noPropagateLabel.text = "occlude = true, propagate = false"

        background.add(
                normal, normalLabel,
                noOcclude, noOccludeLabel,
                noOccludeNoPropagate, noOccludeNoPropagateLabel,
                noPropagate, noPropagateLabel
        )
        mainComponents.add(background)
    }

    private fun rect(x: Int, y: Int, w: Int, h: Int, color: Color): GuiComponent {
        val component = ComponentRect(x, y, w, h)
        component.color = color
        return component
    }
}
