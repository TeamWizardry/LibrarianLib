package com.teamwizardry.librarianlib.core

import net.minecraft.block.Block
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-core")
class LibCoreModule : LibrarianLibModule("core", logger) {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        @JvmStatic
        fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/Core")
