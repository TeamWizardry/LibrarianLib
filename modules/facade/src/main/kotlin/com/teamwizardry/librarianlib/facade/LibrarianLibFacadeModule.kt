package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

public object LibrarianLibFacadeModule: LibrarianLibModule("facade", "Facade") {
    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        StencilUtil.enableStencilBuffer()
    }
}

internal val logger = LibrarianLibFacadeModule.makeLogger(null)
