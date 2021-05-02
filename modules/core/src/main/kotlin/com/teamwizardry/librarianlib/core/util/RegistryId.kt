@file:JvmName("RegistryId")

package com.teamwizardry.librarianlib.core.util

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.loot.condition.LootConditionType
import net.minecraft.loot.entry.LootPoolEntryType
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.potion.Potion
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.StatType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

public val SoundEvent.registryId: Identifier?
    @JvmName("of") get() = Registry.SOUND_EVENT.getId(this)
public val Fluid.registryId: Identifier
    @JvmName("of") get() = Registry.FLUID.getId(this)
public val StatusEffect.registryId: Identifier?
    @JvmName("of") get() = Registry.STATUS_EFFECT.getId(this)
public val Block.registryId: Identifier
    @JvmName("of") get() = Registry.BLOCK.getId(this)
public val Enchantment.registryId: Identifier?
    @JvmName("of") get() = Registry.ENCHANTMENT.getId(this)
public val EntityType<*>.registryId: Identifier
    @JvmName("of") get() = Registry.ENTITY_TYPE.getId(this)
public val Item.registryId: Identifier
    @JvmName("of") get() = Registry.ITEM.getId(this)
public val Potion.registryId: Identifier
    @JvmName("of") get() = Registry.POTION.getId(this)

public val RecipeType<*>.registryId: Identifier?
    @JvmName("of") get() = Registry.RECIPE_TYPE.getId(this)
public val RecipeSerializer<*>.registryId: Identifier?
    @JvmName("of") get() = Registry.RECIPE_SERIALIZER.getId(this)
public val EntityAttribute.registryId: Identifier?
    @JvmName("of") get() = Registry.ATTRIBUTE.getId(this)
public val StatType<*>.registryId: Identifier?
    @JvmName("of") get() = Registry.STAT_TYPE.getId(this)

public val LootPoolEntryType.registryId: Identifier?
    @JvmName("of") get() = Registry.LOOT_POOL_ENTRY_TYPE.getId(this)
public val LootFunctionType.registryId: Identifier?
    @JvmName("of") get() = Registry.LOOT_FUNCTION_TYPE.getId(this)
public val LootConditionType.registryId: Identifier?
    @JvmName("of") get() = Registry.LOOT_CONDITION_TYPE.getId(this)

