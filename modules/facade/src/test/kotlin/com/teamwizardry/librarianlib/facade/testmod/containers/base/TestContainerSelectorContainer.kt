package com.teamwizardry.librarianlib.facade.testmod.containers.base

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.messaging.Message
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.StringTextComponent

class TestContainerSelectorContainer(windowId: Int, player: PlayerEntity, val pos: BlockPos) :
    FacadeContainer(LibrarianLibFacadeTestMod.testContainerSelectorContainerType, windowId, player) {
    val containerSet: TestContainerSet

    init {
        val tile = player.world.getTileEntity(pos) as TestContainerTile
        containerSet = tile.containerSet
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    @Message
    private fun selectType(id: ResourceLocation) {
        if(player is ServerPlayerEntity) {
            val type = containerSet.getType(id)
            type.containerType.open(player, StringTextComponent(containerSet.name), pos)
        }
    }
}
