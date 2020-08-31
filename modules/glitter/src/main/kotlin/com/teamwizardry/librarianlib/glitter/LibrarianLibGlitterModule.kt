package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.LibrarianLibModule
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

object LibrarianLibGlitterModule : LibrarianLibModule("glitter", "Glitter") {
    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        MinecraftForge.EVENT_BUS.register(ParticleSystemManager)
        MinecraftForge.EVENT_BUS.register(GlitterWorldCollider)
    }
}

internal val logger = LibrarianLibGlitterModule.makeLogger(null)
