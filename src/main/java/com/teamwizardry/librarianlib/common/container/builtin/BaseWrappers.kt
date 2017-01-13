package com.teamwizardry.librarianlib.common.container.builtin

import com.teamwizardry.librarianlib.common.container.InventoryWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

/**
 * Created by TheCodeWarrior
 */
object BaseWrappers {

    fun player(player: EntityPlayer) = InventoryWrapperPlayer(InvWrapper(player.inventory), player)

    fun inventory(inv: IInventory) = InventoryWrapper(InvWrapper(inv))

    fun stacks(inv: IItemHandler) = InventoryWrapper(inv)

    class InventoryWrapperPlayer(inv: IItemHandler, val player: EntityPlayer) : InventoryWrapper(inv) {

        val armor = slots[36..39]
        val head = slots[39]
        val chest = slots[38]
        val legs = slots[37]
        val feet = slots[36]

        val hotbar = slots[0..8]
        val main = slots[9..35]
        val offhand = slots[40]

        init {
            head.type = SlotTypeEquipment(player, EntityEquipmentSlot.HEAD)
            chest.type = SlotTypeEquipment(player, EntityEquipmentSlot.CHEST)
            legs.type = SlotTypeEquipment(player, EntityEquipmentSlot.LEGS)
            feet.type = SlotTypeEquipment(player, EntityEquipmentSlot.FEET)
        }

    }

}
