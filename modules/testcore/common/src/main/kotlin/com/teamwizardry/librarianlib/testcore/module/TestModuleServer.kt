package com.teamwizardry.librarianlib.testcore.module

import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig

public interface TestModuleServer {
    public val module: TestModule
    public fun initializeServer(config: TestModuleConfig)

    public companion object {
        public val instances: List<TestModuleServer> by ServiceLoaderHelper.all()
    }
}