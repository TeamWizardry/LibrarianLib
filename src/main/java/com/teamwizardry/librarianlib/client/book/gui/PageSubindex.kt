package com.teamwizardry.librarianlib.client.book.gui

import com.teamwizardry.librarianlib.client.book.data.DataNode
import com.teamwizardry.librarianlib.client.book.data.DataNodeParsers
import com.teamwizardry.librarianlib.client.book.util.BookSectionOther
import com.teamwizardry.librarianlib.client.book.util.LinkParser
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.*
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
class PageSubindex(section: BookSectionOther, node: DataNode, tag: String) : GuiBook(section) {

    init {
        val itemsPerPage = 8

        val indexPage = node["page"].asInt()

        val items = node["items"]

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
            comp.BUS.hook(GuiComponent.MouseInEvent::class.java) { event ->
                addTextSlider(comp, comp.pos.yi, node.get("tip").asStringOr("<NULL>"))
            }
            comp.BUS.hook(GuiComponent.MouseOutEvent::class.java) { event ->
                removeSlider(comp)
            }
        }

        val text = ComponentText(18, 6)
        text.text.setValue(node.get("text").asStringOr("default"))
        comp.add(text)
        ButtonMixin(comp) {}
        comp.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when (event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> text.text.setValue(node.get("text").asStringOr("default"))
                ButtonMixin.EnumButtonState.HOVER -> text.text.setValue("§n" + node["text"].asStringOr("default"))
            }
        }
        comp.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            val link = LinkParser.parse(node["link"].asStringOr("/"))
            openPageRelative(link.path, link.tag)
        }
    }
}
