package com.teamwizardry.librarianlib.common.base

import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnumEnchantmentType
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * @author WireSegal
 * Created at 7:41 PM on 1/24/17.
 */
@Suppress("LeakingThis")
open class EnchantmentMod(name: String, rarity: Rarity, type: EnumEnchantmentType, vararg applicableSlots: EntityEquipmentSlot) : Enchantment(rarity, type, applicableSlots) {

    val modId = currentModId

    init {
        setName("$modId.$name")
        GameRegistry.register(this, ResourceLocation(modId, name))
    }

    open val applicableSlots: Array<EntityEquipmentSlot>
        get() = arrayOf()

    private fun getEntityEquipmentForLevel(entityIn: EntityLivingBase)
            = applicableSlots.mapNotNull { entityIn.getItemStackFromSlot(it) }

    fun getMaxLevel(entity: EntityLivingBase)
            = getEntityEquipmentForLevel(entity)
            .map { EnchantmentHelper.getEnchantmentLevel(this, it) }
            .max() ?: 0

    fun getTotalLevel(entity: EntityLivingBase)
            = getEntityEquipmentForLevel(entity)
            .map { EnchantmentHelper.getEnchantmentLevel(this, it) }
            .sum()
}
