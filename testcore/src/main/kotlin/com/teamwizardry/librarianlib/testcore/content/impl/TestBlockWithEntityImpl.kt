package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.testcore.content.TestBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

public class TestBlockWithEntityImpl(config: TestBlock) : TestBlockImpl(config), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity {
        return config.blockEntityType!!.instantiate()!!
    }
}