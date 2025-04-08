package com.teamwizardry.librarianlib.testcore.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import net.fabricmc.api.DedicatedServerModInitializer

internal object LLTestCoreTestServer : DedicatedServerModInitializer {
    val manager: TestModContentManager = LLTestCoreTestCommon.manager

    private val logger = LLTestCoreTest.logManager.makeLogger<LLTestCoreTestServer>()

    override fun onInitializeServer() {
        manager.registerServer()
    }
}