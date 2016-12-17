package com.teamwizardry.librarianlib.common.base

import com.teamwizardry.librarianlib.common.util.toNonnullList
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

/**
 * @author WireSegal
 * Created at 4:35 PM on 10/28/16.
 */
class ExpandedItemStackHandler : ItemStackHandler {

    var changedCallback: (ExpandedItemStackHandler.(Int) -> Unit)? = null
    var slotPredicate: (ExpandedItemStackHandler.(Int, ItemStack) -> Boolean)? = null

    constructor(size: Int = 1) : super(size)
    constructor(stacks: Array<ItemStack>) : super(stacks.toNonnullList())

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

        val existing = this.stacks[slot]

        var limit = getStackLimit(slot, stack)
        val canInsert = canInsertIntoSlot(slot, stack)
        if (!canInsert) return stack

        if (!existing.isEmpty) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack

            limit -= existing.count
        }

        if (limit <= 0)
            return stack

        val reachedLimit = stack.count > limit

        if (!simulate) {
            if (existing.isEmpty) {
                this.stacks[slot] = if (reachedLimit) ItemHandlerHelper.copyStackWithSize(stack, limit) else stack
            } else {
                existing.grow(if (reachedLimit) limit else stack.count)
            }
            onContentsChanged(slot)
        }

        return if (reachedLimit) ItemHandlerHelper.copyStackWithSize(stack, stack.count - limit) else ItemStack.EMPTY
    }

    fun canInsertIntoSlot(slot: Int, stack: ItemStack): Boolean {
        return slotPredicate?.invoke(this, slot, stack) ?: true
    }

    override fun onContentsChanged(slot: Int) {
        changedCallback?.invoke(this, slot)
    }
}
