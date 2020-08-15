package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.BaseMod
import com.teamwizardry.librarianlib.foundation.BaseTagFactory
import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec
import com.teamwizardry.librarianlib.foundation.registration.ItemSpec
import com.teamwizardry.librarianlib.foundation.registration.RenderLayerSpec
import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.Item
import net.minecraft.tags.BlockTags
import net.minecraft.tags.Tag
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-foundation-test")
object LibrarianLibFoundationTestMod: BaseMod(true) {
    init {
        registrationManager.add(
            BlockSpec("test_log")
                .renderLayer(RenderLayerSpec.SOLID)
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)
                .datagen { tags(ModTags.TEST_LOGS).name("Test Log") }
                .mapColor(MaterialColor.ADOBE)
                .block { BaseLogBlock(MaterialColor.PINK, it.blockProperties) }
        )
        registrationManager.datagen.blockTags.meta(BlockTags.LOGS, ModTags.TEST_LOGS)

        registrationManager.add(
            ItemSpec("test_item")
                .maxStackSize(16)
                .item { Item(it.itemProperties) }
        )
    }
}

object ModTags {
    private val tags = BaseTagFactory(LibrarianLibFoundationTestMod.modid)
    val TEST_LOGS: Tag<Block> = tags.block("test_logs")
}

internal val logger = LogManager.getLogger("LibrarianLib: Foundation Test")
