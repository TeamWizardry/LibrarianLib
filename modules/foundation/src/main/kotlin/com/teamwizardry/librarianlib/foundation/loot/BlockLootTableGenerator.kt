package com.teamwizardry.librarianlib.foundation.loot

import net.minecraft.advancements.criterion.StatePropertiesPredicate
import net.minecraft.block.Block
import net.minecraft.block.SlabBlock
import net.minecraft.state.properties.SlabType
import net.minecraft.util.IItemProvider
import net.minecraft.loot.*
import net.minecraft.loot.conditions.BlockStateProperty
import net.minecraft.loot.conditions.SurvivesExplosion
import net.minecraft.loot.functions.ExplosionDecay
import net.minecraft.loot.functions.SetCount

public open class BlockLootTableGenerator: LootTableGenerator(LootParameterSets.BLOCK) {
    public fun setLootTable(block: Block, table: LootTable.Builder) {
        addLootTable(block.lootTable, table)
    }

    public fun setLootTable(block: Block, vararg pools: LootPool.Builder) {
        addLootTable(block.lootTable, *pools)
    }

    /**
     * Creates a loot pool that drops a single item.
     *
     * @param item The item or block to drop
     * @param immuneToExplosions Whether explosions have a chance to prevent the item from dropping
     */
    public fun createSingleItemDrop(item: IItemProvider, immuneToExplosions: Boolean): LootPool.Builder {
        val pool = LootPool.builder()
            .rolls(ConstantRange.of(1))
            .addEntry(ItemLootEntry.builder(item))
        if(!immuneToExplosions)
            pool.acceptCondition(SurvivesExplosion.builder())
        return pool
    }

    /**
     * Creates a loot pool for slabs
     */
    public fun createDefaultSlabDrops(block: Block): LootPool.Builder {
        return LootPool.builder()
            .rolls(ConstantRange.of(1))
            .addEntry(
                ItemLootEntry.builder(block)
                    .acceptFunction(
                        SetCount.builder(ConstantRange.of(2))
                            .acceptCondition(
                                BlockStateProperty.builder(block)
                                    .fromProperties(
                                        StatePropertiesPredicate.Builder.newBuilder()
                                            .withProp(SlabBlock.TYPE, SlabType.DOUBLE)
                                    )
                            )
                    )
                    .acceptFunction(ExplosionDecay.builder())
            )
    }
}