package com.teamwizardry.librarianlib.facade.container.slot

import com.teamwizardry.librarianlib.facade.container.builtin.PlayerInventorySlotManager
import net.minecraft.inventory.Inventory

/**
 * A collection of slots, along with methods for easily manipulating them. Subclasses should pre-configure slot types as
 * appropriate and should include fields that provide semantic access to slots and slot ranges.
 *
 * For example, [PlayerInventorySlotManager] pre-configures the armor slots to the appropriate [EquipmentSlotType] and
 * provides fields for armor slots, the hotbar, the main inventory, etc.
 */
public open class SlotManager(private val inventory: Inventory) {
    public val all: SlotRegion = SlotRegion.create(inventory)
}
