package com.teamwizardry.librarianlib.features.container.builtin

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.ITransferRule
import com.teamwizardry.librarianlib.features.container.SlotType
import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

open class SlotTypeGhost(val maxStackSize: Int = 1, val overstack: Boolean = false) : SlotType() {
    override fun stackLimit(slot: SlotBase, stack: ItemStack): Int {
        return maxStackSize
    }

    override fun handleClick(slot: SlotBase, container: ContainerBase, dragType: Int, clickType: ClickType?, player: EntityPlayer): Pair<Boolean, ItemStack> {

        val slotStack = slot.stack
        val playerStack: ItemStack = player.inventory.itemStack
        val areEqual = ITransferRule.areItemStacksEqual(slotStack, playerStack)

        if (clickType == ClickType.QUICK_MOVE && playerStack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else if (slotStack.isNotEmpty && (areEqual || playerStack.isEmpty)) {
            if (dragType == 1)
                if (playerStack.isNotEmpty) slotStack.count++ else slotStack.count--
            else
                slotStack.count += playerStack.count
            if (slotStack.count > maxStackSize) slotStack.count = maxStackSize
            if (!overstack && slotStack.count > slotStack.maxStackSize) slotStack.count = slotStack.maxStackSize
            if (slotStack.count <= 0) slot.putStack(ItemStack.EMPTY)
        } else if (slotStack.isNotEmpty && playerStack.isNotEmpty) {
            val copy = playerStack.copy()
            if (dragType == 1) copy.count = 1
            copy.count = Math.min(copy.count, maxStackSize)
            slot.putStack(copy)
        }

        return true to ItemStack.EMPTY
    }

    override fun autoTransferInto(slot: SlotBase, stack: ItemStack): ITransferRule.AutoTransferResult {
        if (slot.stack.isEmpty) {
            val newStack = stack.copy()
            newStack.count = Math.min(newStack.count, maxStackSize)

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
    override fun isValid(slot: SlotBase, stack: ItemStack, default: Boolean): Boolean {
        return types.any { stack.item.isValidArmor(stack, it, player) }
    }

    override fun stackLimit(slot: SlotBase, stack: ItemStack): Int {
        return 1
    }
}
