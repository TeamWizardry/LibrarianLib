package com.teamwizardry.librarianlib.common.container.internal

import com.teamwizardry.librarianlib.common.container.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot

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

    override public fun addSlotToContainer(slotIn: Slot?): Slot {
        return super.addSlotToContainer(slotIn)
    }
}
