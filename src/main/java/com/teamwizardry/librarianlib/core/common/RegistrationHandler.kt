package com.teamwizardry.librarianlib.core.common

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistryEntry

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
object RegistrationHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @JvmStatic fun <T : IForgeRegistryEntry<T>> register(entry: T, rl: ResourceLocation) = register(entry.setRegistryName(rl))
    @JvmStatic fun <T : IForgeRegistryEntry<T>> register(entry: T) = entry.apply { registrar.add(this) }

    private val registrar = mutableListOf<IForgeRegistryEntry<*>>()

    @SubscribeEvent
    fun registerAll(e: RegistryEvent.Register<*>) {
        registrar
                .filter { e.getGenericType() is Class<*> && it.javaClass.isAssignableFrom(e.getGenericType() as Class<*>) }
                .forEach { RegistrationHandlerInternal.registerGeneric(e.getRegistry(), it) }
    }
}
