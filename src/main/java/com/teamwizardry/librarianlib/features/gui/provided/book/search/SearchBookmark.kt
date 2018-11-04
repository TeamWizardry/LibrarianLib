package com.teamwizardry.librarianlib.features.gui.provided.book.search

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark
import com.teamwizardry.librarianlib.features.gui.provided.book.context.ComponentBookMark

/**
 * @author WireSegal
 * Created at 9:49 PM on 8/6/18.
 */
class SearchBookmark : Bookmark {

    var component: ComponentSearchBar? = null

    override fun createBookmarkComponent(book: IBookGui, bookmarkIndex: Int): ComponentBookMark {
        var c = component
        if (c == null || c.isInvalid) {
            c = ComponentSearchBar(book, bookmarkIndex, TFIDFSearch(book).textBoxConsumer(book) { SearchResults(book.book, it) })
            component = c
        } else
            c.parent = null //TODO this is a bad thing

        return c
    }

}
