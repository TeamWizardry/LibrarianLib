package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraft.inventory.container.Slot
import net.minecraftforge.items.IItemHandler

public interface SlotReference {
    /**
     * The slot currently in this reference. This is subject to change if [set] is called.
     */
    public val slot: Slot

    /**
     * Replaces this slot with a slot generated using [factory].
     */
    public fun set(factory: SlotFactory)
}

public class FactorySlotReference(public val inventory: IItemHandler, public val index: Int, factory: SlotFactory): SlotReference {
    override var slot: Slot = factory.createSlot(inventory, index)
        private set

    override fun set(factory: SlotFactory) {
        slot = factory.createSlot(inventory, index)
    }
}

public class DirectSlotReference(override val slot: Slot): SlotReference {
    override fun set(factory: SlotFactory) {
        throw UnsupportedOperationException("Can't set the value of a direct slot reference")
    }
}
