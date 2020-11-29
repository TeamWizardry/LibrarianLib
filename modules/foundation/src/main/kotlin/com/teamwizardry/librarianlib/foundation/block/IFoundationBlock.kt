package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.extensions.IForgeBlock

/**
 * An interface for implementing Foundation's extended block functionality.
 */
public interface IFoundationBlock: IForgeBlock {
    /**
     * Generates the models for this block
     */
    public fun generateBlockState(gen: BlockStateProvider) { }

    /**
     * Creates a BlockItem for this block. This can be overridden in the BlockSpec
     */
    public fun createBlockItem(itemProperties: Item.Properties): BlockItem {
        return BlockItem(this.block, itemProperties)
    }
}