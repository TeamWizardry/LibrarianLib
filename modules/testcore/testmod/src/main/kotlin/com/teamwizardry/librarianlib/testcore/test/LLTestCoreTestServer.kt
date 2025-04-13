package com.teamwizardry.librarianlib.testcore.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig
import com.teamwizardry.librarianlib.testcore.module.TestModuleServer

@AutoService(TestModuleServer::class)
internal class LLTestCoreTestServer : TestModuleServer {
    override val moduleId: String = "testcore"

    override fun initializeServer(config: TestModuleConfig) {
    }
}