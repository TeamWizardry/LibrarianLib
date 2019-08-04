package com.teamwizardry.librarianlib.core

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.block.Block
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager

@Mod(LibCoreModule.MODID)
class LibCoreModule : LibrarianLibModule(MODID) {
    init {
        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> {
            this.setup(it)
        }
        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLClientSetupEvent> {
            this.clientSetup(it)
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        Client // start counting ticks immediately
    }

    private fun setup(event: FMLCommonSetupEvent) {
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
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
            // register a new block here
//            LOGGER.info("HELLO from Register Block")
        }
    }

    companion object {
        const val MODID: String = "librarianlib-core"
        // Directly reference a log4j logger.
    }
}
