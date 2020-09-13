package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

internal object LibrarianLibMosaicModule : LibrarianLibModule("mosaic", "Mosaic") {
    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        Client.resourceReloadHandler.register(MosaicLoader)
    }
}

internal val logger = LibrarianLibMosaicModule.makeLogger(null)
