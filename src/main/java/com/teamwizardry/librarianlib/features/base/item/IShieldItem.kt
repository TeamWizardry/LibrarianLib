package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.kotlin.toolClasses
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author WireSegal
 * Created at 5:02 PM on 4/9/17.
 */
interface IShieldItem {
    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        val canBlockDamageSource = MethodHandleHelper.wrapperForMethod(EntityLivingBase::class.java, arrayOf("e", "func_184583_d", "canBlockDamageSource"), DamageSource::class.java)

        @SubscribeEvent
        fun onLivingAttack(e: LivingAttackEvent) {
            val attacked = e.entityLiving
            val source = e.source
            val indirectSource = source.entity
            val directSource = source.sourceOfDamage

            if (attacked is EntityPlayer && !source.isUnblockable) {
                val activeItem = attacked.activeItemStack
                if (canBlockDamageSource(attacked, arrayOf(source)) as Boolean) {
                    if (activeItem.item is IShieldItem) {
                        val shield = activeItem.item as IShieldItem
                        if (e.amount > 3f) damageShield(activeItem, attacked, indirectSource, directSource, e.amount, source)
                        shield.onDamageBlocked(activeItem, attacked, indirectSource, directSource, e.amount, source)
                        if (indirectSource == directSource && directSource is EntityLivingBase && "axe" in directSource.heldItemMainhand.toolClasses) {
                            if (!shield.onAxeBlocked(activeItem, attacked, directSource, e.amount, e.source))
                                disableShield(activeItem, attacked, directSource)
                        }
                    } else if (activeItem.item == Items.SHIELD &&
                            indirectSource == directSource &&
                            directSource is EntityLivingBase &&
                            "axe" in directSource.heldItemMainhand.toolClasses &&
                            directSource.heldItemMainhand.item !is ItemAxe)
                        disableShield(activeItem, attacked, directSource)
                }
            }
        }

        fun damageShield(stack: ItemStack, player: EntityPlayer, indirectSource: Entity?, directSource: Entity?, amount: Float, source: DamageSource) {
            damageItem(stack, player, indirectSource, directSource, amount, source, 1 + MathHelper.floor(amount))

            if (stack.isEmpty) {
                val hand = player.activeHand
                ForgeEventFactory.onPlayerDestroyItem(player, stack, hand)

                player.setHeldItem(hand, ItemStack.EMPTY)

                player.resetActiveHand()
                player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + player.world.rand.nextFloat() * 0.4f)
            }
        }

        fun disableShield(stack: ItemStack, player: EntityPlayer, attacker: EntityLivingBase) {
            val f1 = 0.25f + EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05f

            if (player.world.rand.nextFloat() < f1) {
                player.cooldownTracker.setCooldown(stack.item, 100)
                player.resetActiveHand()
            }
        }

        fun damageItem(stack: ItemStack, player: EntityPlayer, indirectSource: Entity?, directSource: Entity?, amount: Float, source: DamageSource, damageAmount: Int) {
            if (stack.item is IShieldItem || !(stack.item as IShieldItem).damageItem(stack, player, indirectSource, directSource, amount, source, damageAmount))
                stack.damageItem(damageAmount, player)
        }
    }


    /**
     * Offers shields an opportunity to have an effect when they block damage.
     */
    fun onDamageBlocked(stack: ItemStack, player: EntityPlayer, indirectSource: Entity?, directSource: Entity?, amount: Float, source: DamageSource)

    /**
     * Offers shields an opportunity to have custom item damage implementations,
     * such as repairing from a stored energy source.
     *
     * Return true to cancel default damage processing.
     */
    fun damageItem(stack: ItemStack, player: EntityPlayer, indirectSource: Entity?, directSource: Entity?, amount: Float, source: DamageSource, damageAmount: Int): Boolean

    /**
     * Offers shields an opportunity to have custom axe-blocking implementations.
     *
     * Return true to cancel default processing.
     */
    fun onAxeBlocked(stack: ItemStack, player: EntityPlayer, attacker: EntityLivingBase, amount: Float, source: DamageSource): Boolean
}
