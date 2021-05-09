package com.teamwizardry.librarianlib.etcetera.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

internal object LLEtceteraTestClient : ClientModInitializer {
    val manager: TestModContentManager = LLEtceteraTestCommon.manager

    private val logger = LLEtceteraTest.logManager.makeLogger<LLEtceteraTestClient>()

    override fun onInitializeClient() {
        manager.registerClient()
        Particles.registerClient()
    }
}