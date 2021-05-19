package com.teamwizardry.librarianlib.facade.test.containers.base

import com.teamwizardry.librarianlib.facade.container.FacadeController
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

abstract class TestController<T: TestContainerData>(
    val dataType: Class<T>,
    windowId: Int,
    player: PlayerEntity,
    pos: BlockPos
): FacadeController(TestContainerSet.getTypeByData(dataType).containerType, windowId, player) {
    val data: T

    init {
        val tile = player.world.getBlockEntity(pos) as TestContainerTile
        data = tile.getData(dataType)
    }

    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }
}
