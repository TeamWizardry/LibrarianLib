package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentRecipe
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.helper.TranslationHolder
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageRecipe(override val entry: Entry, jsonElement: JsonObject) : Page {

    private val recipes: List<ResourceLocation>
    private val subtext = TranslationHolder.fromJson(jsonElement.get("subtext"))

    init {
        val recipeObj = jsonElement.get("recipe") ?: jsonElement.get("recipes")
        val allRecipes = mutableListOf<ResourceLocation>()
        recipes = allRecipes
        if (recipeObj.isJsonPrimitive)
            allRecipes.add(ResourceLocation(recipeObj.asString))
        else if (recipeObj.isJsonArray)
            recipeObj.asJsonArray.mapTo(allRecipes) { ResourceLocation(it.asString) }
    }

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<() -> GuiComponent> {
        return mutableListOf({ ComponentRecipe(16, 16, size.xi, size.yi, book.book.bookColor, recipes, book.processArrow, subtext) })
    }
}
