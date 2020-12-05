package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.foundation.item.BaseBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.extensions.IForgeBlock

/**
 * An interface for implementing Foundation's extended block functionality.
 */
public interface IFoundationBlock: IForgeBlock {
    /**
     * Generates the models for this block
     */
    public fun generateBlockState(gen: BlockStateProvider) {}

    /**
     * Gets the this block's inventory block model name (e.g. the default, `block/block_id`). This is used by the
     * default [createBlockItem] implementation.
     */
    public fun inventoryModelName(): String {
        return "block/${block.registryName!!.path}"
    }

    /**
     * Creates a BlockItem for this block. By default it creates an item that uses the [inventoryModelName] model.
     */
    public fun createBlockItem(itemProperties: Item.Properties): BlockItem {
        return BaseBlockItem(block, itemProperties).useBlockModel(
            loc(block.registryName!!.namespace, inventoryModelName())
        )
    }
}