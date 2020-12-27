package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.foundation.item.BaseBlockItem
import net.minecraft.block.DoorBlock
import net.minecraft.block.FenceBlock
import net.minecraft.block.TrapDoorBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation doors.
 *
 * Required textures:
 * - `<modid>:block/<block_id>_top.png`
 * - `<modid>:block/<block_id>_bottom.png`
 * - `<modid>:item/<block_id>.png`
 */
public open class BaseDoorBlock(
    override val properties: FoundationBlockProperties
): DoorBlock(properties.vanillaProperties), IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.doorBlock(this,
            gen.modLoc("block/${registryName!!.path}_bottom"),
            gen.modLoc("block/${registryName!!.path}_top")
        )
    }

    override fun createBlockItem(itemProperties: Item.Properties): BlockItem {
        return BaseBlockItem(block, itemProperties) // no parent model => flat texture
    }
}