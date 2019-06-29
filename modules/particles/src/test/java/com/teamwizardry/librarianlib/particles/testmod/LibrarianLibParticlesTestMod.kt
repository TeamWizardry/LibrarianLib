package com.teamwizardry.librarianlib.particles.testmod

import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntityRenderer
import com.teamwizardry.librarianlib.particles.testmod.init.TestEntities
import com.teamwizardry.librarianlib.particles.testmod.init.TestItems
import net.minecraft.block.Block
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager

internal const val modid: String = "librarianlib-particles-testmod"

@Mod(modid)
class LibrarianLibParticlesTestMod {
    init {
        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> {
            this.setup(it)
        }
        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLClientSetupEvent> {
            this.clientSetup(it)
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun setup(event: FMLCommonSetupEvent) {
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
        RenderingRegistry.registerEntityRenderingHandler(ParticleSpawnerEntity::class.java) { ParticleSpawnerEntityRenderer(it) }
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        @JvmStatic
        fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
        }

        @SubscribeEvent
        @JvmStatic
        fun onItemRegister(itemRegistryEvent: RegistryEvent.Register<Item>) {
            TestItems.register()
        }

        @SubscribeEvent
        @JvmStatic
        fun onEntityRegister(entityRegistryEvent: RegistryEvent.Register<@JvmSuppressWildcards EntityType<*>>) {
            TestEntities.register()
        }
    }

    companion object {
        // Directly reference a log4j logger.
        @JvmStatic
        private val LOGGER = LogManager.getLogger()
    }
}
