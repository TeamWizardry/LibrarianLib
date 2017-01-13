package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
interface ITransferRule {
    /**
     * Return true if the associated target should be applied for the passed slot
     */
    fun shouldApply(slot: SlotBase): Boolean

    /**
     * Transfer stack from [from] to this target. Return the remainder or null if the entire stack fit. Do not modify
     * passed stack.
     *
     * If passed and returned stacks are equal by identity the stack couldn't fit in the region.
     */
    fun putStack(stack: ItemStack): ItemStack?

    companion object {

        /**
         * Attempt to merge the [stack] into the [region]. Returns the remainder or null if the entire stack fit.
         *
         * If the input and output itemstacks succeed on an identity equality then none of the stack fit.
         */
        fun mergeIntoRegion(stack: ItemStack, region: List<SlotBase>): AutoTransferResult {
            var runningStack = stack.copy()

            var foundSpot = false
            var shouldContinue = true

            if (stack.isStackable) { // no sense trying to stack similar items if the stack can't stack.
                // try to merge stack into slots that already have items (don't fill up empty slots unless you need to)
                region.forEach { slot ->
                    if(!shouldContinue)
                        return@forEach

                    val result = slot.type.autoTransferInto(slot, runningStack)

                    runningStack = result.remainingStack
                    foundSpot = foundSpot || result.foundSpot
                    shouldContinue = shouldContinue && result.shouldContinue
                }
            }

            if(runningStack != null) {
                region.forEach { slot ->
                    if(!shouldContinue)
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
            if(stackA == null || stackB == null) {
                return stackA === stackB
            }
            return stackB.item === stackA.item && (!stackA.hasSubtypes || stackA.metadata == stackB.metadata) && ItemStack.areItemStackTagsEqual(stackA, stackB)
        }

    }

    data class AutoTransferResult(val remainingStack: ItemStack?, val foundSpot: Boolean, val shouldContinue: Boolean = remainingStack != null)
}
