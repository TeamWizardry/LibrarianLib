package com.teamwizardry.librarianlib.book

import com.teamwizardry.librarianlib.book.gui.GuiBook
import com.teamwizardry.librarianlib.book.util.BookRegistry
import com.teamwizardry.librarianlib.book.util.BookSection
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import java.util.*

/**
 * The class that defines a book and has helpers to open it. It defines:
 *
 *  * What mod the book is from
 *  * What the color of the book is
 *  * Whether the player has unlocked a page - implementation TODO
 *
 * @author Pierce Corcoran
 */
open class Book(val modid: String) {
    val history = Stack<Page>()

    fun pushHistory(section: BookSection, page: Int) : Page {
        val p = Page(section, page)
        history.push(p)
        return p
    }

    fun display() {
        Minecraft.getMinecraft().displayGuiScreen(if(history.size > 0) getScreen(history.pop()) else getScreen("/index", "")) // the page is pushed back on, so we have to pop it
    }

    fun display(path: String, tag: String) {
        Minecraft.getMinecraft().displayGuiScreen(getScreen(path, tag))
    }

    fun getScreen(page: Page): GuiScreen? {
        val scr = page.section.create("")
        scr?.jumpToPage(page.page)

        if(scr != null && history.size > 0 &&
                history.peek().section.entry.path == page.section.entry.path)
            history.pop()

        return scr
    }

    fun getScreen(path: String, tag: String): GuiScreen? {
        val scr = BookRegistry.getEntry(this, path).getTagged(tag).create(tag)

        if(scr != null && history.size > 0 &&
                history.peek().section.entry.path == scr.section.entry.path)
            history.pop()

        return scr
    }

    fun back() {
        if (history.empty())
            return
        history.pop()
        if (history.peek().gui != null)
            Minecraft.getMinecraft().displayGuiScreen(history.peek().gui) // if a save was saved, display that
        else
            Minecraft.getMinecraft().displayGuiScreen(getScreen(history.pop())) // the page is pushed back on, so we have to pop it
    }




}

data class Page(val section: BookSection, val page: Int, var gui: GuiBook? = null)