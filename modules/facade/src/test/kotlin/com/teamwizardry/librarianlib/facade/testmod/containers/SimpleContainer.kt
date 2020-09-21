package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.messaging.Message
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.StringTextComponent

class SimpleContainer(windowId: Int, player: PlayerEntity): FacadeContainer(LibrarianLibFacadeTestMod.simpleContainerType, windowId, player) {
    init {
        for (i in 0..2) {
            for (j in 0..8) {
                addSlot(Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (k in 0..8) {
            addSlot(Slot(player.inventory, k, 8 + k * 18, 142))
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    @Message
    private fun mouseClicked(position: Vec2d, button: Int) {
        val prefix = if(isClientContainer) "[Client]" else "[Server]"
        player.sendMessage(StringTextComponent("$prefix mouseClicked: $position, $button"))
    }

    @Message
    private fun mouseReleased(position: Vec2d, button: Int) {
        val prefix = if(isClientContainer) "[Client]" else "[Server]"
        player.sendMessage(StringTextComponent("$prefix mouseReleased: $position, $button"))
    }
}