package com.teamwizardry.librarianlib.${lowername}

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLib${PascalName} : LibLibModule("liblib-${lowername}", "${humanName}") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLib${PascalName}.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLib${PascalName}.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLib${PascalName}.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
