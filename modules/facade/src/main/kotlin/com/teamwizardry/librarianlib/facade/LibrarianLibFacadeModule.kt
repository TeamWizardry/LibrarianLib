package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.courier.CourierChannel
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

internal object LibrarianLibFacadeModule: LibrarianLibModule("facade", "Facade") {
    val channel: CourierChannel = CourierChannel(loc("librarianlib", "facade"), "0")

    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        StencilUtil.enableStencilBuffer()
    }
}

internal val logger = LibrarianLibFacadeModule.makeLogger(null)
