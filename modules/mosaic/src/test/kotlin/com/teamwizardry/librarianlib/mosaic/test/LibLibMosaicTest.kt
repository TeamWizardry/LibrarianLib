package com.teamwizardry.librarianlib.mosaic.test

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibMosaicTest {
    val logManager: ModLogManager = ModLogManager("liblib-mosaic-test", "LibrarianLib Mosaic Test")
    val manager: TestModContentManager = TestModContentManager("liblib-mosaic-test", "Mosaic", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            manager.registerCommon()
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            manager.registerClient()
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}
