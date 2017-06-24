package com.teamwizardry.librarianlib.core.common

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import java.lang.reflect.Type

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
object RegistrationHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent fun block(registryEvent: Register<Block>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun item(registryEvent: Register<Item>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun potion(registryEvent: Register<Potion>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun biome(registryEvent: Register<Biome>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun soundEvent(registryEvent: Register<SoundEvent>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun potionType(registryEvent: Register<PotionType>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun enchantment(registryEvent: Register<Enchantment>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun profession(registryEvent: Register<VillagerProfession>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun entity(registryEvent: Register<EntityEntry>) = doRegistration(registryEvent.registry)
    @SubscribeEvent fun recipe(registryEvent: Register<IRecipe>) = doRegistration(registryEvent.registry)

    private val registrarBlock = mutableSetOf<Block>()
    private val registrarItem = mutableSetOf<Item>()
    private val registrarPotion = mutableSetOf<Potion>()
    private val registrarBiome = mutableSetOf<Biome>()
    private val registrarSoundEvent = mutableSetOf<SoundEvent>()
    private val registrarPotionType = mutableSetOf<PotionType>()
    private val registrarEnchantment = mutableSetOf<Enchantment>()
    private val registrarProfession = mutableSetOf<VillagerProfession>()
    private val registrarEntity = mutableSetOf<EntityEntry>()
    private val registrarRecipe = mutableSetOf<IRecipe>()

    private val registrarBlockRL = mutableSetOf<Pair<Block, ResourceLocation>>()
    private val registrarItemRL = mutableSetOf<Pair<Item, ResourceLocation>>()
    private val registrarPotionRL = mutableSetOf<Pair<Potion, ResourceLocation>>()
    private val registrarBiomeRL = mutableSetOf<Pair<Biome, ResourceLocation>>()
    private val registrarSoundEventRL = mutableSetOf<Pair<SoundEvent, ResourceLocation>>()
    private val registrarPotionTypeRL = mutableSetOf<Pair<PotionType, ResourceLocation>>()
    private val registrarEnchantmentRL = mutableSetOf<Pair<Enchantment, ResourceLocation>>()
    private val registrarProfessionRL = mutableSetOf<Pair<VillagerProfession, ResourceLocation>>()
    private val registrarEntityRL = mutableSetOf<Pair<EntityEntry, ResourceLocation>>()
    private val registrarRecipeRL = mutableSetOf<Pair<IRecipe, ResourceLocation>>()

    private inline fun <reified T: IForgeRegistryEntry<T>> doRegistration(registry: IForgeRegistry<T>) {
        getRegistrar(registry.registrySuperType)
                .forEach { registry.register(it) }
        getRLRegistrar(registry.registrySuperType)
                .map { it.first.setRegistryName(it.second) }
                .forEach { registry.register(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : IForgeRegistryEntry<T>> getRegistrar(type: Class<T>) = when(type) {
        Block::class.java -> registrarBlock
        Item::class.java -> registrarItem
        Potion::class.java -> registrarPotion
        Biome::class.java -> registrarBiome
        SoundEvent::class.java -> registrarSoundEvent
        PotionType::class.java -> registrarPotionType
        Enchantment::class.java -> registrarEnchantment
        VillagerProfession::class.java -> registrarProfession
        EntityEntry::class.java -> registrarEntity
        IRecipe::class.java -> registrarRecipe
        else -> mutableSetOf()
    } as MutableSet<T>

    @Suppress("UNCHECKED_CAST")
    private fun <T : IForgeRegistryEntry<T>> getRLRegistrar(type: Class<T>) = when(type) {
        Block::class.java -> registrarBlockRL
        Item::class.java -> registrarItemRL
        Potion::class.java -> registrarPotionRL
        Biome::class.java -> registrarBiomeRL
        SoundEvent::class.java -> registrarSoundEventRL
        PotionType::class.java -> registrarPotionTypeRL
        Enchantment::class.java -> registrarEnchantmentRL
        VillagerProfession::class.java -> registrarProfessionRL
        EntityEntry::class.java -> registrarEntityRL
        IRecipe::class.java -> registrarRecipeRL
        else -> mutableSetOf()
    } as MutableSet<Pair<T, ResourceLocation>>

    @JvmStatic fun <T : IForgeRegistryEntry<T>> register(entry: T, rl: ResourceLocation) = entry.apply { getRLRegistrar(registryType).add(this to rl) }
    @JvmStatic fun <T : IForgeRegistryEntry<T>> register(entry: T) = entry.apply { getRegistrar(registryType).add(this) }

}
