package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.layers.minecraft.ItemStackLayer
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.facade.pastry.layers.dropdown.DropdownSeparatorItem
import com.teamwizardry.librarianlib.facade.pastry.layers.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.facade.pastry.layers.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase
import com.teamwizardry.librarianlib.math.Align2d
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

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
            ItemStack(Blocks.OAK_PLANKS)
        )
        val stackLayer = ItemStackLayer(0, 0)

        val dropdownWidth = stacks.map { stack ->
            stack?.let { PastryLabel(0, 0, it.displayName.string).widthi } ?: 0
        }.maxOrNull() ?: 50
        dropdown = PastryDropdown(0, 0, dropdownWidth + 15) {
            stackLayer.stack = it
        }
        dropdown.items.addAll(stacks.map { stack ->
            stack?.let { DropdownTextItem(it, it.displayName.string) } ?: DropdownSeparatorItem<ItemStack>()
        })

        val horizontalStack = StackLayout.build()
            .add(stackLayer, dropdown)
            .spacing(5)
            .horizontal()
            .fit()
            .align(Align2d.CENTER_LEFT)
            .build()
        stack.add(horizontalStack)
    }

    init {
        val dropdown: PastryDropdown<Int>
        val label = PastryLabel(0, 0, "Long dropdown:")
        val valueLabel = PastryLabel(0, 0)
//        valueLabel.fitToText = true TODO

        val dropdownWidth = PastryLabel(0, 0, "FizzBuzz").widthi
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
            .build()
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