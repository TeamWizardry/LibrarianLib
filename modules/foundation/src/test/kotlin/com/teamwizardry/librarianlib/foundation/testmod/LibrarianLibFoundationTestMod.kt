package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.BaseMod
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec
import com.teamwizardry.librarianlib.foundation.registration.RenderLayerSpec
import net.minecraft.block.Block
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-foundation-test")
object LibrarianLibFoundationTestMod: BaseMod(true) {
    init {
        registrationManager.add(
            BlockSpec("test_block")
                .renderLayer(RenderLayerSpec.CUTOUT)
                .block { Block(it.blockProperties) }
        )
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Foundation Test")
