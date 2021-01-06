package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.core.util.kotlin.getOrNull
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraftforge.items.CapabilityItemHandler
import java.lang.IllegalStateException

class BackpackContainer(
    windowId: Int,
    player: PlayerEntity,
    hand: Hand
): FacadeContainer(LibrarianLibFacadeTestMod.backpackContainerType, windowId, player) {
    val contentsSlots: SlotManager

    init {
        val stack = player.getHeldItem(hand)
        if(stack.item != LibrarianLibFacadeTestMod.backpackItem)
            throw IllegalStateException("Held item isn't a backpack")
        val stackCap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).getOrNull()
            ?: throw IllegalStateException("Held item doesn't have an item handler")

        contentsSlots = SlotManager(stackCap)

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
