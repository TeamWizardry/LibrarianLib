package com.teamwizardry.librarianlib.courier

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibCourier : LibLibModule("liblib-courier", "Courier") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibCourier.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibCourier.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibCourier.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
