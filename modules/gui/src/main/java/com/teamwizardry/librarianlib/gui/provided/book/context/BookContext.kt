package com.teamwizardry.librarianlib.gui.provided.book.context

import com.teamwizardry.librarianlib.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.gui.provided.book.hierarchy.IBookElement

/**
 * @author WireSegal
 * Created at 9:19 PM on 8/6/18.
 */
class BookContext(val book: IBookGui,
                  val pages: List<PaginationContext>,
                  val bookElement: IBookElement,
                  val bookmarks: List<Bookmark>,
                  val parent: BookContext? = null) {
    var position: Int = 0

    constructor(book: IBookGui, element: IBookElement, parent: BookContext? = book.context) :
            this(book, element.createComponents(book), element,
                    element.addAllBookmarks(parent?.bookmarks), parent)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass || other !is BookContext) return false

        if (book != other.book) return false
        if (pages != other.pages) return false
        if (bookElement != other.bookElement) return false
        if (bookmarks != other.bookmarks) return false
        if (parent != other.parent) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = book.hashCode()
        result = 31 * result + pages.hashCode()
        result = 31 * result + bookElement.hashCode()
        result = 31 * result + bookmarks.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + position
        return result
    }


}
