package com.teamwizardry.librarianlib.facade.test.controllers.base

import com.teamwizardry.librarianlib.facade.container.FacadeController
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

abstract class TestController<T: TestControllerData>(
    val dataType: Class<T>,
    windowId: Int,
    player: PlayerEntity,
    pos: BlockPos
): FacadeController(TestControllerSet.getTypeByData(dataType).controllerType, windowId, player) {
    val data: T

    init {
        val tile = player.world.getBlockEntity(pos) as TestControllerBlockEntity
        data = tile.getData(dataType)
    }

    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }
}
