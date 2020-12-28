package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.FenceBlock
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation fences. The passed [textureName] is used for the model texture. e.g. for oak fences, the
 * `textureName` would be `oak_planks`.
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 */
public open class BaseFenceBlock(
    override val properties: FoundationBlockProperties,
    protected val textureName: String
): FenceBlock(properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.fenceBlock(this, gen.modLoc("block/$textureName"))
        gen.itemModels().fenceInventory("block/${registryName!!.path}_inventory", gen.modLoc("block/$textureName"))
    }

    override fun inventoryModelName(): String {
        return "block/${registryName!!.path}_inventory"
    }
}