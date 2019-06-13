package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.layers.ItemStackLayer
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.ItemStackTooltip
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryTooltip
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.VanillaTooltip
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import java.awt.Color

@UseExperimental(ExperimentalBitfont::class)
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
                "Wheee! Such §evanilla§r, much §7plain"
            )
        })

        this.stack.add(PastryButton("ItemStack", 0, 0, 100).also {
            val tt = ItemStackTooltip()
            it.tooltip = tt
            val stack = ItemStack(Items.DIAMOND_AXE, 3)
            tt.stack = stack
        })
    }
}

@UseExperimental(ExperimentalBitfont::class)
class ComplexTooltip(val stack: ItemStack, val text: String): PastryTooltip() {
    private val textLayer = TextLayer(18, 1)
    private val itemStackLayer = ItemStackLayer(1, 1)

    init {
        contents.add(itemStackLayer, textLayer)
        textLayer.color = Color.WHITE
        textLayer.wrap = true

        itemStackLayer.stack = stack
        textLayer.text = text
    }

    override fun layoutContents(maxWidth: Double) {
        textLayer.width = maxWidth - 20
        textLayer.fitToText()

        contents.size = vec(textLayer.textFrame.maxX + 2, textLayer.height + 2)
    }
}
