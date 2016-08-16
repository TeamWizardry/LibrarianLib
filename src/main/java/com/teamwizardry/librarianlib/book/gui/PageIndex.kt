package com.teamwizardry.librarianlib.book.gui

import com.teamwizardry.librarianlib.data.DataNodeParsers
import com.teamwizardry.librarianlib.gui.components.ComponentGrid
import com.teamwizardry.librarianlib.gui.components.ComponentSliderTray
import com.teamwizardry.librarianlib.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.gui.mixin.ButtonMixin
import com.teamwizardry.librarianlib.util.Color
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.util.Link
import com.teamwizardry.librarianlib.book.util.Page
import com.teamwizardry.librarianlib.sprite.Sprite
import com.teamwizardry.librarianlib.data.DataNode
import net.minecraft.util.ResourceLocation
import org.apache.commons.lang3.mutable.MutableObject

class PageIndex(book: Book, rootData: DataNode, pageData: DataNode, page: Page) : GuiBook(book, rootData, pageData, page) {

    init {

        val icons = pageData.get("icons").asList()

        val normalColor = Color.rgb(Integer.parseInt(pageData.get("normalColor").asStringOr("0"), 16))
        val hoverColor = Color.rgb(Integer.parseInt(pageData.get("hoverColor").asStringOr("00BFFF"), 16))
        val pressColor = Color.rgb(0x191970)

        val size = 32
        val sep = (GuiBook.PAGE_WIDTH - size * 3) / 2
        val grid = ComponentGrid(0, 0, size + sep, size + sep, 3)

        for (icon in icons) {

            val iconNormalColor = if (icon.get("normalColor").exists()) Color.rgb(Integer.parseInt(icon.get("normalColor").asString(), 16)) else normalColor
            val iconHoverColor = if (icon.get("hoverColor").exists()) Color.rgb(Integer.parseInt(icon.get("hoverColor").asString(), 16)) else hoverColor


            val sprite = ComponentSprite(DataNodeParsers.parseSprite(icon.get("icon")), 0, 0, size, size)

            ButtonMixin(sprite,
                    { sprite.color.setValue(iconNormalColor) }, { sprite.color.setValue(iconHoverColor) }, { sprite.color.setValue(pressColor) }
            ) {
                val l = Link(icon.get("link").asStringOr("/"))
                openPageRelative(l.path, l.page)
            }
            sprite.mouseIn.add({ c, pos ->
                addTextSlider(sprite, c.pos.yi, icon.get("text").asStringOr("<NULL>"))
                false
            })
            sprite.mouseOut.add({ c, pos ->
                removeSlider(sprite)
                false
            })

            grid.add(sprite)
        }

        contents.add(grid)
    }
}
