package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.testcore.content.TestBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

public class TestBlockWithEntityImpl(config: TestBlock) : TestBlockImpl(config), BlockEntityProvider {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return config.blockEntityType!!.instantiate(pos, state)!!
    }
}