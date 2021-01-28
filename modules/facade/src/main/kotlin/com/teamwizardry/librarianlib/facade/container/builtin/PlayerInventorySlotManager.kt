package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.util.Hand

public class PlayerInventorySlotManager(inventory: PlayerInventory): SlotManager(inventory) {
    public val armor: SlotRegion = all[36..39]
    public val head: SlotRegion = all[39]
    public val chest: SlotRegion = all[38]
    public val legs: SlotRegion = all[37]
    public val feet: SlotRegion = all[36]

    public val hotbar: SlotRegion = all[0..8]
    public val main: SlotRegion = all[9..35]
    public val offhand: SlotRegion = all[40]
    public val mainhand: SlotRegion = if(inventory.currentItem in 0..8) all[inventory.currentItem] else SlotRegion.EMPTY

    init {
        head.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.HEAD) }
        chest.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.CHEST) }
        legs.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.LEGS) }
        feet.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlotType.FEET) }
    }

    public fun getHandSlot(hand: Hand): SlotRegion {
        return when(hand) {
            Hand.MAIN_HAND -> getEquipmentSlot(EquipmentSlotType.MAINHAND)
            Hand.OFF_HAND -> getEquipmentSlot(EquipmentSlotType.OFFHAND)
        }
    }

    private fun getEquipmentSlot(slotType: EquipmentSlotType): SlotRegion {
        return when(slotType) {
            EquipmentSlotType.MAINHAND -> mainhand
            EquipmentSlotType.OFFHAND -> offhand
            EquipmentSlotType.FEET -> feet
            EquipmentSlotType.LEGS -> legs
            EquipmentSlotType.CHEST -> chest
            EquipmentSlotType.HEAD -> head
        }
    }

}