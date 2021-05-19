package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.facade.container.FacadeController
import com.teamwizardry.librarianlib.facade.container.builtin.ContainerLock
import com.teamwizardry.librarianlib.facade.container.builtin.LockingSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraftforge.items.CapabilityItemHandler
import java.lang.IllegalStateException

class BackpackController(
    windowId: Int,
    player: PlayerEntity,
    hand: Hand
): FacadeController(LibrarianLibFacadeTestMod.backpackContainerType, windowId, player) {
    val contentsSlots: SlotManager
    val lock: ContainerLock.ConsistencyLock

    init {
        val stack = player.getHeldItem(hand)
        if(stack.item != LibrarianLibFacadeTestMod.backpackItem)
            throw IllegalStateException("Held item isn't a backpack")
        val stackCap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).getOrNull()
            ?: throw IllegalStateException("Held item doesn't have an item handler")

        lock = ContainerLock.ConsistencyLock(isClientContainer) {
            player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        }
        contentsSlots = SlotManager(stackCap)
        contentsSlots.all.setFactory { inv, index -> LockingSlot(inv, index, lock) }
        playerSlots.getHandSlot(hand).setFactory { inv, index -> LockingSlot(inv, index, ContainerLock.LOCKED) }

        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.all)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.all)
        createTransferRule().from(contentsSlots.all).into(playerSlots.main).into(playerSlots.hotbar)
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return !lock.isLocked()
    }
}
