package com.teamwizardry.librarianlib.testcore.module

import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig

public interface TestModuleClient {
    public val module: TestModule
    public fun initializeClient(config: TestModuleConfig)

    public companion object {
        public val instances: List<TestModuleClient> by ServiceLoaderHelper.all()
    }
}