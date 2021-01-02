package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraft.inventory.container.Slot
import net.minecraftforge.items.IItemHandler

public fun interface SlotFactory {
    public fun createSlot(inventory: IItemHandler, index: Int): Slot
}

/**
 * Provides access to a set of slots
 */
public class SlotRegion(private val slots: List<SlotReference>): Iterable<Slot> {
    /**
     * The number of slots in this region
     */
    public val size: Int = slots.size

    /**
     * Returns the current list of slots. The returned list is *not* live, so changing the slots after this list is
     * generated will lead to unexpected behavior.
     */
    public fun toList(): List<Slot> {
        return slots.map { it.slot }
    }

    override fun iterator(): Iterator<Slot> {
        return slots.asSequence().map { it.slot }.iterator()
    }

    /**
     * Gets a live view of the slots from [fromIndex] (inclusive) to [toIndex] (exclusive)
     */
    public fun getRange(fromIndex: Int, toIndex: Int): SlotRegion {
        if (fromIndex < 0 || fromIndex > slots.size || toIndex < 0 || toIndex > slots.size)
            throw IndexOutOfBoundsException("Range: [$fromIndex, $toIndex), length: ${slots.size}")
        return SlotRegion(slots.subList(fromIndex, toIndex))
    }

    /**
     * Gets a live view of the slots in the specified range
     */
    @JvmSynthetic
    public operator fun get(range: IntRange): SlotRegion {
        return this.getRange(range.first, range.last + 1)
    }

    /**
     * Gets a live view of the slot at the specified index
     */
    public operator fun get(index: Int): SlotRegion {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, length: $size")
        return this[index..index]
    }

    /**
     * Replaces the slots in the specified range with slots generated using [factory].
     *
     * Take care that the given slots haven't already been accessed.
     */
    public fun setRange(fromIndex: Int, toIndex: Int, factory: SlotFactory) {
        if (fromIndex < 0 || fromIndex > slots.size || toIndex < 0 || toIndex > slots.size)
            throw IndexOutOfBoundsException("Range: [$fromIndex, $toIndex), length: ${slots.size}")
        for (i in fromIndex until toIndex) {
            slots[i].set(factory)
        }
    }

    /**
     * Replaces the slots in the specified [range] with slots generated using [factory].
     *
     * Take care that the given slots haven't already been accessed.
     */
    @JvmSynthetic
    public fun setRange(range: IntRange, factory: SlotFactory) {
        setRange(range.first, range.last + 1, factory)
    }

    /**
     * Replaces the slot at [index] with a slot generated using [factory]. A factory is used so you don't have to
     * keep track of inventories or slot indices.
     *
     * Take care that the given slot hasn't already been accessed.
     */
    public fun set(index: Int, factory: SlotFactory) {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, length: $size")
        setRange(index, index + 1, factory)
    }

    /**
     * Returns a region containing all the slots in this region and then all the slots in the given region. Any slots
     * present in both regions will be excluded from the given region before merging.
     */
    @JvmName("union")
    public operator fun plus(other: SlotRegion): SlotRegion {
        return SlotRegion(slots + (other.slots - slots))
    }

    /**
     * Returns a region containing all the slots in this region except the slots in the given region.
     */
    @JvmName("excluding")
    public operator fun minus(other: SlotRegion): SlotRegion {
        return SlotRegion(slots - other.slots)
    }

    public companion object {
        /**
         * Create a region containing specific slot instances. Note: trying to call [set] or [setRange] on any of these
         * slots will throw an [UnsupportedOperationException].
         */
        @JvmStatic
        public fun createDirect(slots: List<Slot>): SlotRegion {
            return SlotRegion(slots.map { DirectSlotReference(it) })
        }
    }
}
