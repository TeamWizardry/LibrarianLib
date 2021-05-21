package com.teamwizardry.librarianlib.facade.test.controllers.base

import com.teamwizardry.librarianlib.facade.container.FacadeController
import com.teamwizardry.librarianlib.facade.container.messaging.Message
import com.teamwizardry.librarianlib.facade.test.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class TestControllerSelectorController(windowId: Int, player: PlayerEntity, val pos: BlockPos) :
    FacadeController(LibrarianLibFacadeTestMod.testContainerSelectorContainerType, windowId, player) {
    val controllerSet: TestControllerSet

    init {
        val tile = player.world.getBlockEntity(pos) as TestControllerBlockEntity
        controllerSet = tile.controllerSet
    }

    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }

    @Message
    private fun selectType(id: Identifier) {
        if(player is ServerPlayerEntity) {
            val type = controllerSet.getType(id)
            type.containerType.open(player, LiteralText(controllerSet.name), pos)
        }
    }
}
