package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.foundation.item.BaseBlockItem
import com.teamwizardry.librarianlib.foundation.registration.DefaultProperties
import net.minecraft.block.Block
import net.minecraft.block.LogBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for simple Foundation blocks.
 *
 * Required textures:
 * - `<modid>:block/<block_id>.png`
 */
public open class BaseSimpleBlock(properties: Properties): Block(properties), IFoundationBlock {
    override fun generateBlockState(gen: BlockStateProvider) {
        gen.simpleBlock(this)
    }

    public companion object {
        @JvmField
        public val STONE_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.ROCK)
            .mapColor(MaterialColor.STONE)
            .hardnessAndResistance(1.5f, 6f)

        @JvmField
        public val OBSIDIAN_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.ROCK)
            .mapColor(MaterialColor.BLACK)
            .hardnessAndResistance(50f, 1200f)

        @JvmField
        public val DIRT_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.EARTH)
            .mapColor(MaterialColor.DIRT)
            .hardnessAndResistance(0.5f)
            .sound(SoundType.GROUND)

        @JvmField
        public val PLANK_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.WOOD)
            .mapColor(MaterialColor.WOOD)
            .hardnessAndResistance(2f, 3f)
            .sound(SoundType.WOOD)

        @JvmField
        public val STONE_BRICKS_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.ROCK)
            .hardnessAndResistance(2f, 6f)

        @JvmField
        public val METAL_BLOCK_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.IRON)
            .hardnessAndResistance(3f, 6f)
            .sound(SoundType.METAL)

        @JvmField
        public val GEM_BLOCK_DEFAULTS: DefaultProperties = DefaultProperties()
            .material(Material.IRON)
            .hardnessAndResistance(5f, 6f)
            .sound(SoundType.METAL)
    }
}