package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.recipe.RecipeGenerator
import com.teamwizardry.librarianlib.foundation.recipe.kotlin.RecipeDslContext
import net.minecraft.block.Blocks
import net.minecraft.data.IFinishedRecipe
import net.minecraft.item.Items
import net.minecraft.tags.ItemTags
import java.util.function.Consumer

object ModRecipes : RecipeGenerator() {
    override fun addRecipes(consumer: Consumer<IFinishedRecipe>) {
        val dsl = RecipeDslContext(consumer, "librarianlib-foundation-test")

        dsl.shapeless("dirt_to_diamonds", Items.DIAMOND, 10) {
            inputs = 1 * Items.DIRT + 3 * ItemTags.COALS

            criteria {
                hasItem("has_dirt", Items.DIRT)
            }
        }

        dsl.shaped("dirt_to_diamond_blocks", Blocks.DIAMOND_BLOCK, 1) {
            +"dcd"
            +"cdc"
            +"dcd"

            'd' *= Items.DIRT
            'c' *= ItemTags.COALS

            criteria {
                hasItem("has_dirt", Items.DIRT)
            }
        }

    }
}