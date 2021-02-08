package com.teamwizardry.librarianlib.foundation.recipe

import net.minecraft.data.CookingRecipeBuilder
import net.minecraft.data.IFinishedRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.IItemProvider
import java.util.function.Consumer

public class CookingRecipePresets {
    /**
     * Adds the default smelting, smoking, and campfire recipes
     */
    public fun buildFoodCookingRecipes(
        consumer: Consumer<IFinishedRecipe>,

        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        config: Consumer<CookingRecipeBuilder>
    ) {

    }
}