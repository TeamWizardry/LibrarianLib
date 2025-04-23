package com.teamwizardry.librarianlib.core.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.core.test.tests.EasingTests
import com.teamwizardry.librarianlib.core.test.tests.LogLevelTests
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig
import com.teamwizardry.librarianlib.testcore.module.TestModule
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon

object LibLibCoreTest : TestModule("core", "Core") {

    @AutoService(TestModuleCommon::class)
    class CommonInit : TestModuleCommon {
        override val module = LibLibCoreTest

        override fun initializeCommon(config: TestModuleConfig) {
            config.unitTest("easings") {
                add<EasingTests>()
            }
            config.unitTest("log_levels") {
                add<LogLevelTests>()
            }
        }
    }
}