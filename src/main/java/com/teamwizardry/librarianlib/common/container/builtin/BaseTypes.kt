package com.teamwizardry.librarianlib.common.container.builtin

import com.teamwizardry.librarianlib.common.container.SlotType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
object BaseTypes {
    val ARMOR = SlotEquipment(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET)
    val ARMOR_HEAD = SlotEquipment(EntityEquipmentSlot.HEAD)
    val ARMOR_CHEST = SlotEquipment(EntityEquipmentSlot.CHEST)
    val ARMOR_LEGS = SlotEquipment(EntityEquipmentSlot.LEGS)
    val ARMOR_FEET = SlotEquipment(EntityEquipmentSlot.FEET)

}

class SlotEquipment(vararg val types: EntityEquipmentSlot) : SlotType() {
    override fun isValid(player: EntityPlayer?, stack: ItemStack?): Boolean {
        return types.any { stack?.item?.isValidArmor(stack, it, player) ?: false }
    }

    override fun stackLimit(player: EntityPlayer?, stack: ItemStack): Int {
        return 1
    }
}
