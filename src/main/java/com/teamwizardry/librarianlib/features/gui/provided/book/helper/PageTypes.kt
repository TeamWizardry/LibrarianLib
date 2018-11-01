package com.teamwizardry.librarianlib.features.gui.provided.book.helper

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.*
import net.minecraft.util.ResourceLocation
import java.util.*

object PageTypes {

    private val pageProviders = HashMap<String, (Entry, JsonObject) -> Page>()

    init {
        registerPageProvider("text", ::PageText)
        registerPageProvider("recipe", ::PageRecipe)
        registerPageProvider("structure", ::PageStructure)
        registerPageProvider("builtin-structure", ::PageBuiltinStructure)
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
            pageProviders[type.toString()]
}
