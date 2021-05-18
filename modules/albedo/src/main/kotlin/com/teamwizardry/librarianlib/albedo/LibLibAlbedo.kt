package com.teamwizardry.librarianlib.albedo

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibAlbedo : LibLibModule("liblib-albedo", "Albedo") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibAlbedo.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibAlbedo.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibAlbedo.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
