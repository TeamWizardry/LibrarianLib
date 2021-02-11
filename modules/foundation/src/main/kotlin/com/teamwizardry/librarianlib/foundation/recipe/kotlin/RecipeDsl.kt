package com.teamwizardry.librarianlib.foundation.recipe.kotlin

import com.teamwizardry.librarianlib.core.util.loc
import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.ShapelessRecipeBuilder
import net.minecraft.util.IItemProvider
import java.util.function.Consumer

@DslMarker
internal annotation class RecipeDslMarker

@RecipeDslMarker
public class RecipeDslContext(private val consumer: Consumer<IFinishedRecipe>, private val modid: String) {
    public fun shapeless(
        id: String,
        result: IItemProvider,
        count: Int = 1,
        config: ShapelessRecipeDsl.() -> Unit
    ) {
        val dsl = ShapelessRecipeDsl(result, count)
        dsl.config()
        dsl.buildRecipe(consumer, loc(modid, id))
    }

    public fun shaped(
        id: String,
        result: IItemProvider,
        count: Int = 1,
        config: ShapedRecipeDsl.() -> Unit
    ) {
        val dsl = ShapedRecipeDsl(result, count)
        dsl.config()
        dsl.buildRecipe(consumer, loc(modid, id))
    }
}
