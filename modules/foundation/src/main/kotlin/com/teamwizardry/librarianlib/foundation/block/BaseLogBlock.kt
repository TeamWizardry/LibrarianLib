package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.LogBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation log blocks.
 *
 * Required textures:
 * - `<modid>:block/<block_id>.png`
 * - `<modid>:block/<block_id>_top.png`
 *
 * @param verticalColorIn The color to use for the ends of the log
 */
public open class BaseLogBlock(
    verticalColorIn: MaterialColor,
    override val properties: FoundationBlockProperties
): LogBlock(verticalColorIn, properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.logBlock(this)
    }

    public companion object {
        @JvmField
        public val DEFAULT_PROPERTIES: FoundationBlockProperties = FoundationBlockProperties()
            .material(Material.WOOD)
            .hardnessAndResistance(2f)
            .sound(SoundType.WOOD)
            .fireInfo(5, 5)
    }
}