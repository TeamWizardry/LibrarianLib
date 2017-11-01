package com.teamwizardry.librarianlib.features.properties.context

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * @author WireSegal
 * Created at 5:05 PM on 11/1/17.
 */
data class ItemPropertyContext(val stack: ItemStack) : IPropertyContext {
    override fun getThis(): Item = stack.item
}

data class BlockPropertyContext(val block: IBlockState, val pos: BlockPos, val world: IBlockAccess) : IPropertyContext {
    override fun getThis(): Block = block.block
}

data class GenericPropertyContext<out T : Any>(val obj: T) : IPropertyContext {
    override fun getThis(): T = obj
}
