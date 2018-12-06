package com.teamwizardry.librarianlib.core.common

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraft.world.biome.Biome
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
@Mod.EventBusSubscriber(modid = LibrarianLib.MODID)
object RegistrationHandler {

    @JvmStatic
    @SubscribeEvent
    fun block(registryEvent: Register<Block>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun item(registryEvent: Register<Item>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun potion(registryEvent: Register<Potion>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun biome(registryEvent: Register<Biome>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun soundEvent(registryEvent: Register<SoundEvent>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun potionType(registryEvent: Register<PotionType>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun enchantment(registryEvent: Register<Enchantment>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun profession(registryEvent: Register<VillagerProfession>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun entity(registryEvent: Register<EntityEntry>) = doRegistration(registryEvent.registry)

    @JvmStatic
    @SubscribeEvent
    fun recipe(registryEvent: Register<IRecipe>) = doRegistration(registryEvent.registry)

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

    private inline fun <reified T : IForgeRegistryEntry<T>> doRegistration(registry: IForgeRegistry<T>) {
        getRegistrar(registry.registrySuperType)
                .forEach { registry.register(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : IForgeRegistryEntry<T>> getRegistrar(type: Class<T>) = when (type) {
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

    @JvmStatic
    fun <T : IForgeRegistryEntry<T>> register(entry: T, rl: ResourceLocation) = entry.apply { getRegistrar(registryType).add(this.setRegistryName(rl)) }

    @JvmStatic
    fun <T : IForgeRegistryEntry<T>> register(entry: T) = entry.apply { getRegistrar(registryType).add(this) }

}
