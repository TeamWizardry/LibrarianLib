package com.teamwizardry.librarianlib.common.container.builtin

import com.teamwizardry.librarianlib.common.container.ContainerBase
import com.teamwizardry.librarianlib.common.container.ITransferRule
import com.teamwizardry.librarianlib.common.container.SlotType
import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class SlotTypeGhost(val maxStackSize: Int = 1, val overstack: Boolean = false) : SlotType() {
    override fun stackLimit(slot: SlotBase, stack: ItemStack?): Int {
        return maxStackSize
    }

    override fun handleClick(slot: SlotBase, container: ContainerBase, dragType: Int, clickType: ClickType?, player: EntityPlayer): Pair<Boolean, ItemStack?> {

        val slotStack = slot.stack
        val playerStack = player.inventory.itemStack
        val areEqual = ITransferRule.areItemStacksEqual(slotStack, playerStack)

        if (clickType == ClickType.QUICK_MOVE && playerStack == null) {
            slot.putStack(null)
        } else if (slotStack != null && (areEqual || playerStack == null)) {
            if (dragType == 1)
                if (playerStack != null) slotStack.stackSize++ else slotStack.stackSize--
            else
                slotStack.stackSize += playerStack?.stackSize ?: 1
            if (slotStack.stackSize > maxStackSize) slotStack.stackSize = maxStackSize
            if (!overstack && slotStack.stackSize > slotStack.maxStackSize) slotStack.stackSize = slotStack.maxStackSize
            if (slotStack.stackSize <= 0) slot.putStack(null)
        } else if (slotStack == null && playerStack != null) {
            val copy = playerStack.copy()
            if (dragType == 1) copy.stackSize = 1
            copy.stackSize = Math.min(copy.stackSize, maxStackSize)
            slot.putStack(copy)
        }

        return true to null
    }

    override fun autoTransferInto(slot: SlotBase, stack: ItemStack): ITransferRule.AutoTransferResult {
        if (slot.stack == null) {
            val newStack = stack.copy()
            newStack.stackSize = Math.min(newStack.stackSize, maxStackSize)

            slot.putStack(newStack)
            return ITransferRule.AutoTransferResult(stack, true, false)
        }
        if (ITransferRule.areItemStacksEqual(slot.stack, stack)) {
            return ITransferRule.AutoTransferResult(stack, false, false)
        }
        return ITransferRule.AutoTransferResult(stack, false)
    }
}

open class SlotTypeEquipment(val player: EntityPlayer, vararg val types: EntityEquipmentSlot) : SlotType() {
    override fun isValid(slot: SlotBase, stack: ItemStack?, default: Boolean): Boolean {
        return types.any { stack?.item?.isValidArmor(stack, it, player) ?: false }
    }

    override fun stackLimit(slot: SlotBase, stack: ItemStack?): Int {
        return 1
    }
}
