package com.teamwizardry.librarianlib.book.util

import com.teamwizardry.librarianlib.book.gui.GuiBook

class Page(val path: String, val page: Int) {
    var gui: GuiBook? = null

    fun pageNum(page: Int): Page {
        return Page(path, page)
    }
}
