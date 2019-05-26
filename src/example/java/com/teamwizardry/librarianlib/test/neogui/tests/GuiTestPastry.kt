package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.components.ComponentStack
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryCheckbox
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.layers.PastryProgressBar
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryRadioButtonSet
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastrySwitch
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.dropdown.DropdownSeparatorItem
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
class GuiTestPastry : GuiBase() {
    val dropdown: PastryDropdown<ItemStack>

    init {
        main.size = vec(200, 200)

        main.add(PastryBackground(0, 0, 200, 200))
        val button = PastryButton("Some text here that's too long to fit in the text thing wow", 10, 10, 50, 12)
        main.add(button)
        button.requestFocus()

        val progress = PastryProgressBar(10, 35, 75, 5)
        progress.progress_im.animateKeyframes(0.0)
            .add(80f, 1.0, Easing.easeOutCubic)
            .add(10f, 1.0)
            .add(40f, 0.0, Easing.easeOutBounce)
            .add(10f, 0.0)
            .finish().repeatCount = -1
        main.add(progress)

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
        main.add(stackComponent, dropdown)

        val switches = switchPanel()
        switches.x = -switches.width
        switches.y = 30.0
        switches.add(PastryBackground(0, 0, switches.widthi, switches.heighti).also { it.zIndex = 0.0 })
        main.add(switches)
    }

    fun switchPanel(): GuiComponent {
        val panel = GuiComponent(0, 0, 55, 45)

        val radioSet = PastryRadioButtonSet<String>()
        panel.add(radioSet.addOption("1", 5, 5))
        panel.add(radioSet.addOption("2", 5, 15))
        panel.add(radioSet.addOption("3", 5, 25))
        panel.add(radioSet.addOption("4", 5, 35))

        val radioText = TextLayer(13, 5, 0, 0)
        radioText.text = "x"
        radioText.fitToText = true
        panel.add(radioText)

        radioSet.BUS.hook<PastryRadioButtonSet<String>.OptionSelected> {
            radioText.text = it.option ?: "x"
        }

        panel.add(PastryCheckbox(23, 5))
        panel.add(PastryCheckbox(23, 15))
        panel.add(PastryCheckbox(23, 25))
        panel.add(PastryCheckbox(23, 35))

        panel.add(PastrySwitch(35, 5))
        panel.add(PastrySwitch(35, 15))
        panel.add(PastrySwitch(35, 25))
        panel.add(PastrySwitch(35, 35))

        return panel
    }
}
