package com.teamwizardry.librarianlib.test.facade.pastry

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.features.helpers.rect
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color
import kotlin.math.max

open class PastryTestBase: GuiComponent() {
    val stack = StackLayout.build()
            .space(3)
            .vertical()
            .alignTop()
            .alignLeft()
            .component()

    init {
        this.add(stack)
    }

    /**
     * Add a label line
     */
    @UseExperimental(ExperimentalBitfont::class)
    fun label(text: String) {
        stack.add(PastryLabel(text))
    }

    /**
     * Add a separator line
     */
    fun separator() {
        stack.add(Separator())
    }

    /**
     * Add a wider gap between elements
     */
    fun gap() {
        stack.add(GuiLayer())
    }

    override fun layoutChildren() {
        super.layoutChildren()
        stack.frame = this.bounds
    }

    private class Separator: GuiLayer() {
        val line = RectLayer(Color.darkGray, 0, 0, 1, 1)
        init {
            this.add(line)
            this.height = 1.0
        }

        override fun layoutChildren() {
            super.layoutChildren()
            line.frame = rect(8, 0, max(0.0, this.width-16), 1)
        }
    }
}