package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.EquipmentSlotType

public class PlayerInventorySlotManager(inventory: PlayerInventory): SlotManager(inventory) {
    public val armor: SlotRegion = slots[36..39]
    public val head: SlotRegion = slots[39]
    public val chest: SlotRegion = slots[38]
    public val legs: SlotRegion = slots[37]
    public val feet: SlotRegion = slots[36]

    public val hotbar: SlotRegion = slots[0..8]
    public val main: SlotRegion = slots[9..35]
    public val offhand: SlotRegion = slots[40]

    init {
        head.set(0) { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.HEAD) }
        chest.set(0) { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.CHEST) }
        legs.set(0) { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.LEGS) }
        feet.set(0) { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.FEET) }
    }
}