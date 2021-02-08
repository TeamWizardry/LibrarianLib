package com.teamwizardry.librarianlib.foundation.recipe

import net.minecraft.advancements.criterion.*
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tags.ITag
import net.minecraft.util.IItemProvider

/**
 * A collection of preset recipe unlock criteria
 */
public object RecipeCriteria {
    /**
     * Creates a new [EnterBlockTrigger] for use with recipe unlock criteria.
     */
    public fun enteredBlock(block: Block): EnterBlockTrigger.Instance {
        return EnterBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, block, StatePropertiesPredicate.EMPTY)
    }

    /**
     * Creates a new [InventoryChangeTrigger] that checks for a player having a certain item.
     */
    public fun hasItem(item: IItemProvider): InventoryChangeTrigger.Instance {
        return hasItem(ItemPredicate.Builder.create().item(item).build())
    }

    /**
     * Creates a new [InventoryChangeTrigger] that checks for a player having an item within the given tag.
     */
    public fun hasItem(tag: ITag<Item>): InventoryChangeTrigger.Instance {
        return hasItem(ItemPredicate.Builder.create().tag(tag).build())
    }

    /**
     * Creates a new [InventoryChangeTrigger] that checks for a player having a certain item.
     */
    public fun hasItem(vararg predicate: ItemPredicate): InventoryChangeTrigger.Instance {
        return InventoryChangeTrigger.Instance(
            EntityPredicate.AndPredicate.ANY_AND,
            MinMaxBounds.IntBound.UNBOUNDED,
            MinMaxBounds.IntBound.UNBOUNDED,
            MinMaxBounds.IntBound.UNBOUNDED,
            predicate
        )
    }
}