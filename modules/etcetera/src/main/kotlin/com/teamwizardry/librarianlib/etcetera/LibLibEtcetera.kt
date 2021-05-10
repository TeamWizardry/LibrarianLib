package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibEtcetera : LibLibModule("liblib-etcetera", "Etcetera") {
    internal object CommonInitializer : ModInitializer {
        private val logger = LibLibEtcetera.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    internal object ClientInitializer : ClientModInitializer {
        private val logger = LibLibEtcetera.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
        }
    }

    internal object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibEtcetera.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}

