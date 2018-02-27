package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentRecipe
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageRecipe(override val entry: Entry, jsonElement: JsonObject) : Page {

    private val recipe = ResourceLocation(jsonElement.getAsJsonPrimitive("recipe").asString)

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<GuiComponent> {
        return mutableListOf<GuiComponent>(ComponentRecipe(0, 0, size.xi, size.yi, book.book.bookColor, recipe, book.nextSpritePressed))
    }
}
