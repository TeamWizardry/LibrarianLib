package com.teamwizardry.librarianlib.testcore.test

import com.teamwizardry.librarianlib.testcore.TestModManager
import com.teamwizardry.librarianlib.testcore.objects.TestItem
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

object LLTestCoreTestClient : ClientModInitializer {
    val manager: TestModManager = LLTestCoreTestCommon.manager

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