package com.teamwizardry.librarianlib.core.test

import com.teamwizardry.librarianlib.core.test.tests.EasingTests
import com.teamwizardry.librarianlib.core.test.tests.LogLevelTests
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.junit.UnitTestSuite
import net.fabricmc.api.ModInitializer
import net.minecraft.util.registry.Registry

internal object LLCoreTestCommon : ModInitializer {
    val manager: TestModContentManager = TestModContentManager("liblib-core-test", LLCoreTest.logManager)

    private val logger = LLCoreTest.logManager.makeLogger<LLCoreTestCommon>()

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