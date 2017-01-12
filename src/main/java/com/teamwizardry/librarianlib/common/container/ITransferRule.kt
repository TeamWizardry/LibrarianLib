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
        fun mergeIntoRegion(stack: ItemStack, region: List<SlotBase>): ItemStack? {
            var runningStack = stack.copy()
            var insertedAny = false

            if (stack.isStackable) { // no sense trying to stack similar items if the stack can't stack.
                // try to merge stack into slots that already have items (don't fill up empty slots unless you need to)
                region.forEach { slot ->
                    if (runningStack == null)
                        return@forEach
                    val slotStack = slot.stack ?: return@forEach

                    if(areItemStacksEqual(runningStack, slotStack)) {
                        val combinedSize = runningStack.stackSize + slotStack.stackSize
                        val maxStackSize = Math.min(slot.getItemStackLimit(runningStack), runningStack.maxStackSize)

                        if(combinedSize <= maxStackSize) {
                            runningStack = null
                            val newStack = slotStack.copy()
                            newStack.stackSize = combinedSize
                            slot.putStack(newStack)
                            insertedAny = true
                        } else {
                            runningStack.stackSize -= maxStackSize - slotStack.stackSize
                            val newStack = slotStack.copy()
                            newStack.stackSize = maxStackSize
                            slot.putStack(newStack)

                            insertedAny = true
                        }
                    }
                }
            }

            if(runningStack != null) {
                region.forEach { slot ->
                    if(runningStack == null)
                        return@forEach
                    val slotStack = slot.stack
                    if(slotStack != null)
                        return@forEach

                    val maxStackSize = Math.min(slot.getItemStackLimit(runningStack), runningStack.maxStackSize)

                    if(runningStack.stackSize <= maxStackSize) {
                        slot.putStack(runningStack.copy())
                        runningStack = null
                        insertedAny = true
                    } else {
                        val newStack = runningStack.copy()
                        newStack.stackSize = maxStackSize
                        slot.putStack(newStack)

                        runningStack.stackSize -= maxStackSize
                        insertedAny = true
                    }
                }
            }

            if(insertedAny)
                return runningStack
            else
                return stack
        }


        private fun areItemStacksEqual(stackA: ItemStack, stackB: ItemStack): Boolean {
            return stackB.item === stackA.item && (!stackA.hasSubtypes || stackA.metadata == stackB.metadata) && ItemStack.areItemStackTagsEqual(stackA, stackB)
        }
    }
}
