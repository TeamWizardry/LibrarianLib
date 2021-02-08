package com.teamwizardry.librarianlib.foundation.recipe.kotlin

import com.teamwizardry.librarianlib.foundation.recipe.RecipeCriteria
import net.minecraft.advancements.ICriterionInstance
import net.minecraft.advancements.criterion.*
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tags.ITag
import net.minecraft.util.IItemProvider

@RecipeDslMarker
public class RecipeCriteriaDsl(private val addCriterion: (String, ICriterionInstance) -> Unit) {
    public fun add(name: String, criterion: ICriterionInstance) {
        addCriterion(name, criterion)
    }

    /**
     * Creates a new [EnterBlockTrigger] for use with recipe unlock criteria.
     */
    public fun enteredBlock(name: String, block: Block) {
        add(name, RecipeCriteria.enteredBlock(block))
    }

    /**
     * Creates a new [InventoryChangeTrigger] that checks for a player having a certain item.
     */
    public fun hasItem(name: String, item: IItemProvider) {
        add(name, RecipeCriteria.hasItem(item))
    }

    /**
     * Creates a new [InventoryChangeTrigger] that checks for a player having an item within the given tag.
     */
    public fun hasItem(name: String, tag: ITag<Item>) {
        add(name, RecipeCriteria.hasItem(tag))
    }

    /**
     * Creates a new [InventoryChangeTrigger] that checks for a player having a certain item.
     */
    public fun hasItem(name: String, vararg predicate: ItemPredicate) {
        add(name, RecipeCriteria.hasItem(*predicate))
    }
}