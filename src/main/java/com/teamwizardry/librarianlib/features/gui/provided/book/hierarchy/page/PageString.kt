package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class PageString(override val entry: Entry) : Page {

    @get:SideOnly(Side.CLIENT)
    abstract val text: String

    @SideOnly(Side.CLIENT)
    fun lineCount(size: Vec2d): Int {
        return Math.ceil(size.y / Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT).toInt() - 1
    }

    @SideOnly(Side.CLIENT)
    override fun createBookComponents(book: IBookGui, size: Vec2d): List<() -> GuiComponent> {
        val pages = mutableListOf<() -> GuiComponent>()

        val minecraft = Minecraft.getMinecraft()

        val lineCount = lineCount(size)

        val text = text.replace("<br>", "\n")

        val fr = minecraft.fontRenderer

        fr.bidiFlag = true
        fr.unicodeFlag = true
        val lines = fr.listFormattedStringToWidth(text, size.xi)

        val sections = mutableListOf<String>()

        val page = mutableListOf<String>()
        for (line in lines) {
            val trim = line.trim { it <= ' ' }
            if (!trim.isEmpty()) {
                page.add(trim)
                if (page.size >= lineCount) {
                    sections.add(page.joinToString("\n"))
                    page.clear()
                }
            }
        }

        if (!page.isEmpty())
            sections.add(page.joinToString("\n"))

        for (section in sections) {
            pages.add {
                val sectionComponent = ComponentText(16, 16, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
                sectionComponent.text.setValue(section)
                sectionComponent.wrap.setValue(size.xi)
                sectionComponent.unicode.setValue(true)
                sectionComponent
            }
        }
        return pages
    }
}
