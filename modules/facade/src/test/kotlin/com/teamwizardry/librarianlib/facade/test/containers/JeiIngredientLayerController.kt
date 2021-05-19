package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.facade.test.containers.base.TestController
import com.teamwizardry.librarianlib.facade.test.containers.base.TestContainerData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class JeiIngredientLayerController(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestController<JeiIngredientLayerController.Data>(Data::class.java, windowId, player, pos) {
    init {
    }

    class Data: TestContainerData() {
    }
}