package com.teamwizardry.librarianlib.testcore.module

import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig

public interface TestModuleCommon {
    public val moduleId: String
    public fun initializeCommon(config: TestModuleConfig)

    public companion object {
        public val instances: List<TestModuleCommon> by ServiceLoaderHelper.all()
    }
}