package com.teamwizardry.librarianlib.testcore.test

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestEntity
import com.teamwizardry.librarianlib.testcore.content.TestItem
import com.teamwizardry.librarianlib.testcore.junit.UnitTestSuite
import net.fabricmc.api.ModInitializer
import net.minecraft.util.registry.Registry

internal object LLTestCoreTestCommon : ModInitializer {
    val manager: TestModContentManager = TestModContentManager("liblib-testcore-test", "Test Core", LLTestCoreTest.logManager)

    private val logger = LLTestCoreTest.logManager.makeLogger<LLTestCoreTestCommon>()

    override fun onInitialize() {
        Registry.register(UnitTestSuite.REGISTRY, manager.id("unit_tests"), UnitTestSuite().apply {
            add<UnitTestTests>()
        })

        manager.create<TestItem>("right_click_item") {
            name = "Right Click"

            common {
                rightClick { chat("[Common] rightClick") }
                rightClickAir { chat("[Common] rightClickAir") }
                rightClickBlock { chat("[Common] rightClickBlock") }
                rightClickEntity { chat("[Common] rightClickEntity") }
                rightClickHold { chat("[Common] rightClickHold") }
                rightClickRelease { chat("[Common] rightClickRelease") }
            }
            client {
                rightClick { chat("[Client] rightClick") }
                rightClickAir { chat("[Client] rightClickAir") }
                rightClickBlock { chat("[Client] rightClickBlock") }
                rightClickEntity { chat("[Client] rightClickEntity") }
                rightClickHold { chat("[Client] rightClickHold") }
                rightClickRelease { chat("[Client] rightClickRelease") }
            }
            server {
                rightClick { chat("[Server] rightClick") }
                rightClickAir { chat("[Server] rightClickAir") }
                rightClickBlock { chat("[Server] rightClickBlock") }
                rightClickEntity { chat("[Server] rightClickEntity") }
                rightClickHold { chat("[Server] rightClickHold") }
                rightClickRelease { chat("[Server] rightClickRelease") }
            }
        }

        manager.create<TestItem>("left_click_item") {
            name = "Left Click"
            common {
                leftClickBlock { chat("[Common] leftClickBlock") }
                leftClickEntity { chat("[Common] leftClickEntity") }
            }
            client {
                leftClickBlock { chat("[Client] leftClickBlock") }
                leftClickEntity { chat("[Client] leftClickEntity") }
            }
            server {
                leftClickBlock { chat("[Server] leftClickBlock") }
                leftClickEntity { chat("[Server] leftClickEntity") }
            }
        }

        manager.create<TestItem>("inventory_tick_item") {
            name = "Inventory Tick"
            common {
                inventoryTick { sneaking { chat("[Common] inventoryTick") } }
                tickInHand { sneaking { chat("[Common] tickInHand") } }
            }
            client {
                inventoryTick { sneaking { chat("[Client] inventoryTick") } }
                tickInHand { sneaking { chat("[Client] tickInHand") } }
            }
            server {
                inventoryTick { sneaking { chat("[Server] inventoryTick") } }
                tickInHand { sneaking { chat("[Server] tickInHand") } }
            }
        }

        manager.create<TestItem>("sided_item") {
            name = "Right Click"

            common {
                rightClick {
                    chat("[Common] rightClick")
                }
            }
        }

        manager.create<TestEntity>("simple_entity") {
            name = "A simple entity"
        }

        manager.registerCommon()
    }
}