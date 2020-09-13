package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

internal object LibrarianLibFacadeModule: LibrarianLibModule("facade", "Facade") {
    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        StencilUtil.enableStencilBuffer()
    }
}

internal val logger = LibrarianLibFacadeModule.makeLogger(null)
