package com.teamwizardry.librarianlib.testcore.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig
import com.teamwizardry.librarianlib.testcore.content.utils.TestScreen
import com.teamwizardry.librarianlib.testcore.module.TestModuleClient
import net.minecraft.client.MinecraftClient

@AutoService(TestModuleClient::class)
internal class LLTestCoreTestClient : TestModuleClient {
    override val moduleId: String = "testcore"

    override fun initializeClient(config: TestModuleConfig) {
        config.item("sided_item") {
            client {
                rightClick {
                    val player = MinecraftClient.getInstance().player
                    chat("[Client] player name: ${player?.name}")
                }
            }
        }

        config.item("empty_screen") {
            rightClick.client {
                Client.minecraft.setScreen(TestScreen {
                })
            }
        }

        config.item("simple_screen") {
            rightClick.client {
                Client.minecraft.setScreen(TestScreen {
                    draw {
                        fill(0, 0, 10, 10, 0xFFFF00FFu)
                    }
                })
            }
        }

        config.item("sized_screen") {
            rightClick.client {
                Client.minecraft.setScreen(TestScreen {
                    size = vec(20, 20)

                    draw {
                        fill(0, 0, 20, 20, 0xFFFF00FFu)
                    }
                })
            }
        }
    }
}