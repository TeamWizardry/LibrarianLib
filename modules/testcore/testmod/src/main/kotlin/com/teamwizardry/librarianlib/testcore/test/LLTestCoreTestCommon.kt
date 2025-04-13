package com.teamwizardry.librarianlib.testcore.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.testcore.content.*
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon

@AutoService(TestModuleCommon::class)
internal class LLTestCoreTestCommon : TestModuleCommon {
    override val moduleId: String = "testcore"

    override fun initializeCommon(config: TestModuleConfig) {
        config.unitTest("unit_tests") {
            add<UnitTestTests>()
        }

        config.item("right_click_item") {
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

        config.item("left_click_item") {
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

        config.item("inventory_tick_item") {
            name = "Inventory Tick"
            description = "Logs to chat when sneaking"
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

        config.item("sided_item") {
            name = "Right Click"

            common {
                rightClick {
                    chat("[Common] rightClick")
                }
            }
        }

        config.entity("simple_entity") {
            name = "Simple Entity"
        }

        config.item("empty_screen") {
            name = "Empty Screen"
        }
        config.item("simple_screen") {
            name = "Simple Screen"
            description = "(0, 0) should be located at the center of the screen"
        }
        config.item("sized_screen") {
            name = "Sized Screen"
            description = "The (20, 20) size means (10, 10) should be located at the center of the screen"
        }

        config.block("simple_block") {
            name = "Simple Block"
        }
        config.block("transparent_block") {
            name = "Transparent Block"
            transparent = true
        }
        config.block("facing_block") {
            name = "Facing Block"
        }
        config.block("events_block") {
            name = "Events Block"
            common {
                rightClick { chat("[Common] rightClick") }
                leftClick { chat("[Common] leftClick") }
                place { chat("[Common] place") }
                destroy { chat("[Common] destroy") }
            }

            server {
                rightClick { chat("[Server] rightClick") }
                leftClick { chat("[Server] leftClick") }
                place { chat("[Server] place") }
                destroy { chat("[Server] destroy") }
            }

            client {
                rightClick { chat("[Client] rightClick") }
                leftClick { chat("[Client] leftClick") }
                place { chat("[Client] place") }
                destroy { chat("[Client] destroy") } // not emitted clientside
            }
        }
    }
}