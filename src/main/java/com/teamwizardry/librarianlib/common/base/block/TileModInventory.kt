package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*

/**
 * Created by TheCodeWarrior
 */
open class TileModInventory(val size: Int) : TileMod(), IItemHandlerModifiable {

    open fun getStackLimit(slot: Int, stack: ItemStack): Int {
        return stack.maxStackSize
    }

    override fun getSlotLimit(slot: Int): Int {
        return 64
    }

    open fun onContentsChanged(slot: Int) {
        this.markDirty()
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        validateSlotIndex(slot)
        return stacks[slot]
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        validateSlotIndex(slot)
        if (ItemStack.areItemStacksEqual(this.stacks[slot], stack))
            return
        this.stacks[slot] = stack
        onContentsChanged(slot)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

        val existing = this.stacks[slot]

        var limit = getStackLimit(slot, stack)

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

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (amount == 0)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

        val existing = this.stacks[slot]

        if (existing.isEmpty)
            return ItemStack.EMPTY

        val toExtract = Math.min(amount, existing.maxStackSize)

        if (existing.count <= toExtract) {
            if (!simulate) {
                this.stacks[slot] = ItemStack.EMPTY
                onContentsChanged(slot)
            }
            return existing
        } else {
            if (!simulate) {
                this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.count - toExtract)
                onContentsChanged(slot)
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract)
        }
    }

    override fun getSlots(): Int {
        return stacks.size
    }


    protected fun validateSlotIndex(slot: Int) {
        if (slot < 0 || slot >= stacks.size)
            throw RuntimeException("Slot $slot not in valid range - [0,${stacks.size})")
    }

    var stacks = Array<ItemStack>(size) { ItemStack.EMPTY }
        @SaveMethodGetter("stacks")
        get
        @SaveMethodSetter("stacks")
        set(value) {
            if (value.size != size) {
                field = Arrays.copyOf(value, size)
            } else {
                field = value
            }
        }
}
