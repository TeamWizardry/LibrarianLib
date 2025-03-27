package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.testcore.content.TestBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

public class TestBlockWithEntityImpl(config: TestBlock) : TestBlockImpl(config), BlockEntityProvider {
    private val ticker: BlockEntityTicker<BlockEntity>? = config.blockEntityTickFunction?.let { tickFunction ->
        BlockEntityTicker<BlockEntity> { _, _, _, blockEntity -> tickFunction(blockEntity) }
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        @Suppress("UNCHECKED_CAST")
        return ticker as BlockEntityTicker<T>?
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return config.blockEntityType!!.instantiate(pos, state)!!
    }
}