package com.teamwizardry.librarianlib.facade.container.slot

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.facade.container.builtin.PlayerInventorySlotManager
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Slot
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException

/**
 * A collection of slots, along with methods for easily manipulating them. Subclasses should pre-configure slot types as
 * appropriate and should include fields that provide semantic access to slots and slot ranges.
 *
 * For example, [PlayerInventorySlotManager] pre-configures the armor slots to the appropriate [EquipmentSlotType] and
 * provides fields for armor slots, the hotbar, the main inventory, etc.
 */
public open class SlotManager(private val inventory: IItemHandler) {
    public constructor(inventory: IInventory): this(InvWrapper(inventory))

    public val slots: SlotRegion = SlotRegion((0 until inventory.slots).map {
        FactorySlotReference(inventory, it) { inv, i -> SlotItemHandler(inv, i, 0, 0) }
    })
}
