package com.teamwizardry.librarianlib.features.structure.dynamic

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * @author WireSegal
 * Created at 2:49 PM on 3/29/18.
 */

interface DynamicBlockInfo {
    operator fun invoke(world: IBlockAccess, position: BlockPos, orientation: EnumFacing): Boolean

    val validStates: List<IBlockState>
}

class SingleState(val state: IBlockState) : DynamicBlockInfo {
    override fun invoke(world: IBlockAccess, position: BlockPos, orientation: EnumFacing) =
            world.getBlockState(position) == state

    override val validStates = listOf(state)
}

class SingleBlock(val block: Block) : DynamicBlockInfo {
    override fun invoke(world: IBlockAccess, position: BlockPos, orientation: EnumFacing) =
            world.getBlockState(position).block == block

    override val validStates = block.blockState.validStates.toList()
}

class OfStates(vararg val states: IBlockState) : DynamicBlockInfo {
    override fun invoke(world: IBlockAccess, position: BlockPos, orientation: EnumFacing) =
            world.getBlockState(position) in states

    override val validStates = listOf(*states)
}

class OfBlocks(vararg val blocks: Block) : DynamicBlockInfo {
    override fun invoke(world: IBlockAccess, position: BlockPos, orientation: EnumFacing) =
            world.getBlockState(position).block in blocks

    override val validStates = blocks.flatMap { it.blockState.validStates }
}
