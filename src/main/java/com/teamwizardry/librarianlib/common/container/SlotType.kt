package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack

open class SlotType {

    /**
     * Returned form the slot `isItemValid` method. Passed the result of the default method
     */
    open fun isValid(slot: SlotBase, stack: ItemStack?, default: Boolean): Boolean { return default }

    /**
     * Returned from the slot `getSlotStackLimit` method.
     */
    open fun stackLimit(slot: SlotBase, stack: ItemStack?): Int { return 64 }

    /**
     * Returned from the slot `canTakeStack` method, passed the result of the default method.
     */
    open fun canTake(slot: SlotBase, player: EntityPlayer?, stack: ItemStack?, default: Boolean): Boolean { return default }

    /**
     * try to shift click the item into this slot
     */
    open fun autoTransferInto(slot: SlotBase, stack: ItemStack): ITransferRule.AutoTransferResult {
        if(!slot.isItemValid(stack))
            return ITransferRule.AutoTransferResult(stack,  false)
        val slotStack = slot.stack

        if(slotStack.isEmpty) {
            val leftOver = stack.copy()
            val quantity = Math.min(slot.slotStackLimit, leftOver.count)

            val insert = leftOver.copy()
            insert.count = quantity
            slot.putStack(insert)

            leftOver.count -= quantity
            return ITransferRule.AutoTransferResult(if(leftOver.count <= 0) ItemStack.EMPTY else leftOver, true)
        }
        if(ITransferRule.areItemStacksEqual(stack, slotStack)) {
            val combinedSize = stack.count + slotStack.count
            val maxStackSize = Math.min(slot.getItemStackLimit(stack), stack.maxStackSize)

            if(combinedSize <= maxStackSize) {
                val newStack = slotStack.copy()
                newStack.count = combinedSize
                slot.putStack(newStack)

                return ITransferRule.AutoTransferResult(ItemStack.EMPTY, true)
            } else {
                val leftoverStack = stack.copy()
                leftoverStack.count -= maxStackSize - slotStack.count

                val newStack = slotStack.copy()
                newStack.count = maxStackSize
                slot.putStack(newStack)

                return ITransferRule.AutoTransferResult(leftoverStack, true)
            }
        }

        return ITransferRule.AutoTransferResult(stack, false)
    }

    companion object {
        @JvmStatic
        val BASIC = SlotType()
    }

    /**
     * handle a click on the slot, the first value in the returned pair is whether the click had custom handling, and the
     * second is the value to return from the container slot click method. If the click was handled the default slot click
     * handling is not run.
     */
    open fun handleClick(slot: SlotBase, container: ContainerBase, dragType: Int, clickType: ClickType?, player: EntityPlayer): Pair<Boolean, ItemStack?> {
        return false to null
    }

    /**
     * Called in the slot `onPickupFromSlot` method. If this returns true the normal handling will proceed, false will
     * override the default handling causing it not to be called.
     */
    open fun onPickup(slot: SlotBase, player: EntityPlayer?, stack: ItemStack): Boolean { return true }

    /**
     * Called in the slot `onSlotChange()` method, return false to cancel default handling.
     */
    open fun onSlotChange(slot: SlotBase): Boolean { return true }

    /**
     * Called in the slot `onSlotChange(old, new)` method, return false to cancel default handling.
     */
    open fun onSlotChange(slot: SlotBase, old: ItemStack, new: ItemStack): Boolean { return true }

    /**
     * Called in the slot `putStack` method, return false to cancel default handling
     */
    open fun putStack(slot: SlotBase, stack: ItemStack): Boolean { return true }


    /**
     * Returned from the slot `getStack` method. It is passed the default stack, and may modify or entirely replace the
     * return value
     */
    open fun getStack(slot: SlotBase, defaultStack: ItemStack): ItemStack { return defaultStack }

    /**
     * Returned from the slot `canBeHovered` method.
     */
    open fun canHover(slot: SlotBase): Boolean { return true }

}

