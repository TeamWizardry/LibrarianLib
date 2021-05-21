package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.facade.test.controllers.base.TestController
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class JeiIngredientLayerController(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestController<JeiIngredientLayerController.Data>(Data::class.java, windowId, player, pos) {
    init {
    }

    class Data: TestControllerData() {
    }
}