package com.teamwizardry.librarianlib.foundation.recipe

import net.minecraft.data.*
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.IItemProvider

public class DefaultRecipeBuilders {
    @JvmOverloads
    public fun shaped(result: IItemProvider, count: Int = 1): ShapedRecipeBuilder =
        ShapedRecipeBuilder.shapedRecipe(result, count)

    @JvmOverloads
    public fun shapeless(result: IItemProvider, count: Int = 1): ShapelessRecipeBuilder =
        ShapelessRecipeBuilder.shapelessRecipe(result, count)

    /**
     * Used for furnaces
     */
    public fun smelting(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder =
        CookingRecipeBuilder.cookingRecipe(ingredient, result, experience, cookingTime, IRecipeSerializer.SMELTING)

    /**
     * Used for ores and general "melting" recipes, with a 0.5x cooking time multiplier
     */
    public fun blasting(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder =
        CookingRecipeBuilder.cookingRecipe(ingredient, result, experience, cookingTime, IRecipeSerializer.BLASTING)

    /**
     * Used for food, with a 0.5x cooking time multiplier
     */
    public fun smoker(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder =
        CookingRecipeBuilder.cookingRecipe(ingredient, result, experience, cookingTime, IRecipeSerializer.SMOKING)

    /**
     * Used for food, with a 3x cooking time multiplier
     */
    public fun campfire(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder = CookingRecipeBuilder.cookingRecipe(
        ingredient,
        result,
        experience,
        cookingTime,
        IRecipeSerializer.CAMPFIRE_COOKING
    )

    @JvmOverloads
    public fun stonecutting(ingredient: Ingredient, result: IItemProvider, count: Int = 1): SingleItemRecipeBuilder =
        SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, result, count)

    public fun smithing(base: Ingredient, addition: Ingredient, output: IItemProvider): SmithingRecipeBuilder =
        SmithingRecipeBuilder.smithingRecipe(base, addition, output.asItem())
}