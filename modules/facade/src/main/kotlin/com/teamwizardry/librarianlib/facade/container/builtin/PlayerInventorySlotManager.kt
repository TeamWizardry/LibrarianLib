package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerInventory
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
    public val mainhand: SlotRegion = if(inventory.selectedSlot in 0..8) all[inventory.selectedSlot] else SlotRegion.EMPTY

    init {
        head.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlot.HEAD) }
        chest.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlot.CHEST) }
        legs.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlot.LEGS) }
        feet.setFactory { inv, i -> PlayerEquipmentSlot(inv, i, inventory.player, EquipmentSlot.FEET) }
    }

    public fun getHandSlot(hand: Hand): SlotRegion {
        return when(hand) {
            Hand.MAIN_HAND -> getEquipmentSlot(EquipmentSlot.MAINHAND)
            Hand.OFF_HAND -> getEquipmentSlot(EquipmentSlot.OFFHAND)
        }
    }

    private fun getEquipmentSlot(slotType: EquipmentSlot): SlotRegion {
        return when(slotType) {
            EquipmentSlot.MAINHAND -> mainhand
            EquipmentSlot.OFFHAND -> offhand
            EquipmentSlot.FEET -> feet
            EquipmentSlot.LEGS -> legs
            EquipmentSlot.CHEST -> chest
            EquipmentSlot.HEAD -> head
        }
    }

}