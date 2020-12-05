package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.FenceBlock
import net.minecraft.block.WallBlock
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation walls. The passed [textureName] is used for the model texture. e.g. for cobblestone
 * walls, the `textureName` would be `cobblestone`.
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 */
public open class BaseWallBlock(properties: Properties, private val textureName: String): WallBlock(properties),
    IFoundationBlock {
    override fun generateBlockState(gen: BlockStateProvider) {
        gen.wallBlock(this, gen.modLoc("block/$textureName"))
        gen.itemModels().wallInventory("block/${registryName!!.path}_inventory", gen.modLoc("block/$textureName"))
    }

    override fun inventoryModelName(): String {
        return "block/${registryName!!.path}_inventory"
    }
}