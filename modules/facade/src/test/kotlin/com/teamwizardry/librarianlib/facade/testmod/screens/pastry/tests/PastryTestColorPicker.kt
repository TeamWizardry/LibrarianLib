package com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryColorPicker
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestBase
import java.awt.Color

class PastryTestColorPicker: PastryTestBase() {
    init {
        val colorSwatch = RectLayer(Color.WHITE, 0, 0, 10, 10)
        val colorLabel = PastryLabel(0, 0, "#")
        val container = GuiLayer(175, 150)

        colorLabel.textFitting = TextLayer.FitType.HORIZONTAL

        val colorPicker = PastryColorPicker()
        colorPicker.pos = vec(10, 10)
        container.add(colorPicker)
        colorPicker.hook<PastryColorPicker.ColorChangeEvent> {
            colorSwatch.color = it.color
            colorLabel.text = Integer.toHexString(it.color.rgb).padStart(6, '0')
        }

        this.stack.add(StackLayout.build()
            .horizontal()
            .alignCenterY()
            .spacing(3)
            .add(colorSwatch, colorLabel)
            .fitBreadth()
            .build()
        )
        this.stack.add(container)
    }
}