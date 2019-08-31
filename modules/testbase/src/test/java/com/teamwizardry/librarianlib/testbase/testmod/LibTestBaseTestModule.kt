package com.teamwizardry.librarianlib.testbase.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestBlock
import com.teamwizardry.librarianlib.testbase.objects.TestBlockConfig
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-testbase-test")
class LibTestBaseTestModule: TestMod("testbase", "Test Base", logger) {
    init {
        +TestItem(TestItemConfig("right_click", "Right Click") {
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
        })
        +TestItem(TestItemConfig("left_click", "Left Click") {
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
        })
        +TestItem(TestItemConfig("inventory_tick", "Inventory Tick") {
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
        })

        +TestBlock(TestBlockConfig("normal_solid", "Normal Solid Block") {
        })
        +TestBlock(TestBlockConfig("normal_transparent", "Normal Transparent Block") {
            transparent = true
        })

        +TestBlock(TestBlockConfig("directional_solid", "Directional Solid Block") {
            directional = true
        })
        +TestBlock(TestBlockConfig("directional_transparent", "Directional Transparent Block") {
            directional = true
            transparent = true
        })

        +TestBlock(TestBlockConfig("right_click", "Right Click Block") {
            client {
                rightClick { chat("[Client] rightClick") }
            }
            common {
                rightClick { chat("[Common] rightClick") }
            }
            server {
                rightClick { chat("[Server] rightClick") }
            }
        })
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/Test Base/Test")
