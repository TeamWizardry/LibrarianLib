package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.SlabBlock
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation slabs. The passed [modelName] is used for both the full-block model and the slab textures.
 * e.g. for oak slabs, the `modelName` would be `oak_planks`.
 *
 * Required textures:
 * - `<modid>:block/<modelName>.png`
 *
 * Required models:
 * - `<modid>:block/<modelName>.json`
 */
public open class BaseSlabBlock(properties: Properties, private val modelName: String): SlabBlock(properties),
    IFoundationBlock {
    override fun generateBlockState(gen: BlockStateProvider) {
        val modelLoc = gen.modLoc("block/$modelName")
        gen.slabBlock(this, modelLoc, modelLoc)
    }
}