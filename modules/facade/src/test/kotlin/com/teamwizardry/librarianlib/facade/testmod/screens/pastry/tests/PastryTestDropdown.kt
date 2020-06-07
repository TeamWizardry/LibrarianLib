package com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.layers.minecraft.ItemStackLayer
import com.teamwizardry.librarianlib.facade.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.facade.pastry.components.dropdown.DropdownSeparatorItem
import com.teamwizardry.librarianlib.facade.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.facade.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestBase
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
        val stackComponent = ItemStackLayer(0, 0)

        val dropdownWidth = stacks.map { stack ->
            64 // stack?.let { PastryLabel.stringSize(it.displayName).widthi } ?: 0 TODO
        }.max() ?: 50
        dropdown = PastryDropdown(0, 0, dropdownWidth + 15) {
            stackComponent.stack = it
        }
        dropdown.items.addAll(stacks.map { stack ->
            stack?.let { DropdownTextItem(it, it.displayName.string) } ?: DropdownSeparatorItem<ItemStack>()
        })

        val horizontalStack = StackLayout.build()
            .add(stackComponent, dropdown)
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

        val dropdownWidth = 30 // PastryLabel.stringSize("FizzBuzz").widthi TODO
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