package com.teamwizardry.librarianlib.core.test

import com.teamwizardry.librarianlib.core.test.tests.EasingTests
import com.teamwizardry.librarianlib.core.test.tests.LogLevelTests
import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.junit.UnitTestSuite
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.util.registry.Registry

internal object LibLibCoreTest {
    val logManager: ModLogManager = ModLogManager("liblib-core-test", "LibrarianLib Core Test")
    val manager: TestModContentManager = TestModContentManager("liblib-core-test", "Core", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            Registry.register(UnitTestSuite.REGISTRY, manager.id("easings"), UnitTestSuite().apply {
                add<EasingTests>()
            })
            Registry.register(UnitTestSuite.REGISTRY, manager.id("log_levels"), UnitTestSuite().apply {
                add<LogLevelTests>()
            })

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