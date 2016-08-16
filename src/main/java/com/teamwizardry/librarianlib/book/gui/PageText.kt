package com.teamwizardry.librarianlib.book.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer

import com.teamwizardry.librarianlib.gui.components.ComponentMarkup
import com.teamwizardry.librarianlib.gui.components.ComponentMarkup.MarkupElement
import com.teamwizardry.librarianlib.util.Color
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.util.Page
import com.teamwizardry.librarianlib.data.DataNode

class PageText(book: Book, rootData: DataNode, pageData: DataNode, page: Page) : GuiBook(book, rootData, pageData, page) {

    internal var pageNum = -1

    init {
        val fr = Minecraft.getMinecraft().fontRendererObj

        val markup = ComponentMarkup(0, 0, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT)
        contents.add(markup)
        markup.preDraw.add({ c, pos, ticks -> fr.unicodeFlag = true })
        fr.unicodeFlag = true

        var list: List<DataNode>? = null
        if (pageData.get("text").isList) {
            list = pageData.get("text").asList()
        } else {
            val texts = rootData.get("texts")
            list = texts.get(pageData.get("text").get("global").asStringOr("default")).asList()
            pageNum = pageData.get("text").get("page").asInt()
        }

        var formats = ""

        for (i in list!!.indices) {
            val node = list[i]
            var str: String? = null

            if (node.isString) {
                str = node.asString()
                if (i != 0 && list[i - 1].isString) {
                    str = "\n\n" + str!! // if it's two strings then it should have a paragraph break
                }
            }
            if (node.isMap) {
                str = node.get("text").asString()
            }

            if (str != null) {
                str = str.replace("\n", "§r§0\n")
                val elem = markup.create(str)
                elem.format.setValue(formats)

                if (node.isString) {
                    // do nothing
                } else if (node.isMap) {
                    val type = node.get("type").asStringOr("<err>")!!.toLowerCase()
                    if (type == "link") {
                        val hoverColor = Color.argb(0xff0000EE.toInt())
                        val normalColor = Color.argb(0xff0F00B0.toInt())
                        elem.format.func { hover -> if (hover) "§n" else "" }
                        elem.color.func { hover -> if (hover) hoverColor else normalColor }

                        val ref = node.get("ref").asStringOr("/error")
                        val colon = ref!!.lastIndexOf(":")
                        var linkPage = 0

                        if (colon != -1) {
                            try {
                                linkPage = Integer.parseInt(ref.substring(colon))
                            } catch (e: NumberFormatException) {
                                // TODO: logging
                            }

                        }

                        val path = ref.substring(0, if (colon == -1) ref.length else colon)
                        val _page = linkPage
                        elem.click.add({ openPageRelative(path, _page) })
                    }
                }
                formats = FontRenderer.getFormatFromString(formats + str)
            }
        }

        fr.unicodeFlag = false
        markup.postDraw.add({ c, pos, ticks -> fr.unicodeFlag = false })

        if (pageNum != -1) {
            // the / font_height ) * font_height is to round it to the nearest font_height multiple (int division)
            markup.start.setValue((pageNum * GuiBook.PAGE_HEIGHT + 1) / fr.FONT_HEIGHT * fr.FONT_HEIGHT)
            markup.end.setValue(((pageNum + 1) * GuiBook.PAGE_HEIGHT + 1) / fr.FONT_HEIGHT * fr.FONT_HEIGHT)
        }
    }

}
