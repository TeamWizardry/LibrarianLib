package com.teamwizardry.librarianlib.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.gui.provided.book.context.Bookmark
import com.teamwizardry.librarianlib.gui.provided.book.helper.TranslationHolder
import com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.gui.provided.book.structure.BookmarkDynamicStructure
import com.teamwizardry.librarianlib.gui.provided.book.structure.ComponentDynamicStructure
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 8:48 PM on 3/29/18.
 */

class PageBuiltinStructure(override val entry: Entry, element: JsonObject) : Page {

    private val structureName = ResourceLocation(element.getAsJsonPrimitive("name").asString)
    private val subtext = TranslationHolder.fromJson(element.get("subtext"))

    override val searchableStrings: Collection<String>?
        get() = mutableListOf(structureName.toString())

    override val extraBookmarks: List<Bookmark>
        get() = listOf(BookmarkDynamicStructure(structureName))

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<() -> GuiComponent> {
        return mutableListOf({ ComponentDynamicStructure(book, 16, 16, size.xi, size.yi, structureName, subtext) })
    }
}
