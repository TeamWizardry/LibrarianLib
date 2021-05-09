package com.teamwizardry.librarianlib.etcetera.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import net.fabricmc.api.DedicatedServerModInitializer

internal object LLEtceteraTestServer : DedicatedServerModInitializer {
    val manager: TestModContentManager = LLEtceteraTestCommon.manager

    private val logger = LLEtceteraTest.logManager.makeLogger<LLEtceteraTestServer>()

    override fun onInitializeServer() {
        manager.registerServer()
    }
}