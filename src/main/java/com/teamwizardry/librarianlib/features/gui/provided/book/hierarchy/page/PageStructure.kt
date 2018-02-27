package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentCachedStructure
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.StructureCacheRegistry
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageStructure(override val entry: Entry, `object`: JsonObject) : Page {

    private val structureName: String
    private val structure: RenderableStructure?

    override val searchableStrings: Collection<String>?
        get() = mutableListOf(structureName)

    init {
        structureName = `object`.getAsJsonPrimitive("name").asString
        structure = StructureCacheRegistry.getStructureOrAdd(structureName)
    }

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<GuiComponent> {
        return mutableListOf<GuiComponent>(
                ComponentCachedStructure(0, 0, size.xi, size.yi, structure))
    }
}
