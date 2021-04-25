package com.teamwizardry.librarianlib.foundation.recipe.kotlin

import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.ShapedRecipeBuilder
import net.minecraft.data.ShapelessRecipeBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.tags.ITag
import net.minecraft.util.IItemProvider
import net.minecraft.util.Identifier
import java.util.function.Consumer

/**
 *
 */
@RecipeDslMarker
public class ShapedRecipeDsl(result: IItemProvider, count: Int) {
    private val builder: ShapedRecipeBuilder = ShapedRecipeBuilder.shapedRecipe(result, count)

    public var group: String = ""
        set(value) {
            field = value
            builder.setGroup(value)
        }

    public fun ingredient(tag: ITag<Item>): Ingredient = Ingredient.fromTag(tag)
    public fun ingredient(vararg items: IItemProvider): Ingredient = Ingredient.fromItems(*items)
    public fun ingredient(vararg stacks: ItemStack): Ingredient = Ingredient.fromStacks(*stacks)

    public operator fun String.unaryPlus() {
        builder.patternLine(this)
    }

    public operator fun Char.timesAssign(other: Ingredient) { builder.key(this, other) }
    public operator fun Char.timesAssign(other: ITag<Item>) { builder.key(this, other) }
    public operator fun Char.timesAssign(other: IItemProvider) { builder.key(this, other) }

    public fun criteria(config: RecipeCriteriaDsl.() -> Unit) {
        RecipeCriteriaDsl(builder::addCriterion).config()
    }

    public fun buildRecipe(consumer: Consumer<IFinishedRecipe>, id: Identifier) {
        builder.build(consumer, id)
    }
}