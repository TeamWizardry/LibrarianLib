package com.teamwizardry.librarianlib.facade.container.transfer

import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import java.util.function.Predicate

public class BasicTransferRule: TransferRule {
    private val fromSet = mutableSetOf<Slot>()
    private var filter: Predicate<Slot> = Predicate { true }
    private val targets = mutableListOf<SlotRegion>()

    public fun from(slots: SlotRegion): BasicTransferRule {
        fromSet.addAll(slots)
        return this
    }

    public fun from(slots: List<Slot>): BasicTransferRule {
        fromSet.addAll(slots)
        return this
    }

    public fun from(vararg slots: Slot): BasicTransferRule {
        fromSet.addAll(slots)
        return this
    }

    public fun filter(filter: Predicate<Slot>): BasicTransferRule {
        this.filter = filter
        return this
    }

    /**
     * Add a destination region. If multiple regions are added only the first one that can fit any of the item will be
     * used. i.e. if any items were successfully deposited in the first region the remainder of the stack won't roll
     * over to the next region.
     */
    public fun into(region: SlotRegion): BasicTransferRule {
        targets.add(region)
        return this
    }

    override fun appliesToSlot(slot: Slot): Boolean {
        return slot in fromSet && filter.test(slot)
    }

    override fun transferStack(stack: ItemStack): TransferRule.Result {
        val transfer = TransferState(stack, false, false)
        for (target in targets) {
            if (stack.isEmpty)
                break
            TransferRule.mergeIntoRegion(transfer, target) // todo: target filter by visibility
            if (transfer.halt || transfer.foundSpot)
                break
        }
        return TransferRule.Result(transfer.foundSpot || transfer.halt, transfer.stack)
    }
}
