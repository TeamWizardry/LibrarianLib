package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.loc
import net.minecraft.block.Block
import net.minecraft.block.LeavesBlock
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
public open class BaseLeavesBlock(override val properties: FoundationBlockProperties):
    LeavesBlock(properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.simpleBlock(
            this,
            gen.models().singleTexture(
                registryName!!.path,
                loc("minecraft:block/leaves"),
                "all", gen.blockTexture(this)
            )
        )
    }

    public companion object {
        @JvmStatic
        public val defaultProperties: FoundationBlockProperties
            get() = FoundationBlockProperties()
                .material(Material.LEAVES)
                .hardnessAndResistance(0.2f)
                .tickRandomly()
                .sound(SoundType.PLANT)
                .notSolid()
                .fireInfo(30, 60)
    }
}