package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.components.ComponentItemStack
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.DropdownSeparatorItem
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

@UseExperimental(ExperimentalBitfont::class)
class PastryTestDropdown: PastryTestBase() {


    init {
        val dropdown: PastryDropdown<ItemStack>
        val stacks = listOf(
            ItemStack(Items.DIAMOND_SWORD),
            ItemStack(Items.DIAMOND_PICKAXE),
            ItemStack(Items.DIAMOND_AXE),
            ItemStack(Items.DIAMOND_SHOVEL),
            ItemStack(Items.DIAMOND_HOE),
            null,
            ItemStack(Items.DIAMOND),
            ItemStack(Items.GOLD_INGOT),
            ItemStack(Items.IRON_INGOT),
            ItemStack(Blocks.COBBLESTONE),
            ItemStack(Blocks.PLANKS)
        )
        val stackComponent = ComponentItemStack(0, 0)

        val dropdownWidth = stacks.map { stack ->
            stack?.let { PastryLabel.stringSize(it.displayName).widthi } ?: 0
        }.max() ?: 50
        dropdown = PastryDropdown(0, 0, dropdownWidth + 15) {
            stackComponent.stack = it
        }
        dropdown.items.addAll(stacks.map { stack ->
            stack?.let { DropdownTextItem(it, it.displayName) } ?: DropdownSeparatorItem<ItemStack>()
        })

        val horizontalStack = StackLayout.build()
            .add(stackComponent, dropdown)
            .spacing(5)
            .horizontal()
            .fit()
            .align(Align2d.CENTER_LEFT)
            .component()
        stack.add(horizontalStack)
    }

    init {
        val dropdown: PastryDropdown<Int>
        val label = PastryLabel(0, 0, "Long dropdown:")
        val valueLabel = PastryLabel(0, 0)
        valueLabel.fitToText = true

        val dropdownWidth = PastryLabel.stringSize("FizzBuzz").widthi
        dropdown = PastryDropdown(0, 0, dropdownWidth + 15) {
            valueLabel.text = "$it"
        }

        dropdown.items.addAll((1 until 50).map { num ->
            DropdownTextItem(num, fizzBuzz(num))
        })

        dropdown.select(20)

        val horizontalStack = StackLayout.build()
            .add(label, valueLabel, dropdown)
            .spacing(5)
            .horizontal()
            .fit()
            .component()
        stack.add(horizontalStack)
    }

    private fun fizzBuzz(num: Int): String {
        return when {
            num % 3 == 0 && num % 5 == 0 -> "FizzBuzz"
            num % 3 == 0 -> "Fizz"
            num % 5 == 0 -> "Buzz"
            else -> "$num"
        }
    }
}