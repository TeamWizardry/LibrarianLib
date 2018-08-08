package com.teamwizardry.librarianlib.features.gui.provided.book.context

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.sprite.Sprite

/**
 * @author WireSegal
 * Created at 9:49 PM on 8/6/18.
 */

interface Bookmark {
    fun createBookmarkComponent(book: IBookGui, bookmarkIndex: Int): ComponentBookMark
}

open class SimpleBookmark(val smallIcon: Sprite) : Bookmark {

    override fun createBookmarkComponent(book: IBookGui, bookmarkIndex: Int): ComponentBookMark =
            ComponentBookMark(book, smallIcon, bookmarkIndex)
}
