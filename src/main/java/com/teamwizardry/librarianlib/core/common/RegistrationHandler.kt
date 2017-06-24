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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession
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

    @SubscribeEvent fun block(registryEvent: RegistryEvent.Register<Block>) = registrarBlock.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun item(registryEvent: RegistryEvent.Register<Item>) = registrarItem.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun potion(registryEvent: RegistryEvent.Register<Potion>) = registrarPotion.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun biome(registryEvent: RegistryEvent.Register<Biome>) = registrarBiome.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun soundEvent(registryEvent: RegistryEvent.Register<SoundEvent>) = registrarSoundEvent.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun potionType(registryEvent: RegistryEvent.Register<PotionType>) = registrarPotionType.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun enchantment(registryEvent: RegistryEvent.Register<Enchantment>) = registrarEnchantment.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun profession(registryEvent: RegistryEvent.Register<VillagerProfession>) = registrarProfession.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun entity(registryEvent: RegistryEvent.Register<EntityEntry>) = registrarEntity.forEach { registryEvent.registry.register(it) }
    @SubscribeEvent fun recipe(registryEvent: RegistryEvent.Register<IRecipe>) = registrarRecipe.forEach { registryEvent.registry.register(it) }

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

    private val registrars = mutableMapOf<Type, MutableSet<out IForgeRegistryEntry<*>>>(
            Block::class.java to registrarBlock,
            Item::class.java to registrarItem,
            Potion::class.java to registrarPotion,
            Biome::class.java to registrarBiome,
            SoundEvent::class.java to registrarSoundEvent,
            PotionType::class.java to registrarPotionType,
            Enchantment::class.java to registrarEnchantment,
            VillagerProfession::class.java to registrarProfession,
            EntityEntry::class.java to registrarEntity,
            IRecipe::class.java to registrarRecipe
    )

    @Suppress("UNCHECKED_CAST")
    fun <T : IForgeRegistryEntry<T>> getRegistrar(entry: T) = registrars[entry.registryType] as MutableSet<T>

    @JvmStatic fun <T : IForgeRegistryEntry<T>> register(entry: T, rl: ResourceLocation) = register(entry.setRegistryName(rl))
    @JvmStatic fun <T : IForgeRegistryEntry<T>> register(entry: T) = entry.apply { getRegistrar(this).add(this) }

}
