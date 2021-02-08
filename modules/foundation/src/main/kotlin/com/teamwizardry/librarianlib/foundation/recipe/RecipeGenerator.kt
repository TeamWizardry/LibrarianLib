package com.teamwizardry.librarianlib.foundation.recipe

import net.minecraft.data.IFinishedRecipe
import java.util.function.Consumer

/**
 * @see net.minecraft.data.RecipeProvider.registerRecipes
 * @see net.minecraft.data.ShapedRecipeBuilder.shapedRecipe
 * @see net.minecraft.data.ShapelessRecipeBuilder.shapelessRecipe
 * @see net.minecraft.data.CookingRecipeBuilder.smeltingRecipe
 * @see net.minecraft.data.CookingRecipeBuilder.blastingRecipe
 * @see net.minecraft.data.CookingRecipeBuilder.cookingRecipe
 * @see net.minecraft.data.SingleItemRecipeBuilder.stonecuttingRecipe
 * @see net.minecraft.data.SmithingRecipeBuilder.smithingRecipe
 * @see net.minecraft.data.CustomRecipeBuilder
 * @see net.minecraft.item.crafting.SpecialRecipeSerializer
 */
public abstract class RecipeGenerator {
    public abstract fun addRecipes(consumer: Consumer<IFinishedRecipe>)


}