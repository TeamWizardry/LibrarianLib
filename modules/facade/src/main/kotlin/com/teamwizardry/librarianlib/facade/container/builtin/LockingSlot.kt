package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import java.util.function.Supplier

public class LockingSlot(
    itemHandler: IItemHandler, index: Int,
    public var lock: SlotLock
): FacadeSlot(itemHandler, index) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return !lock.isLocked() && super.isItemValid(stack)
    }

    override fun canTakeStack(playerIn: PlayerEntity): Boolean {
        return !lock.isLocked() && super.canTakeStack(playerIn)
    }

    // ===== slot locks =====

    public fun interface SlotLock {
        public fun isLocked(): Boolean
    }

    public class StaticLock(private val isLocked: Boolean): SlotLock {
        override fun isLocked(): Boolean {
            return isLocked
        }
    }

    /**
     * A slot intended to prevent certain kinds of item duplication glitches. To do this, it records the value returned
     * from the [verificationCallback] and before every operation it verifies that the callback returns the same object.
     *
     * The specific dupe this prevents is (using a backpack as an example):
     *
     * - the player opens a backpack item
     * - you open a container and add the held backpack's inventory to it
     * - the player puts items in the backpack inventory
     * - the player uses a "player interface" of some sort to take the backpack out of their inventory and put it in a
     * chest, which copies the itemstack and discards the existing instance
     * - your screen is now accessing an "orphaned" backpack, so the player can pull items out without affecting the
     * instance in the chest
     * - congratulations, you've just introduced a new dupe!
     *
     * The code is designed to be more general though, so you can use any object as the verification.
     *
     * @param verificationCallback A callback that, if its value changes, will lock the slot.
     * @param isClient Whether this slot is in the client container. ItemStack object identity in particular is not
     * preserved on the client, so the check has to be disabled there.
     */
    public class ConsistencyLock(
        private val verificationCallback: Supplier<Any>,
        private val isClient: Boolean
    ): SlotLock {

        /**
         * Create an instance backed by a particular inventory slot
         *
         * @param sourceInventory The inventory to check
         * @param sourceSlot The slot in [inventory] that, if its item changes, will lock this slot.
         * @param isClient Whether this slot is in the client container. ItemStack object identity is not preserved on the
         * client, so the check has to be disabled there.
         */
        public constructor(
            sourceInventory: IItemHandler,
            sourceSlot: Int,
            isClient: Boolean
        ): this({ sourceInventory.getStackInSlot(sourceSlot) }, isClient)

        /**
         * Create an instance backed by a particular inventory slot
         *
         * @param sourceInventory The inventory to check
         * @param sourceSlot The slot in [inventory] that, if its item changes, will lock this slot.
         * @param isClient Whether this slot is in the client container. ItemStack object identity is not preserved on the
         * client, so the check has to be disabled there.
         */
        public constructor(
            sourceInventory: IInventory,
            sourceSlot: Int,
            isClient: Boolean
        ): this({ sourceInventory.getStackInSlot(sourceSlot) }, isClient)


        private val verificationObject: Any = verificationCallback.get()

        override fun isLocked(): Boolean {
            if(isClient)
                return false
            return verificationCallback.get() !== verificationObject
        }
    }
}