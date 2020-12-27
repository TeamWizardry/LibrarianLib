package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for simple Foundation blocks.
 *
 * Required textures:
 * - `<modid>:block/<block_id>.png`
 */
public open class BaseBlock(override val properties: FoundationBlockProperties):
    Block(properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.simpleBlock(this)
    }

    public companion object {
        @JvmStatic
        public val defaultStoneProperties: FoundationBlockProperties
            get() = FoundationBlockProperties()
                .material(Material.ROCK)
                .mapColor(MaterialColor.STONE)
                .hardnessAndResistance(1.5f, 6f)

        @JvmStatic
        public val defaultMetalBlockProperties: FoundationBlockProperties
            get() = FoundationBlockProperties()
                .material(Material.IRON)
                .hardnessAndResistance(3f, 6f)
                .sound(SoundType.METAL)

        @JvmStatic
        public val defaultGemBlockProperties: FoundationBlockProperties
            get() = FoundationBlockProperties()
                .material(Material.IRON)
                .hardnessAndResistance(5f, 6f)
                .sound(SoundType.METAL)
    }
}