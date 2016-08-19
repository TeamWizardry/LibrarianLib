package com.teamwizardry.librarianlib.client.book.gui

import com.teamwizardry.librarianlib.client.book.util.BookSectionText
import com.teamwizardry.librarianlib.client.book.util.LinkParser
import com.teamwizardry.librarianlib.client.book.data.DataNode
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentMarkup
import com.teamwizardry.librarianlib.client.util.Color
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer

class PageText(section: BookSectionText, data: DataNode, tag: String) : GuiBook(section) {

    internal var pageNum = -1

    init {
        val fr = Minecraft.getMinecraft().fontRendererObj

        val markup = ComponentMarkup(0, 0, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT)
        contents.add(markup)
        markup.BUS.hook(GuiComponent.PreDrawEvent::class.java) { Minecraft.getMinecraft().fontRendererObj.unicodeFlag = true }
        fr.unicodeFlag = true

        val list = data.get("text").asList()

        var formats = ""

        for (i in list.indices) {
            val node = list[i]
            var str: String? = null

            if (node.isString) {
                str = node.asString()
                if (i != 0 && list[i - 1].isString) {
                    str = "\n\n" + str // if it's two strings then it should have a paragraph break
                }
            }
            if (node.isList) {
                str = node[0].asString()
            }

            if (str != null) {
                str = str.replace("\n", "§r§0\n")
                val elem = markup.create(str)
                elem.format.setValue(formats)

                if (node.isString) {
                    // do nothing
                } else if (node.isList) {
                    val type = node[1].asStringOr("<TYPE_ERROR>").toLowerCase()
                    if (type == "!link") {
                        val hoverColor = Color.argb(0xff0000EE.toInt())
                        val normalColor = Color.argb(0xff0F00B0.toInt())
                        elem.format.func { hover -> if (hover) "§n" else "" }
                        elem.color.func { hover -> if (hover) hoverColor else normalColor }

                        val link = LinkParser.parse(node[2].asStringOr("/error"))

                        elem.BUS.hook(ComponentMarkup.ElementClickEvent::class.java) { openPageRelative(link.path, link.tag) }
                    }
                }
                formats = FontRenderer.getFormatFromString(formats + str)
            }
        }

        fr.unicodeFlag = false
        markup.BUS.hook(GuiComponent.PostDrawEvent::class.java) { Minecraft.getMinecraft().fontRendererObj.unicodeFlag = false }

        if (pageNum != -1) {
            // the / font_height ) * font_height is to round it to the nearest font_height multiple (int division)
            markup.start.setValue((pageNum * GuiBook.PAGE_HEIGHT + 1) / fr.FONT_HEIGHT * fr.FONT_HEIGHT)
            markup.end.setValue(((pageNum + 1) * GuiBook.PAGE_HEIGHT + 1) / fr.FONT_HEIGHT * fr.FONT_HEIGHT)
        }
    }

}
