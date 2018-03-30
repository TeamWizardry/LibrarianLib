package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentBuiltinStructure
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
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

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<GuiComponent> {
        return mutableListOf(ComponentBuiltinStructure(0, 0, size.xi, size.yi, structureName, subtext))
    }
}
