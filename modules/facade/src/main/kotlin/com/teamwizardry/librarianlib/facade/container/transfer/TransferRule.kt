package com.teamwizardry.librarianlib.facade.container.transfer

import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import kotlin.math.max
import kotlin.math.min

/**
 * Created by TheCodeWarrior
 */
public interface TransferRule {
    /**
     * Return true if this rule should be used to determine transfers for the passed slot
     */
    public fun appliesToSlot(slot: Slot): Boolean

    /**
     * Transfer [stack] to this target. Return the remainder.
     */
    public fun transferStack(stack: ItemStack): Result

    public data class Result(
        /**
         * whether *any of* the stack fit in this rule. Even if the stack wasn't completely consumed, it should only be
         * successfully processed by a single rule at a time.
         */
        val successful: Boolean,
        /**
         * The remaining stack after the rule has transferred as much as it can.
         */
        val remaining: ItemStack
    )

    public companion object {
        /**
         * Attempt to merge the transfer into the specified slots
         */
        public fun mergeIntoRegion(transfer: TransferState, region: SlotRegion) {
            val similarSlots = region.filter { slot ->
                (slot as? TransferSlot)?.isStackSimilar(transfer.stack) ?: defaultIsStackSimilar(slot, transfer.stack)
            }

            for (slot in similarSlots + (region - similarSlots)) {
                (slot as? TransferSlot)?.transferIntoSlot(transfer) ?: defaultTransferIntoSlot(slot, transfer)
                if (transfer.halt)
                    break
            }
        }

        /**
         * @see TransferSlot.isStackSimilar
         */
        private fun defaultIsStackSimilar(slot: Slot, stack: ItemStack): Boolean {
            return Container.areItemsAndTagsEqual(stack, slot.stack)
        }

        /**
         * @see TransferSlot.transferIntoSlot
         */
        private fun defaultTransferIntoSlot(slot: Slot, transfer: TransferState) {
            if (!slot.isItemValid(transfer.stack))
                return
            val slotStack = slot.stack

            // if it's the same item, we should try stacking. if it's empty, we'll just stack onto that zero.
            if (Container.areItemsAndTagsEqual(transfer.stack, slotStack) || slotStack.isEmpty) {
                // compute how much to actually transfer
                val maxStackSize = min(slot.getItemStackLimit(transfer.stack), transfer.stack.maxStackSize)
                val transferLimit = max(0, maxStackSize - slotStack.count)
                val transferAmount = min(transfer.stack.count, transferLimit)

                // true, the item matched, but if none of it actually *fit*, we didn't find a spot and shouldn't do
                // anything
                if(transferAmount == 0)
                    return

                // split off a stack and insert it
                // we copy the transferring stack instead of the current stack so we can seamlessly handle empty slots
                val insert = transfer.stack.copy()
                insert.count = slotStack.count + transferAmount
                slot.putStack(insert)

                // decrement the remaining stack appropriately. `isEmpty` returns true when the count is zero
                transfer.stack.count -= transferAmount
                transfer.foundSpot = true
            }
        }
    }
}

public class TransferState(
    /**
     * The stack remaining after the transfer
     */
    public var stack: ItemStack,
    /**
     * whether the item was added to the slot, used to prevent a partial stack rolling over from one rule to another
     */
    public var foundSpot: Boolean,
    /**
     * Whether transfer processing should
     */
    public var halt: Boolean)

/**
 * Slots can implement this interface to change their behavior when the player shift-clicks an item into them.
 */
public interface TransferSlot {
    /**
     * Does this slot contain the same item as [stack]? This is used to prioritize transferring into slots that already
     * contain the same stack.
     *
     * Here's a reference implementation:
     * ```kotlin
     * Container.areItemsAndTagsEqual(stack, this.stack)
     * ```
     */
    public fun isStackSimilar(stack: ItemStack): Boolean

    /**
     * Try to transfer an itemstack into this slot.
     *
     * Here's a pseudocode reference implementation based on the default behavior. The full default implementation can
     * be found in [TransferRule.defaultTransferIntoSlot].
     *
     * ```kotlin
     * // check if the stack is valid here
     * if(Container.areItemsAndTagsEqual(transfer.stack, this.stack) || this.stack.isEmpty) {
     *     val transferAmount = // compute how much can fit
     *
     *     // split off a stack and insert it
     *     val insert = transfer.stack.copy()
     *     insert.count = this.stack.count + transferAmount
     *     this.putStack(insert)
     *
     *     // decrement the transferred stack
     *     transfer.stack.count -= transferAmount
     *
     *     // make sure that the search stops with this region
     *     transfer.foundSpot = true
     *
     *     // if you wanted the transfer to stop immediately
     *     // instead of continuing until the stack is empty:
     *     // transfer.halt = true
     * }
     * ```
     */
    public fun transferIntoSlot(transfer: TransferState)
}
