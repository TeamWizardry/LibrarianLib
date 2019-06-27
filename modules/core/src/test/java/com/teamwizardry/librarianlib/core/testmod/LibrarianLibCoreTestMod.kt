package com.teamwizardry.librarianlib.core.testmod

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.stream.Collectors
import kotlin.streams.asSequence

@Mod("librarianlib-core-testmod")
class LibrarianLibCoreTestMod {
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
        // Directly reference a log4j logger.
        @JvmStatic
        private val LOGGER = LogManager.getLogger()
    }
}
