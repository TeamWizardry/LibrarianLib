package com.teamwizardry.librarianlib.common.container.internal

import com.teamwizardry.librarianlib.common.container.ContainerBase
import com.teamwizardry.librarianlib.common.container.InventoryWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

/**
 * Created by TheCodeWarrior
 */
class ContainerImpl(val container: ContainerBase) : Container() {
    override fun canInteractWith(playerIn: EntityPlayer?): Boolean {
        return true
    }

    init {
        container.addTo(this)
    }

    fun addSlots(inv: InventoryWrapper<*>) {
        inv.slotArray.forEach { this.addSlotToContainer(it) }
    }
}
