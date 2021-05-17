package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class SimpleInventoryContainer(
    windowId: Int,
    player: PlayerEntity,
    pos: BlockPos
): FacadeContainer(LibrarianLibFacadeTestMod.simpleInventoryContainerType, windowId, player) {
    val contentsSlots: SlotManager

    init {
        val tile = player.world.getBlockEntity(pos) as SimpleInventoryTile
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
