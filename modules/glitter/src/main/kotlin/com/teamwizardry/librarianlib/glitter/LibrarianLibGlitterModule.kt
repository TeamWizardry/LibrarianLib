package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.LibrarianLibModule
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

internal object LibrarianLibGlitterModule: LibrarianLibModule("glitter", "Glitter") {
    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        MinecraftForge.EVENT_BUS.register(ParticleSystemManager)
        MinecraftForge.EVENT_BUS.register(GlitterWorldCollider)
        MinecraftForge.EVENT_BUS.register(GlitterLightingCache)
    }
}

internal val logger = LibrarianLibGlitterModule.makeLogger(null)
