package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentRenderableStructure
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.StructureCacheRegistry
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageStructure(override val entry: Entry, element: JsonObject) : Page {

    private val structureName: String = element.getAsJsonPrimitive("name").asString
    private val structure by lazy(StructureCacheRegistry.getPromise(structureName))
    private val subtext = TranslationHolder.fromJson(element.get("subtext"))

    override val searchableStrings: Collection<String>?
        get() = mutableListOf(structureName)

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<GuiComponent> {
        return mutableListOf<GuiComponent>(ComponentRenderableStructure(book, 0, 0, size.xi, size.yi, structure, subtext))
    }
}
