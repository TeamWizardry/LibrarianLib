package com.teamwizardry.librarianlib.book

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.book.util.PageHandler
import com.teamwizardry.librarianlib.book.gui.GuiBook
import com.teamwizardry.librarianlib.book.util.Page
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen

import java.util.Stack

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
    /**
     * The history of the pages used to get to the current page. The top element is the current page.
     */
    val history = Stack<Page>()

    init {
        history.push(Page("/", 0))
    }

    protected open fun canOpenPage(path: String): Boolean {
        return true
    }

    protected open fun getScreen(page: Page): GuiScreen? {
        var newPage = page
        var scr = PageHandler.create(this, newPage)
        if (scr == null) {
            LibrarianLog.warn("Page [%s:%d] not found! Going to [/:0] ", newPage.path, newPage.page)
            newPage = Page("/", 0)
            scr = PageHandler.create(this, newPage)
        }
        newPage.gui = scr
        if (history.empty())
            history.push(Page("/", 0))
        if (history.peek().path == newPage.path)
            history.pop()
        history.push(newPage)
        return scr
    }

    fun display() {
        Minecraft.getMinecraft().displayGuiScreen(getScreen(history.pop())) // the page is pushed back on, so we have to pop it
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

    fun display(page: Page) {
        Minecraft.getMinecraft().displayGuiScreen(getScreen(page))
    }


}
