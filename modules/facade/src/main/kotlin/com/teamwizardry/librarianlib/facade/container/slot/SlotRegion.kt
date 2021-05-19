package com.teamwizardry.librarianlib.facade.container.slot

import com.teamwizardry.librarianlib.facade.container.DefaultInventoryImpl
import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot
import java.lang.IllegalStateException

public fun interface SlotFactory {
    public fun createSlot(inventory: Inventory, index: Int): Slot
}

/**
 * Provides lazy access to a set of slots
 */
public class SlotRegion private constructor(private val slots: List<LazySlot>) : Iterable<Slot> {

    /**
     * The number of slots in this region
     */
    public val size: Int = slots.size

    /**
     * Returns the list of resolved slots. This forcibly resolves all the slots in this region, so trying to further
     * configure any of them will throw an exception.
     */
    public fun toList(): List<Slot> {
        return slots.map { it.slot }
    }

    /**
     * Returns the an iterator over the resolved slots. This forcibly resolves the slots in this region, so trying to
     * further configure any of them will throw an exception.
     */
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
     * Sets the factories of all the slots in this region. If any of the slots has already been resolved, this will
     * throw an exception.
     */
    public fun setFactory(factory: SlotFactory) {
        for (slot in slots) {
            slot.setFactory(factory)
        }
    }

    /**
     * Sets the factories of the slots in the given range. If any of the slots has already been resolved, this will
     * throw an exception.
     */
    public fun setFactory(fromIndex: Int, toIndex: Int, factory: SlotFactory) {
        if (fromIndex < 0 || fromIndex > slots.size || toIndex < 0 || toIndex > slots.size)
            throw IndexOutOfBoundsException("Range: [$fromIndex, $toIndex), length: ${slots.size}")
        for (i in fromIndex until toIndex) {
            slots[i].setFactory(factory)
        }
    }

    /**
     * Sets the factories of the slots in the given range. If any of the slots has already been resolved, this will
     * throw an exception.
     */
    @JvmSynthetic
    public fun setFactory(range: IntRange, factory: SlotFactory) {
        setFactory(range.first, range.last + 1, factory)
    }

    /**
     * Sets the factory of the slot at the given index. If the slot has already been resolved, this will throw an
     * exception.
     */
    public fun setFactory(index: Int, factory: SlotFactory) {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, length: $size")
        slots[index].setFactory(factory)
    }

    /**
     * Directly sets the slot at the given index. If the slot has already been resolved, this will throw an exception.
     */
    public fun setDirect(index: Int, slot: Slot) {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, length: $size")
        slots[index].setSlot(slot)
    }

    /**
     * Resolves and returns the slot at the given index. Once a slot has been resolved, calling [setFactory] or
     * [setDirect] with that slot will throw an exception.
     */
    public fun getDirect(index: Int): Slot {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, length: $size")
        return slots[index].slot
    }

    /**
     * Returns a region containing all the slots in this region and then all the slots in the given region. Duplicate
     * slots will not be included.
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
        @JvmField
        public val EMPTY: SlotRegion = SlotRegion(emptyList())

        /**
         * Create a region containing specific slot instances. Note: trying to re-configure the slots in this region,
         * either directly or using a factory, will throw an exception.
         */
        @JvmStatic
        public fun create(slots: List<Slot>): SlotRegion {
            return SlotRegion(slots.map { LazySlot(it) })
        }

        /**
         * Create a region containing the slots in the given inventory.
         */
        @JvmStatic
        public fun create(inventory: Inventory): SlotRegion {
            return SlotRegion((0 until inventory.size()).map {
                LazySlot(inventory, it) { inv, i -> FacadeSlot(inv, i) }
            })
        }
    }

    private class LazySlot(val inventory: Inventory, val index: Int, factory: SlotFactory) {
        constructor(slot: Slot) : this(EMPTY_INVENTORY, 0, { _, _ -> slot }) {
            _slot = slot
        }

        private var _factory: SlotFactory = factory
        private var explicit: Boolean = false
        private var _slot: Slot? = null

        val slot: Slot
            get() = _slot ?: _factory.createSlot(inventory, index).also { _slot = it }

        fun setSlot(slot: Slot) {
            throwIfResolved()
            _slot = slot
        }

        public fun setFactory(factory: SlotFactory) {
            throwIfResolved()
            _factory = factory
        }

        private fun throwIfResolved() {
            if (_slot != null)
                throw IllegalStateException(
                    "Slot $index has already been " + (if (explicit) "explicitly set" else "lazily generated")
                )
        }

        companion object {
            private val EMPTY_INVENTORY = DefaultInventoryImpl.ofSize(0)
        }
    }
}
