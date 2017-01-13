package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack

open class SlotType {

    open fun isValid(slot: SlotBase, stack: ItemStack?): Boolean { return true }
    open fun stackLimit(slot: SlotBase, stack: ItemStack?): Int { return 64 }
    open fun canTake(slot: SlotBase, player: EntityPlayer?, stack: ItemStack?): Boolean { return true }

    /**
     * try to shift click the item into this slot
     */
    open fun autoTransferInto(slot: SlotBase, stack: ItemStack): ITransferRule.AutoTransferResult {
        if(!slot.isItemValid(stack))
            return ITransferRule.AutoTransferResult(stack,  false)
        val slotStack = slot.stack

        if(slotStack == null) {
            val leftOver = stack.copy()
            val quantity = Math.min(slot.slotStackLimit, leftOver.stackSize)

            val insert = leftOver.copy()
            insert.stackSize = quantity
            slot.putStack(insert)

            leftOver.stackSize -= quantity
            return ITransferRule.AutoTransferResult(if(leftOver.stackSize <= 0) null else leftOver, true)
        }
        if(ITransferRule.areItemStacksEqual(stack, slotStack)) {
            val combinedSize = stack.stackSize + slotStack.stackSize
            val maxStackSize = Math.min(slot.getItemStackLimit(stack), stack.maxStackSize)

            if(combinedSize <= maxStackSize) {
                val newStack = slotStack.copy()
                newStack.stackSize = combinedSize
                slot.putStack(newStack)

                return ITransferRule.AutoTransferResult(null, true)
            } else {
                val leftoverStack = stack.copy()
                leftoverStack.stackSize -= maxStackSize - slotStack.stackSize

                val newStack = slotStack.copy()
                newStack.stackSize = maxStackSize
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
}

