package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.BaseMod
import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec
import com.teamwizardry.librarianlib.foundation.registration.ItemSpec
import com.teamwizardry.librarianlib.foundation.registration.RenderLayerSpec
import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.Item
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-foundation-test")
object LibrarianLibFoundationTestMod: BaseMod(true) {
    init {
        registrationManager.add(
            BlockSpec("test_log")
                .renderLayer(RenderLayerSpec.SOLID)
                .withProperties(BaseLogBlock.DEFAULT_PROPERTIES)
                .mapColor(MaterialColor.ADOBE)
                .block { BaseLogBlock(MaterialColor.PINK, it.blockProperties) }
        )

        registrationManager.add(
            ItemSpec("test_item")
                .maxStackSize(16)
                .item { Item(it.itemProperties) }
        )
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Foundation Test")
