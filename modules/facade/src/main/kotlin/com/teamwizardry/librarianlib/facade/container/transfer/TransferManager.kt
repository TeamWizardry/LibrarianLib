package com.teamwizardry.librarianlib.facade.container.transfer

import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack

public class TransferManager {
    private val rules = mutableListOf<TransferRule>()

    /**
     * Add a new basic transfer rule
     */
    public fun createBasicRule(): BasicTransferRule {
        val rule = BasicTransferRule()
        rules.add(rule)
        return rule
    }

    /**
     * Add a custom transfer rule
     */
    public fun add(rule: TransferRule) {
        rules.add(rule)
    }

    /**
     * The return value here is just used to cross-check the client and server. If the server's container returns the
     * same stack, it clears the "has changed" status of all the slots. If they differ, it sends the entire inventory
     * to the client.
     *
     * In vanilla, the result is empty unless the item partially fit, in which case it's the contents of the slot before
     * you clicked it. I'm going to emulate this here.
     *
     * ## Upon further consideration...
     *
     * On further inspection (and many, many deadlocks), it's even more irritating. This method will be called
     * repeatedly until either it returns EMPTY or the slot's item (not stack, item) != the returned stack's item.
     *
     * I'm just going to return EMPTY, because fuck all that I'm gonna handle it myself.
     */
    public fun transferStackInSlot(slot: Slot): ItemStack {
        var stack = slot.stack.copy()
        for (rule in rules) {
            if (stack.isEmpty)
                return ItemStack.EMPTY
            if (rule.appliesToSlot(slot)) {
                val result = rule.transferStack(stack)

                // We will carry over the resulting stack regardless of whether it *says* it was successful, otherwise
                // it would be much easier for poorly implemented rules to create item duplication glitches.
                stack = result.remaining
                if (result.successful) {
                    slot.putStack(stack)
                    break
                }
            }
        }
        return ItemStack.EMPTY
    }
}