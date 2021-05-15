package com.teamwizardry.librarianlib.scribe

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibScribe : LibLibModule("liblib-scribe", "Scribe") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibScribe.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibScribe.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibScribe.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
