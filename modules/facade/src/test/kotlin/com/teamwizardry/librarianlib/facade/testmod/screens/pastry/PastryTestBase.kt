package com.teamwizardry.librarianlib.facade.testmod.screens.pastry

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.math.rect
import java.awt.Color
import kotlin.math.max

open class PastryTestBase: GuiLayer() {
    val stack = StackLayout.build()
        .spacing(3)
        .vertical()
        .alignTop()
        .alignLeft()
        .build()

    init {
        this.add(stack)
    }

    /**
     * Add a label line
     */
    fun label(text: String) {
        this.add(PastryLabel(text))
    }

    /**
     * Add a separator line
     */
    fun separator() {
        this.add(Separator())
    }

    /**
     * Add a wider gap between elements
     */
    fun gap() {
        this.add(GuiLayer())
    }

    private class Separator: GuiLayer() {
        val line = RectLayer(Color.darkGray, 0, 0, 1, 1)

        init {
            this.add(line)
            this.height = 1.0
        }

        override fun layoutChildren() {
            super.layoutChildren()
            line.frame = rect(8, 0, max(0.0, this.width - 16), 1)
        }
    }
}