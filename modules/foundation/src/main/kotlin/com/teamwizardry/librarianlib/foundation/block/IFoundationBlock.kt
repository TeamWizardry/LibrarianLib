package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.foundation.item.BaseBlockItem
import com.teamwizardry.librarianlib.foundation.loot.BlockLootTableGenerator
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.extensions.IForgeBlock

/**
 * An interface for implementing Foundation's extended block functionality.
 */
public interface IFoundationBlock: IForgeBlock {
    public val properties: FoundationBlockProperties

    /**
     * Generates the models for this block
     */
    public fun generateBlockState(gen: BlockStateProvider) {}

    /**
     * Gets this block's inventory block model name (e.g. the default, `block/block_id`). This is used by the
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

    /**
     * Generates the loot table for this block. The default implementation directly drops this block
     */
    public fun generateLootTable(gen: BlockLootTableGenerator) {
        if(!properties.usesExternalLoot && block.asItem() != Items.AIR)
            gen.setLootTable(block, gen.createSingleItemDrop(block, false)) // TODO: configure explosion immunity?
    }

    @JvmDefault
    override fun getFlammability(state: BlockState?, world: IBlockReader?, pos: BlockPos?, face: Direction?): Int {
        return properties.getFlammabilityImpl(state, world, pos, face)
    }

    @JvmDefault
    override fun getFireSpreadSpeed(state: BlockState?, world: IBlockReader?, pos: BlockPos?, face: Direction?): Int {
        return properties.getFireSpreadSpeedImpl(state, world, pos, face)
    }
}