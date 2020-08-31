package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec
import com.teamwizardry.librarianlib.foundation.registration.LazyBlock
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.registration.RenderLayerSpec
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileBlock
import net.minecraft.block.material.MaterialColor

object ModBlocks {
    val testTile: LazyBlock = LazyBlock()
    val testLog: LazyBlock = LazyBlock()

    internal fun registerBlocks(registrationManager: RegistrationManager) {
        testTile.from(registrationManager.add(
            BlockSpec("test_tile")
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

    }
}