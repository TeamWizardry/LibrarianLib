package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.category

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentCategoryPage
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentEntryPage
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
class Category(val book: Book, json: JsonObject) : IBookElement {

    val entries: List<Entry>
    val titleKey: String
    val descKey: String
    val icon: JsonElement
    val color: Color

    var isValid = false

    val isSingleEntry: Boolean
        get() = entries.size == 1

    override val bookParent: IBookElement?
        get() = book

    init {
        var title = ""
        var desc = ""
        var icon: JsonElement = JsonObject()
        val entries = mutableListOf<Entry>()
        var color = book.highlightColor

        try {
            title = json.getAsJsonPrimitive("title").asString
            desc = json.getAsJsonPrimitive("description").asString
            icon = json.get("icon")
            if (json.has("color"))
                color = Book.colorFromJson(json.get("color"))
            val allEntries = json.getAsJsonArray("entries")
            for (entryJson in allEntries) {
                val parsable = Book.getJsonFromLink(entryJson.asString)
                val entry = Entry(this, entryJson.asString, parsable!!.asJsonObject)
                if (entry.isValid)
                    entries.add(entry)
            }
            if (!entries.isEmpty())
                isValid = true
        } catch (exception: Exception) {
            LibrarianLog.error(exception, "Failed trying to parse a category component")
        }

        this.titleKey = title
        this.descKey = desc
        this.icon = icon
        this.entries = entries
        this.color = color
    }

    fun anyUnlocked(player: EntityPlayer): Boolean {
        return entries.any { it.isUnlocked(player) }
    }

    @SideOnly(Side.CLIENT)
    override fun createComponent(book: IBookGui): GuiComponent {
        return if (isSingleEntry) ComponentEntryPage(book, entries[0]) else ComponentCategoryPage(book, this)
    }
}
