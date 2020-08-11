package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.foundation.registration.DefaultProperties
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
 */
class BaseLogBlock(verticalColorIn: MaterialColor, properties: Properties): LogBlock(verticalColorIn, properties), IFoundationBlock {
    override fun generateBlockState(gen: BlockStateProvider) {
        gen.logBlock(this)
        this.asItem()
    }

    companion object {
        @JvmField
        val DEFAULT_PROPERTIES: DefaultProperties = DefaultProperties()
            .material(Material.WOOD)
            .hardnessAndResistance(2f)
            .sound(SoundType.WOOD)
    }
}