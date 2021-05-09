package com.teamwizardry.librarianlib.core.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import net.fabricmc.api.DedicatedServerModInitializer

internal object LLCoreTestServer : DedicatedServerModInitializer {
    val manager: TestModContentManager = LLCoreTestCommon.manager

    private val logger = LLCoreTest.logManager.makeLogger<LLCoreTestServer>()

    override fun onInitializeServer() {
        manager.registerServer()
    }
}