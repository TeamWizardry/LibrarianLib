package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.core.util.kotlin.makeTranslationKey
import com.teamwizardry.librarianlib.testcore.content.TestBlock
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

public open class TestBlockItem(block: TestBlockImpl, builder: Settings): BlockItem(block, builder) {
    public val config: TestBlock = block.config

    override fun getBlock(): TestBlockImpl {
        return super.getBlock() as TestBlockImpl
    }

    override fun place(context: ItemPlacementContext, state: BlockState): Boolean {
        if(super.place(context, state)) {
            context.stack.increment(1)
            return true
        }
        return false
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        type: TooltipType
    ) {
        super.appendTooltip(stack, context, tooltip, type)

        if (block.config.description != null) {
            val description = Text.translatable(block.config.id.makeTranslationKey("block", "tooltip"))
            description.style.withFormatting(Formatting.GRAY)
            tooltip.add(description)
        }
    }
}