package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibFacade : LibLibModule("liblib-facade", "Facade") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibFacade.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibFacade.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibFacade.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
