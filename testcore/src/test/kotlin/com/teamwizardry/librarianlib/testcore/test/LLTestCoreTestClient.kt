package com.teamwizardry.librarianlib.testcore.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

internal object LLTestCoreTestClient : ClientModInitializer {
    val manager: TestModContentManager = LLTestCoreTestCommon.manager

    private val logger = LLTestCoreTest.logManager.makeLogger<LLTestCoreTestClient>()

    override fun onInitializeClient() {
        manager.named<TestItem>("sided_item") {
            client {
                rightClick {
                    val player = MinecraftClient.getInstance().player
                    chat("[Client] player name: ${player?.name}")
                }
            }
        }

        manager.registerClient()
    }
}