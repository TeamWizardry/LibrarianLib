package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.testmod.containers.base.TestContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.base.TestContainerData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class JeiIngredientLayerContainer(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestContainer<JeiIngredientLayerContainer.Data>(Data::class.java, windowId, player, pos) {
    init {
    }

    class Data: TestContainerData() {
    }
}