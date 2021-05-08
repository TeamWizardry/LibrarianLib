package com.teamwizardry.librarianlib.testcore.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.objects.TestItem
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

object LLTestCoreTestClient : ClientModInitializer {
    val manager: TestModContentManager = LLTestCoreTestCommon.manager

    private val logger = TestCoreTest.logManager.makeLogger<LLTestCoreTestClient>()

    override fun onInitializeClient() {
        manager.named<TestItem>("sided") {
            client {
                rightClick {
                    val player = MinecraftClient.getInstance().player
                    chat("[Client] player name: ${player?.name}")
                }
            }
        }
    }
}