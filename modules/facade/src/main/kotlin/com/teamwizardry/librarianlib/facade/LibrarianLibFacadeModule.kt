package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.courier.CourierChannel
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import com.teamwizardry.librarianlib.facade.container.messaging.MessagePacketType
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

internal object LibrarianLibFacadeModule: LibrarianLibModule("facade", "Facade") {
    val channel: CourierChannel = CourierChannel(loc("librarianlib", "facade"), "0")

    init {
        channel.register(MessagePacketType)
    }

    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        StencilUtil.enableStencilBuffer()
    }
}

internal val logger = LibrarianLibFacadeModule.makeLogger(null)
