package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class SimpleInventoryContainer(
    windowId: Int,
    player: PlayerEntity,
    pos: BlockPos
): FacadeContainer(LibrarianLibFacadeTestMod.simpleInventoryContainerType, windowId, player) {
    val contentsSlots: SlotManager

    init {
        val tile = player.world.getTileEntity(pos) as SimpleInventoryTile
        contentsSlots = SlotManager(tile.inventory)

        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.slots)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.slots)
        createTransferRule().from(contentsSlots.slots).into(playerSlots.main).into(playerSlots.hotbar)
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}
