package com.teamwizardry.librarianlib.features.gui.provided.book.provider

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.Page
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.PageRecipe
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.PageStructure
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.PageText
import net.minecraft.util.ResourceLocation
import java.util.*

object PageTypes {

    private val pageProviders = HashMap<String, (Entry, JsonObject) -> Page>()

    init {
        registerPageProvider("text", ::PageText)
        registerPageProvider("recipe", ::PageRecipe)
        registerPageProvider("structure", ::PageStructure)
    }

    fun registerPageProvider(name: String, provider: (Entry, JsonObject) -> Page) =
            registerPageProvider(ResourceLocation(name), provider)

    fun registerPageProvider(name: ResourceLocation, provider: (Entry, JsonObject) -> Page) {
        val key = name.toString()
        if (!pageProviders.containsKey(key))
            pageProviders[key] = provider
    }

    fun getPageProvider(type: String) =
            getPageProvider(ResourceLocation(type.toLowerCase(Locale.ROOT)))

    fun getPageProvider(type: ResourceLocation) =
            pageProviders.getOrDefault(type.toString(), null)
}
