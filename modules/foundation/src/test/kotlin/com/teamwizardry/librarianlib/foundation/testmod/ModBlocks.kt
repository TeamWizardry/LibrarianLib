package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.*
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileBlock
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.Items

object ModBlocks {
    val testTile: LazyBlock = LazyBlock()
    val strangeLoot: LazyBlock = LazyBlock()

    val buildingBlock: LazyBlock = LazyBlock()
    val buildingBlockSlab: LazyBlock = LazyBlock()
    val buildingBlockStairs: LazyBlock = LazyBlock()
    val buildingBlockWall: LazyBlock = LazyBlock()

    val woodPlanks: LazyBlock = LazyBlock()
    val woodSlab: LazyBlock = LazyBlock()
    val woodStairs: LazyBlock = LazyBlock()
    val woodFence: LazyBlock = LazyBlock()
    val woodFenceGate: LazyBlock = LazyBlock()
    val woodLog: LazyBlock = LazyBlock()
    val strippedWoodLog: LazyBlock = LazyBlock()
    val strippedWood: LazyBlock = LazyBlock()
    val wood: LazyBlock = LazyBlock()
    val woodLeaves: LazyBlock = LazyBlock()

    internal fun registerBlocks(registrationManager: RegistrationManager) {
        testTile.from(registrationManager.add(
            BlockSpec("test_tile")
                .notSolid()
                .tileEntity(ModTiles.testTile)
                .block { TestTileBlock(it.blockProperties) }
        ))

        strangeLoot.from(registrationManager.add(
            BlockSpec("strange_loot")
                .datagen {
                    lootTable.custom {
                        setLootTable(block, createSingleItemDrop(Items.STICK, false))
                    }
                    tags(ModTags.STRANGE_BLOCK)
                }
        ))

        val buildingBlockCollection = BuildingBlockCollection("building_block", "building_block")
        buildingBlockCollection.blockProperties
            .material(Material.ROCK)
            .hardnessAndResistance(2f, 6f)
            .mapColor(MaterialColor.MAGENTA)
        buildingBlock.from(registrationManager.add(buildingBlockCollection.full))
        buildingBlockSlab.from(registrationManager.add(buildingBlockCollection.slab))
        buildingBlockStairs.from(registrationManager.add(buildingBlockCollection.stairs))
        buildingBlockWall.from(registrationManager.add(buildingBlockCollection.wall))

        val woodCollection = WoodBlockCollection(
            registrationManager,
            "test_wood",
            MaterialColor.PINK,
            MaterialColor.PINK_TERRACOTTA
        )
        woodPlanks.from(registrationManager.add(woodCollection.planks))
        woodSlab.from(registrationManager.add(woodCollection.slab))
        woodStairs.from(registrationManager.add(woodCollection.stairs))
        woodFence.from(registrationManager.add(woodCollection.fence))
        woodFenceGate.from(registrationManager.add(woodCollection.fenceGate))

        woodLog.from(registrationManager.add(woodCollection.log))
        strippedWoodLog.from(registrationManager.add(woodCollection.strippedLog))
        strippedWood.from(registrationManager.add(woodCollection.strippedWood))
        wood.from(registrationManager.add(woodCollection.wood))

        woodLeaves.from(registrationManager.add(woodCollection.leaves))
    }
}