package com.teamwizardry.librarianlib.facade.container

import net.minecraft.inventory.Inventory
import net.minecraft.util.collection.DefaultedList
import net.minecraft.item.ItemStack
import net.minecraft.inventory.Inventories
import net.minecraft.entity.player.PlayerEntity

/**
 * A simple `Inventory` implementation with only default methods + an item list getter.
 *
 *
 * Originally by Juuz
 */
public interface DefaultInventoryImpl : Inventory {
    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    public val items: DefaultedList<ItemStack>

    /**
     * Returns the inventory size.
     */
    override fun size(): Int {
        return items.size
    }

    /**
     * Checks if the inventory is empty.
     *
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    override fun isEmpty(): Boolean {
        for (i in 0 until size()) {
            val stack = getStack(i)
            if (!stack.isEmpty) {
                return false
            }
        }
        return true
    }

    /**
     * Retrieves the item in the slot.
     */
    override fun getStack(slot: Int): ItemStack {
        return items[slot]
    }

    /**
     * Removes items from an inventory slot.
     *
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     * takes all items in that slot.
     */
    override fun removeStack(slot: Int, count: Int): ItemStack {
        val result = Inventories.splitStack(items, slot, count)
        if (!result.isEmpty) {
            markDirty()
        }
        return result
    }

    /**
     * Removes all items from an inventory slot.
     *
     * @param slot The slot to remove from.
     */
    override fun removeStack(slot: Int): ItemStack {
        return Inventories.removeStack(items, slot)
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     *
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     * this inventory ([Inventory.getMaxCountPerStack]),
     * it gets resized to this inventory's maximum amount.
     */
    override fun setStack(slot: Int, stack: ItemStack) {
        items[slot] = stack
        if (stack.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    /**
     * Clears the inventory.
     */
    override fun clear() {
        items.clear()
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    override fun markDirty() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return true
    }

    public companion object {
        /**
         * Creates an inventory from the item list.
         */
        @JvmStatic
        public fun of(items: DefaultedList<ItemStack>): DefaultInventoryImpl {
            return object : DefaultInventoryImpl {
                override val items: DefaultedList<ItemStack> = items
            }
        }

        /**
         * Creates a new inventory with the specified size.
         */
        @JvmStatic
        public fun ofSize(size: Int): DefaultInventoryImpl {
            return of(DefaultedList.ofSize(size, ItemStack.EMPTY))
        }
    }
}