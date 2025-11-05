package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.facade.container.FacadeController
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.LibLibFacadeTest
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class SimpleInventoryController(
    windowId: Int,
    player: PlayerEntity,
    pos: BlockPos
): FacadeController(LibLibFacadeTest.simpleInventoryControllerType, windowId, player) {
    val contentsSlots: SlotManager

    init {
        val tile = player.world.getBlockEntity(pos) as SimpleInventoryBlockEntity
        contentsSlots = SlotManager(tile.inventory)

        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.all)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.all)
        createTransferRule().from(contentsSlots.all).into(playerSlots.main).into(playerSlots.hotbar)
    }

    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }
}
