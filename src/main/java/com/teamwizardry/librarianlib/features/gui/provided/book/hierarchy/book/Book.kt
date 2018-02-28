package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentMainIndex
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.category.Category
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
@Suppress("LeakingThis")
open class Book(val location: ResourceLocation) : IBookElement {
    constructor(name: String) : this(ResourceLocation(currentModId, name))

    open var categories: List<Category> = listOf()
    open var headerKey: String = ""
    open var subtitleKey: String = ""
    open var bookColor: Color = Color.WHITE
    open var bindingColor: Color = Color.WHITE
    open var highlightColor: Color = Color.WHITE
    open var textureSheet: String = ""
    open var searchTextColor: Color = Color.WHITE
    open var searchTextHighlight: Color = Color.WHITE
    open var searchTextCursor: Color = Color.WHITE

    open val contentCache: Map<Entry, String>
        @SideOnly(Side.CLIENT) get() {
            val searchCache = mutableMapOf<Entry, String>()
            for (category in categories) {
                for (entry in category.entries) {
                    val searchBuilder = StringBuilder()
                    searchBuilder.append(I18n.format(entry.titleKey)).append(' ')
                            .append(I18n.format(entry.descKey)).append(' ')
                    for (page in entry.pages) {
                        var searchable = page.searchableKeys
                        if (searchable != null)
                            for (key in searchable)
                                searchBuilder.append(I18n.format(key)).append(' ')
                        searchable = page.searchableStrings
                        if (searchable != null)
                            for (value in searchable)
                                searchBuilder.append(value).append(' ')
                    }
                    searchCache[entry] = searchBuilder.toString()
                }
            }
            return searchCache
        }

    final override val bookParent: IBookElement?
        get() = null

    init {
        allBooks.add(this)
        if (hasEverReloaded)
            reload()
    }

    open fun reload() {
        try {
            val jsonElement = getJsonFromLink(location)
            if (jsonElement == null || !jsonElement.isJsonObject)
                return
            val json = jsonElement.asJsonObject
            bookColor = if (json.has("color"))
                colorFromJson(json.get("color"))
            else
                Color.WHITE
            bindingColor = if (json.has("binding"))
                colorFromJson(json.get("binding"))
            else
                bookColor.brighter().brighter()

            highlightColor = colorFromJson(json.get("highlight"))
            headerKey = json.getAsJsonPrimitive("title").asString
            subtitleKey = json.getAsJsonPrimitive("subtitle").asString

            textureSheet = if (json.has("texture_sheet"))
                json.getAsJsonPrimitive("texture_sheet").asString
            else
                "${LibrarianLib.MODID}:gui/book/guide_book"

            searchTextColor = if (json.has("search_text_color"))
                colorFromJson(json.get("search_text_color"))
            else
                Color.WHITE
            searchTextHighlight = if (json.has("search_highlight_color"))
                colorFromJson(json.get("search_highlight_color"))
            else
                Color.BLUE
            searchTextCursor = if (json.has("search_cursor_color"))
                colorFromJson(json.get("search_cursor_color"))
            else
                Color(0xd0d0d0)

            val allCategories = json.getAsJsonArray("categories")
            categories = allCategories
                    .map { Category(this, it.asJsonObject) }
                    .filter { it.isValid }
        } catch (error: Exception) {
            LibrarianLog.error(error, "Failed trying to parse a book component")
        }

    }

    @SideOnly(Side.CLIENT)
    override fun createComponent(book: IBookGui): GuiComponent {
        return ComponentMainIndex(book)
    }

    companion object {

        var hasEverReloaded = false
        private val allBooks = mutableListOf<Book>()

        init {
            ClientRunnable.registerReloadHandler {
                hasEverReloaded = true
                for (book in allBooks)
                    book.reload()
            }
        }

        fun colorFromJson(element: JsonElement): Color {
            if (element.isJsonPrimitive) {
                val primitive = element.asJsonPrimitive
                return if (primitive.isNumber)
                    Color(primitive.asInt)
                else
                    Color(Integer.decode(element.asString)!!)
            } else if (element.isJsonArray) {
                val array = element.asJsonArray
                return Color(array.get(0).asInt, array.get(1).asInt, array.get(2).asInt)
            }
            return Color.WHITE
        }

        fun getJsonFromLink(location: String): JsonElement? {
            return getJsonFromLink(ResourceLocation(location))
        }

        fun getJsonFromLink(location: ResourceLocation): JsonElement? {
            val stream = LibrarianLib.PROXY.getResource(location.resourceDomain, "book/" + location.resourcePath + ".json")
                    ?: return null
            return JsonParser().parse(stream.reader())
        }
    }
}
