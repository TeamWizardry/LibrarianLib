package com.teamwizardry.librarianlib.common.util.saving.helpers

import com.teamwizardry.librarianlib.common.util.toNonnullList
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler

/**
 * Represents an ItemStackHandler that can have its contents directly read and modified through [backingArray]
 */
open class StaticSavableItemStackHandler(arr: Array<ItemStack?>) : ItemStackHandler(arr.toNonnullList(ItemStack.EMPTY)) {
    constructor(slots: Int) : this(arrayOfNulls(slots))

    open val backingArray: Array<ItemStack?>
        get() = stacks.toTypedArray()
}

/**
 * Represents an ItemStackHandler that can have its contents directly read and modified through [backingArray]
 *
 * Almost identical to [StaticSavableItemStackHandler] except that the backing array can be completely replaced,
 * allowing the caller to change the size of the inventory
 */
class SavableItemStackHandler(arr: Array<ItemStack?>) : StaticSavableItemStackHandler(arr) {
    constructor(slots: Int) : this(arrayOfNulls(slots))

    override var backingArray: Array<ItemStack?>
        get() = stacks.toTypedArray()
        set(value) {
            stacks = value.toNonnullList(ItemStack.EMPTY)
        }
}
