package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem
import net.minecraft.item.BlockItemUseContext
import net.minecraft.util.ActionResultType

class TestBlockItem(block: TestBlock, builder: Properties): BlockItem(block, builder) {
    override fun getBlock(): TestBlock {
        return super.getBlock() as TestBlock
    }

    override fun placeBlock(context: BlockItemUseContext, state: BlockState): Boolean {
        if(super.placeBlock(context, state)) {
            context.item.grow(1)
            return true
        }
        return false
    }
}