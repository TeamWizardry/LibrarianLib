package com.teamwizardry.librarianlib.common.container.internal

import com.teamwizardry.librarianlib.common.container.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
class ContainerImpl(val container: ContainerBase) : Container() {

    override fun canInteractWith(playerIn: EntityPlayer?): Boolean {
        return true
    }

    init {
        container.impl = this
        container.addContainerSlots()
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType?, player: EntityPlayer): ItemStack {
        if (slotId > 0 && slotId < inventorySlots.size) {
            val slot = inventorySlots[slotId] as SlotBase

            val pair = slot.handleClick(container, dragType, clickTypeIn, player)
            if (pair.first) {
                return pair.second
            }
        }

        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? {
        return container.transferStackInSlot(inventorySlots[index] as SlotBase)
    }

    override public fun addSlotToContainer(slotIn: Slot?): Slot {
        return super.addSlotToContainer(slotIn)
    }
}
