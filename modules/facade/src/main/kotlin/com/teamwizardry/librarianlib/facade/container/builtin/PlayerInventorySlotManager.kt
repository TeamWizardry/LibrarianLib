package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.EquipmentSlotType

public class PlayerInventorySlotManager(inventory: PlayerInventory): SlotManager(inventory) {
    public val armor: SlotRegion = all[36..39]
    public val head: SlotRegion = all[39]
    public val chest: SlotRegion = all[38]
    public val legs: SlotRegion = all[37]
    public val feet: SlotRegion = all[36]

    public val hotbar: SlotRegion = all[0..8]
    public val main: SlotRegion = all[9..35]
    public val offhand: SlotRegion = all[40]

    init {
        head.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.HEAD) }
        chest.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.CHEST) }
        legs.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.LEGS) }
        feet.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.FEET) }
    }
}