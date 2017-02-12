package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
interface ITransferRule {
    /**
     * Return true if the associated targets should be applied for the passed slot
     */
    fun shouldApply(slot: SlotBase): Boolean

    /**
     * Transfer stack from [from] to this target. Return the remainder or null if the entire stack fit. Do not modify
     * passed stack.
     */
    fun putStack(stack: ItemStack): ItemStack?

    companion object {

        /**
         * Attempt to merge the [stack] into the [region].
         */
        fun mergeIntoRegion(stack: ItemStack, region: List<SlotBase>): AutoTransferResult {
            var runningStack = stack.copy()

            var foundSpot = false
            var shouldContinue = true

            if (stack.isStackable) { // no sense trying to stack similar items if the stack can't stack.
                // try to merge stack into slots that already have items (don't fill up empty slots unless you need to)
                region.forEach { slot ->
                    if (!shouldContinue)
                        return@forEach

                    val result = slot.type.autoTransferInto(slot, runningStack)

                    runningStack = result.remainingStack
                    foundSpot = foundSpot || result.foundSpot
                    shouldContinue = shouldContinue && result.shouldContinue
                }
            }

            if (runningStack != null) {
                region.forEach { slot ->
                    if (!shouldContinue)
                        return@forEach

                    val result = slot.type.autoTransferInto(slot, runningStack)

                    runningStack = result.remainingStack
                    foundSpot = foundSpot || result.foundSpot
                    shouldContinue = shouldContinue && result.shouldContinue
                }
            }

            return AutoTransferResult(runningStack, foundSpot, !foundSpot)
        }


        fun areItemStacksEqual(stackA: ItemStack?, stackB: ItemStack?): Boolean {
            if (stackA == null || stackB == null) {
                return stackA === stackB
            }
            return stackB.item === stackA.item && (!stackA.hasSubtypes || stackA.metadata == stackB.metadata) && ItemStack.areItemStackTagsEqual(stackA, stackB)
        }

    }

    data class AutoTransferResult(
            /**
             * The stack remaining after the transfer
             */
            val remainingStack: ItemStack?,
            /**
             * whether the item was added to the slot, used to know if the next slot region should be attempted
             */
            val foundSpot: Boolean,
            /**
             * whether the rest of the stack should be tried in the other slots
             */
            val shouldContinue: Boolean = remainingStack != null)
}
