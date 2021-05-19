package com.teamwizardry.librarianlib.facade.test.containers.base

import com.teamwizardry.librarianlib.facade.container.FacadeController
import com.teamwizardry.librarianlib.facade.container.messaging.Message
import com.teamwizardry.librarianlib.facade.test.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

class TestContainerSelectorController(windowId: Int, player: PlayerEntity, val pos: BlockPos) :
    FacadeController(LibrarianLibFacadeTestMod.testContainerSelectorContainerType, windowId, player) {
    val containerSet: TestContainerSet

    init {
        val tile = player.world.getBlockEntity(pos) as TestContainerTile
        containerSet = tile.containerSet
    }

    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }

    @Message
    private fun selectType(id: Identifier) {
        if(player is ServerPlayerEntity) {
            val type = containerSet.getType(id)
            type.containerType.open(player, LiteralText(containerSet.name), pos)
        }
    }
}
