package com.teamwizardry.librarianlib.common.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 * @param D Data type. Used to pass additional data to the slot.
 */
open class SlotType {

//    open fun onChange(player: EntityPlayer, from: ItemStack?, to: ItemStack?) {}
//    open fun tryPickUp(player: EntityPlayer, )

    open fun onSlotChange(player: EntityPlayer?, from: ItemStack?, to: ItemStack?) {}
    open fun onPickUp(player: EntityPlayer, stack: ItemStack) {}

    open fun isValid(player: EntityPlayer?, stack: ItemStack?): Boolean { return true }
    open fun stackLimit(player: EntityPlayer?, stack: ItemStack): Int { return 64 }
    open fun canTake(player: EntityPlayer, stack: ItemStack): Boolean { return true }
    open fun canHover(): Boolean { return true }



    companion object {
        @JvmStatic
        val BASIC = SlotType()
    }
}
