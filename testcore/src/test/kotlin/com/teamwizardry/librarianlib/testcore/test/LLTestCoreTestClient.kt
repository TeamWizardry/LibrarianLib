package com.teamwizardry.librarianlib.testcore.test

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import com.teamwizardry.librarianlib.testcore.content.utils.TestScreen
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

        manager.named<TestItem>("empty_screen") {
            rightClick.client {
                Client.minecraft.openScreen(TestScreen {
                })
            }
        }

        manager.named<TestItem>("simple_screen") {
            rightClick.client {
                Client.minecraft.openScreen(TestScreen {
                    draw {
                        fill(0, 0, 10, 10, 0xFFFF00FFu)
                    }
                })
            }
        }

        manager.named<TestItem>("sized_screen") {
            rightClick.client {
                Client.minecraft.openScreen(TestScreen {
                    size = vec(20, 20)

                    draw {
                        fill(0, 0, 20, 20, 0xFFFF00FFu)
                    }
                })
            }
        }

        manager.registerClient()
    }
}