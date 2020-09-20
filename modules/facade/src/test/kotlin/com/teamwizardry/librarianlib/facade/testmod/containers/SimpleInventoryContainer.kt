package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.core.util.kotlin.getOrNull
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler
import java.lang.IllegalStateException

class SimpleInventoryContainer(windowId: Int, player: PlayerEntity, pos: BlockPos): FacadeContainer(LibrarianLibFacadeTestMod.simpleInventoryContainerType, windowId) {
    init {
        for (i in 0..2) {
            for (j in 0..8) {
                addSlot(Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (k in 0..8) {
            addSlot(Slot(player.inventory, k, 8 + k * 18, 142))
        }

        val tile = player.world.getTileEntity(pos) as SimpleInventoryTile

        for(i in 0..15) {
            addSlot(SlotItemHandler(tile.inventory, i, i * 18, 0))
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }
}
