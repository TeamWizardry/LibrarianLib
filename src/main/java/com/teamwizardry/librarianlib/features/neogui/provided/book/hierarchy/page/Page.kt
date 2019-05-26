package com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy.page

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.neogui.provided.book.context.Bookmark
import com.teamwizardry.librarianlib.features.neogui.provided.book.helper.PageTypes
import com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface Page {

    val entry: Entry

    val searchableStrings: Collection<String>?
        get() = null

    val searchableKeys: Collection<String>?
        get() = null

    val extraBookmarks: List<Bookmark>
        get() = listOf()

    @SideOnly(Side.CLIENT)
    fun createBookComponents(book: IBookGui, size: Vec2d): List<() -> GuiComponent>

    companion object {

        fun fromJson(entry: Entry, element: JsonElement): Page? {
            try {
                var obj: JsonObject? = null
                var provider: ((Entry, JsonObject) -> Page)? = null
                if (element.isJsonPrimitive) {
                    provider = PageTypes.getPageProvider("text")
                    obj = JsonObject()
                    obj.addProperty("type", "text")
                    obj.add("value", element)
                } else if (element.isJsonObject) {
                    obj = element.asJsonObject
                    provider = PageTypes.getPageProvider(obj!!.getAsJsonPrimitive("type").asString)
                }

                return if (obj == null || provider == null) null else provider(entry, obj)

            } catch (error: Exception) {
                LibrarianLog.error(error, "Failed trying to parse a page component")
                return null
            }

        }
    }
}
