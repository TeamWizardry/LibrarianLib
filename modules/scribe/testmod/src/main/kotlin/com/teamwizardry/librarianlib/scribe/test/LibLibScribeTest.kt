package com.teamwizardry.librarianlib.scribe.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig
import com.teamwizardry.librarianlib.testcore.module.TestModule
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon

object LibLibScribeTest : TestModule("scribe", "Scribe") {

    @AutoService(TestModuleCommon::class)
    class CommonInit : TestModuleCommon {
        override val module = LibLibScribeTest

        override fun initializeCommon(config: TestModuleConfig) {
        }
    }
}
