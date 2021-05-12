package com.teamwizardry.librarianlib.facade.testmod.containers.base

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

abstract class TestContainer<T: TestContainerData>(
    val dataType: Class<T>,
    windowId: Int,
    player: PlayerEntity,
    pos: BlockPos
): FacadeContainer(TestContainerSet.getTypeByData(dataType).containerType, windowId, player) {
    val data: T

    init {
        val tile = player.world.getBlockEntity(pos) as TestContainerTile
        data = tile.getData(dataType)
    }

    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }
}
