package com.teamwizardry.librarianlib.core.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

internal object LLCoreTestClient : ClientModInitializer {
    val manager: TestModContentManager = LLCoreTestCommon.manager

    private val logger = LLCoreTest.logManager.makeLogger<LLCoreTestClient>()

    override fun onInitializeClient() {
        manager.registerClient()
    }
}