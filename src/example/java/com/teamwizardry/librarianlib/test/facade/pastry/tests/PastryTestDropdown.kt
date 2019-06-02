package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.components.ComponentStack
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.DropdownSeparatorItem
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

@UseExperimental(ExperimentalBitfont::class)
class PastryTestDropdown: PastryTestBase() {

    val dropdown: PastryDropdown<ItemStack>

    init {
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
        val stackComponent = ComponentStack(10, 45)

        val dropdownWidth = stacks.map { stack ->
            stack?.let { TextLayer.stringSize(it.displayName).widthi } ?: 0
        }.max() ?: 50
        dropdown = PastryDropdown(30, 47, dropdownWidth + 15) {
            if(it != null)
                stackComponent.stack = it
        }
        dropdown.items.addAll(stacks.map { stack ->
            stack?.let { DropdownTextItem(it, it.displayName) } ?: DropdownSeparatorItem<ItemStack>()
        })

        val horizontalStack = StackLayout.build()
            .add(stackComponent, dropdown)
            .space(5)
            .horizontal()
            .fit()
            .component()
        stack.add(horizontalStack)
    }
}