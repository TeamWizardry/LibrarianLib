package com.teamwizardry.librarianlib.book.gui

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.components.*
import com.teamwizardry.librarianlib.gui.mixin.ButtonMixin
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.util.Link
import com.teamwizardry.librarianlib.book.util.Page
import com.teamwizardry.librarianlib.data.DataNode
import com.teamwizardry.librarianlib.data.DataNodeParsers
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
class PageSubindex(book: Book, rootData: DataNode, pageData: DataNode, page: Page) : GuiBook(book, rootData, pageData, page) {

    init {
        val itemsPerPage = 8

        val indexPage = pageData.get("page").asInt()

        val items = if (pageData.get("index").isString) rootData.get("subindexes").get(pageData.get("index").asStringOr("default")) else rootData.get("subindex")

        val begin = indexPage * itemsPerPage

        val list = ComponentList(0, 0)

        for (i in 0..itemsPerPage - 1) {
            if (items.get(begin + i).exists())
                addIndex(list, items.get(begin + i))
        }

        contents.add(list)
    }

    private fun addIndex(parent: GuiComponent<*>, node: DataNode) {
        val comp = ComponentVoid(0, 0, GuiBook.PAGE_WIDTH, 20)
        parent.add(comp)

        var icon: GuiComponent<*>? = null
        if (node.get("item").exists()) {
            val slot = ComponentSlot(0, 0)
            var stack = DataNodeParsers.parseStack(node.get("item"))

            var amountText = ""
            if (stack == null || stack.item == null) {
                stack = ItemStack(Blocks.STONE)
                amountText = if (stack == null) "~s~" else "~i~"
            }
            val _amountText = amountText
            slot.quantityText.add({ c, text -> _amountText })
            slot.stack.setValue(stack)

            slot.tooltip.setValue(false)
            icon = slot
        }
        if (node.get("icon").exists()) {
            val sprite = ComponentSprite(DataNodeParsers.parseSprite(node.get("icon")), 0, 0, 16, 16)
            icon = sprite
        }

        if (icon != null) {
            icon.pos = Vec2d(0.0, 2.0)
            comp.add(icon)
        }

        if (node.get("tip").exists()) {
            comp.mouseIn.add({ c, pos ->
                addTextSlider(comp, c.pos.yi, node.get("tip").asStringOr("<NULL>"))
                false
            })
            comp.mouseOut.add({ c, pos ->
                removeSlider(comp)
                false
            })
        }

        val text = ComponentText(18, 6)
        text.text.setValue(node.get("text").asString())
        comp.add(text)
        ButtonMixin(comp,
                { text.text.setValue(node.get("text").asString()) },
                { text.text.setValue("§n" + node.get("text").asString()) }, { }
        ) {
            val l = Link(node.get("link").asStringOr("/"))
            openPageRelative(l.path, l.page)
            var parnt = text.parent
            parnt = null
        }
    }
}
