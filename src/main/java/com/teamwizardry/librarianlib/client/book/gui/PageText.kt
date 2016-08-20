package com.teamwizardry.librarianlib.client.book.gui

import com.teamwizardry.librarianlib.client.book.data.DataNode
import com.teamwizardry.librarianlib.client.book.util.BookSectionText
import com.teamwizardry.librarianlib.client.book.util.LinkParser
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentMarkup
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import java.awt.Color

class PageText(section: BookSectionText, data: DataNode, tag: String) : GuiBook(section) {

    private var pageNum = 0
    private var maxPage: Int

    init {
        val fr = Minecraft.getMinecraft().fontRendererObj

        val markup = ComponentMarkup(0, 0, PAGE_WIDTH, PAGE_HEIGHT)
        contents.add(markup)
        markup.BUS.hook(GuiComponent.PreDrawEvent::class.java) { Minecraft.getMinecraft().fontRendererObj.unicodeFlag = true }
        fr.unicodeFlag = true

        val list = data.asList()

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
                        val hoverColor = Color(0x0000EE)
                        val normalColor = Color(0x0F00B0)
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

        markup.start.func { (pageNum * GuiBook.PAGE_HEIGHT + 1) / fr.FONT_HEIGHT * fr.FONT_HEIGHT }
        markup.end.func { ((pageNum + 1) * GuiBook.PAGE_HEIGHT + 1) / fr.FONT_HEIGHT * fr.FONT_HEIGHT }

        val h = markup.getLogicalSize()?.heightI() ?: 0
        maxPage = Math.floor(h.toDouble() / GuiBook.PAGE_HEIGHT).toInt()
    }


    override fun jumpToPage(page: Int) {
        pageNum = page
    }

    override fun pageJump(): Int {
        return pageNum
    }

    override fun maxpPageJump(): Int {
        return maxPage
    }

    override fun hasNextPage(): Boolean {
        return pageNum < maxPage
    }

    override fun hasPrevPage(): Boolean {
        return pageNum > 0
    }

    override fun goToNextPage() {
        pageNum++
    }

    override fun goToPrevPage() {
        pageNum--
    }
}
