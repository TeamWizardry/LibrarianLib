package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.RotatedPillarBlock
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation rotated pillar blocks.
 *
 * Required textures:
 * - `<modid>:block/<block_id>_side.png`
 * - `<modid>:block/<block_id>_end.png`
 */
public open class BaseRotatedPillarBlock(override val properties: FoundationBlockProperties):
    RotatedPillarBlock(properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.axisBlock(this)
    }
}