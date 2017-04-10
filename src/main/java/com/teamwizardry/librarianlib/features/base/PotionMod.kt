package com.teamwizardry.librarianlib.features.base

import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("LeakingThis")
open class PotionMod(name: String, badEffect: Boolean, color: Int) : Potion(badEffect, color) {

    val iconX: Int
    val iconY: Int

    private val modid: String = currentModId
    private val resource: ResourceLocation = ResourceLocation(modid, "textures/gui/potions.png")

    init {

        val iconIndex = (iconIndexByModId[modid] ?: 0) + 1
        iconIndexByModId[modid] = iconIndex
        iconX = iconIndex % 8
        iconY = iconIndex / 8

        GameRegistry.register(this, ResourceLocation(modid, VariantHelper.toSnakeCase(name)))
        setPotionName("$modid.potion." + VariantHelper.toSnakeCase(name))
        if (!badEffect)
            setBeneficial()
    }

    override fun hasStatusIcon() = false

    @SideOnly(Side.CLIENT)
    override fun renderInventoryEffect(x: Int, y: Int, effect: PotionEffect, mc: Minecraft) {
        mc.renderEngine.bindTexture(resource)
        mc.currentScreen?.drawTexturedModalRect(x + 6, y + 7, 0 + iconX * 18, 198 + iconY * 18, 18, 18)
    }

    @SideOnly(Side.CLIENT)
    override fun renderHUDEffect(x: Int, y: Int, effect: PotionEffect?, mc: Minecraft, alpha: Float) {
        mc.renderEngine.bindTexture(resource)
        mc.ingameGUI.drawTexturedModalRect(x + 3, y + 3, 0 + iconX * 18, 198 + iconY * 18, 18, 18)
    }

    fun hasEffect(entity: EntityLivingBase): Boolean {
        return hasEffect(entity, this)
    }

    fun getEffect(entity: EntityLivingBase): PotionEffect? {
        return getEffect(entity, this)
    }

    companion object {
        private val iconIndexByModId = mutableMapOf<String, Int>()

        fun hasEffect(entity: EntityLivingBase, potion: Potion): Boolean {
            return entity.getActivePotionEffect(potion) != null
        }

        fun getEffect(entity: EntityLivingBase, potion: Potion): PotionEffect? {
            return entity.getActivePotionEffect(potion)
        }
    }
}
