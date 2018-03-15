package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageText(entry: Entry, jsonElement: JsonObject) : PageString(entry) {

    private val holder = TranslationHolder.fromJson(jsonElement)

    override val searchableStrings: Collection<String>?
        get() = listOf(holder.toString())

    override val text: String
        @SideOnly(Side.CLIENT)
        get() = holder.toString()
}
