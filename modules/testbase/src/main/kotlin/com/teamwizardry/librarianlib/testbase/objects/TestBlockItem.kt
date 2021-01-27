package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.BlockItem
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResultType
import net.minecraft.util.text.Color
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

public open class TestBlockItem(block: TestBlock, builder: Properties): BlockItem(block, builder) {
    override fun getBlock(): TestBlock {
        return super.getBlock() as TestBlock
    }

    override fun placeBlock(context: BlockItemUseContext, state: BlockState): Boolean {
        if (super.placeBlock(context, state)) {
            context.item.grow(1)
            return true
        }
        return false
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if (block.config.description != null) {
            val description = TranslationTextComponent(registryName!!.translationKey("block", "tooltip"))
            description.style.applyFormatting(TextFormatting.GRAY)
            tooltip.add(description)
        }
    }
}