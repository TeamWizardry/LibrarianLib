package com.teamwizardry.librarianlib.xtemplatex

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import net.minecraft.block.Block
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-xtemplatex")
class LibParticlesModule : LibrarianLibModule("xtemplatex", logger) {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        @JvmStatic
        fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/UTemplateU")
