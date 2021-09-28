package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.layers.minecraft.ItemStackLayer
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.pastry.layers.*
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import java.awt.Color

class PastryTestTooltips: PastryTestBase() {
    init {
        this.stack.add(PastryButton("Truncated text", 0, 0, 50).also {
            it.tooltipText = "Tooltips!"
        })
        this.stack.add(PastryButton("Short text", 0, 0, 100).also {
            it.tooltipText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Duis ultricies sit amet purus vel sagittis. Nunc commodo est est."
        })
        this.stack.add(PastryButton("Complex", 0, 0, 100).also {
            it.tooltip = ComplexTooltip(
                ItemStack(Items.GOLDEN_APPLE),
                "To eat a golden apple, press and hold use while it is selected in the hotbar. " +
                    "Both restore 4 points of hunger and 9.6 hunger saturation."
            )
        })
        this.stack.add(PastryButton("Vanilla", 0, 0, 100).also {
            val tt = VanillaTooltip()
            it.tooltip = tt
            tt.lines = listOf(
                "§6§nI'm important",
                "Wheee! Such §evanilla§r, much §7plain",
                "Many lines!"
            )
        })
        this.stack.add(PastryButton("Faux vanilla", 0, 0, 100).also {
            val tt = PastryBasicTooltip(vanilla = true)
            it.tooltip = tt
            tt.text = "§6§nI'm important\n§rLooks vanilla, but it's actually not! It's like vanilla tooltips but not " +
                    "garbage!"
        })

        this.stack.add(PastryButton("ItemStack", 0, 0, 100).also {
            val tt = ItemStackTooltip()
            it.tooltip = tt
            val stack = ItemStack(Items.DIAMOND_AXE, 3)
            tt.stack = stack
        })

        this.stack.add(GuiLayer(0, 0, 200, 50).also { outer ->
            outer.add(RectLayer(Color.WHITE).also {
                it.frame = outer.bounds
            })
            outer.add(GuiLayer(150, 10, 40, 30).also { inner ->
                inner.add(RectLayer(Color.BLUE).also {
                    it.frame = inner.bounds
                })
            })
            outer.tooltipText = "Outer tooltip"
            outer.add(PastryButton("Short text", 10, 10, 100).also {
                it.tooltipText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Duis ultricies sit amet purus vel sagittis. Nunc commodo est est."
            })
        })
    }
}

class ComplexTooltip(val stack: ItemStack, val text: String): PastryTooltip() {
    private val textLayer = TextLayer(18, 1)
    private val itemStackLayer = ItemStackLayer(1, 1)

    init {
        contents.add(itemStackLayer, textLayer)
        textLayer.color = Color.WHITE

        itemStackLayer.stack = stack
        textLayer.text = text
    }

    override fun layoutContents(maxWidth: Double) {
        textLayer.width = maxWidth - 20
        textLayer.fitToText(TextFit.VERTICAL_SHRINK)

        contents.size = vec(textLayer.frame.maxX + 2, textLayer.height + 2)
    }
}
