package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.SidedRunnable
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

object LibrarianLibFacadeModule: LibrarianLibModule("facade", logger) {
    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        StencilUtil.enableStencilBuffer()
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Facade")
