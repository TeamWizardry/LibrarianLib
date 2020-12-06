package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock
import com.teamwizardry.librarianlib.foundation.registration.*
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileBlock
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.Items

object ModBlocks {
    val testTile: LazyBlock = LazyBlock()
    val testLog: LazyBlock = LazyBlock()
    val strangeLoot: LazyBlock = LazyBlock()

    val woodPlanks: LazyBlock = LazyBlock()
    val woodSlab: LazyBlock = LazyBlock()
    val woodStairs: LazyBlock = LazyBlock()
    val woodFence: LazyBlock = LazyBlock()
    val woodFenceGate: LazyBlock = LazyBlock()
    val woodWall: LazyBlock = LazyBlock()

    internal fun registerBlocks(registrationManager: RegistrationManager) {
        testTile.from(registrationManager.add(
            BlockSpec("test_tile")
                .notSolid()
                .tileEntity(ModTiles.testTile)
                .block { TestTileBlock(it.blockProperties) }
        ))

        testLog.from(registrationManager.add(
            BlockSpec("test_log")
                .renderLayer(RenderLayerSpec.SOLID)
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)
                .mapColor(MaterialColor.ADOBE)
                .block { BaseLogBlock(MaterialColor.PINK, it.blockProperties) }
                .datagen { tags(ModTags.TEST_LOGS).name("Test Log") }
        ))

        strangeLoot.from(registrationManager.add(
            BlockSpec("strange_loot")
                .datagen {
                    lootTable.custom {
                        setLootTable(block, createSingleItemDrop(Items.STICK, false))
                    }
                }
        ))

        val woodCollection = BuildingBlockCollection("wood_planks", "wood")
        woodPlanks.from(registrationManager.add(woodCollection.full))
        woodSlab.from(registrationManager.add(woodCollection.slab))
        woodStairs.from(registrationManager.add(woodCollection.stairs))
        woodFence.from(registrationManager.add(woodCollection.fence))
        woodFenceGate.from(registrationManager.add(woodCollection.fenceGate))
        woodWall.from(registrationManager.add(woodCollection.wall))
    }
}