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
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

    @Message
    private fun buttonClick(position: Vec2d) {
        val prefix = if(isClientContainer) "[Client Container]" else "[Server Container]"
        player.sendStatusMessage(StringTextComponent("$prefix button click: $position"), false)
    }
}