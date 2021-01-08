package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraft.inventory.container.Slot
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

public open class FacadeSlot(itemHandler: IItemHandler, index: Int, xPosition: Int, yPosition: Int) :
    SlotItemHandler(itemHandler, index, xPosition, yPosition) {

    // Fix MinecraftForge#7581
    override fun isSameInventory(other: Slot): Boolean {
        return other is SlotItemHandler && other.itemHandler == this.itemHandler
    }
}