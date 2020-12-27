package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.FenceBlock
import net.minecraft.block.TrapDoorBlock
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation trapdoors.
 *
 * Required textures:
 * - `<modid>:block/<block_id>.png`
 */
public open class BaseTrapDoorBlock(
    override val properties: FoundationBlockProperties
): TrapDoorBlock(properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.trapdoorBlock(this, gen.modLoc("block/${registryName!!.path}"), true)
    }

    override fun inventoryModelName(): String {
        return "block/${block.registryName!!.path}_bottom"
    }
}