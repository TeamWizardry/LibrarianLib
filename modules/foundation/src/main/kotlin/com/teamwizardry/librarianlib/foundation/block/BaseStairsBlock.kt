package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.BlockState
import net.minecraft.block.StairsBlock
import net.minecraftforge.client.model.generators.BlockStateProvider
import java.util.function.Supplier

/**
 * A base class for Foundation slabs. The passed [textureName] is used for the model texture. e.g. for oak stairs, the
 * `textureName` would be `oak_planks`.
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 *
 * @param state A supplier returning the blockstate for the full block
 */
public open class BaseStairsBlock(
    state: Supplier<BlockState>,
    override val properties: FoundationBlockProperties,
    private val textureName: String
): StairsBlock(state, properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.stairsBlock(this, gen.modLoc("block/$textureName"))
    }
}