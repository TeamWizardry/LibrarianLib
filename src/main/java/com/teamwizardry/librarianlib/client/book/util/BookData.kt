package com.teamwizardry.librarianlib.client.book.util

import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.client.book.data.DataNode
import com.teamwizardry.librarianlib.client.book.gui.*


object BookRegistry {
    val entries = mutableMapOf<Book, MutableMap<String, BookEntry>>()
    val map: MutableMap<String, (BookSectionOther, DataNode, String) -> GuiBook> = mutableMapOf()

    init {
        register("index", ::PageIndex)
        register("subindex", ::PageSubindex)
        register("structure", ::PageStructure)
    }

    fun clearCache() {
        entries.clear()
    }

    fun getEntry(book: Book, path: String): BookEntry {
        return entries.getOrPut(book, { mutableMapOf() }).getOrPut(path, { BookEntry(book, path) })
    }

    fun getType(type: String): ((BookSectionOther, DataNode, String) -> GuiBook)? {
        return map[type]
    }

    fun register(type: String, initializer: (BookSectionOther, DataNode, String) -> GuiBook) {
        map[type] = initializer
    }
}

class BookEntry(val book: Book, val path: String) {
    val title: String
    val bookSections: MutableList<BookSection> = mutableListOf()
    val tags: MutableMap<String, BookSection> = mutableMapOf()

    fun getTagged(tag: String): BookSection {
        return tags[tag] ?: bookSections.first()
    }

    init {
        val data = PageDataManager.getPageData(book.modid, path)
        if (!data.exists()) {
            title = "<<404: PAGE NOT FOUND>>"
            bookSections.add(BookSectionError("404: Page not found\n${book.modid}:$path", this, "section-${bookSections.size}"))
        } else {

            title = data["title"].asStringOr("<<NO TITLE>>")

            // parse text

            val pages = data["pages"]
            val text = data["text"]
            val len = text.asList().size

            val currentTags: MutableList<String> = mutableListOf("") // first page is tagged with ""
            var currentNode: DataNode = DataNode.list()

            for (i in 0..len - 1) {
                val node = text[i]

                if (node.isString) {
                    currentNode.add(node)
                } else if (node.isList) {
                    if (node.asList().size == 1) // tag
                        currentTags.add(node[0].asStringOr("<INVALID_TAG>"))
                    else {
                        currentNode.add(node)
                    }
                } else if (node.isMap) {
                    if (currentNode.asList().size > 0) {
                        val section: BookSection = BookSectionText(currentNode, this, "section-${bookSections.size}")
                        tags.put(section.sectionTag, section)
                        tags.put(section.sectionTagEnd, section)
                        currentTags.forEach { tags.put(it, section) }

                        if (bookSections.lastIndex >= 0) {
                            bookSections[bookSections.lastIndex].nextSection = section
                            section.prevSection = bookSections[bookSections.lastIndex]
                        }
                        bookSections.add(section)

                        currentNode = DataNode.list()
                    }
                    currentTags.clear()

                    val pageNode = pages[node["page"].asStringOr("<INVALID_PAGE>")]
                    val section = BookSectionOther(pageNode, this, "section-${bookSections.size}")
                    tags.put(section.sectionTag, section)
                    tags.put(section.sectionTagEnd, section)
                    pageNode["tags"].asList().forEach { tags.put(it.asStringOr("<INVALID_TAG>"), section) }

                    if (bookSections.lastIndex >= 0) {
                        bookSections[bookSections.lastIndex].nextSection = section
                        section.prevSection = bookSections[bookSections.lastIndex]
                    }
                    bookSections.add(section)
                }
            }

            if (currentNode.asList().size != 0) {
                val section: BookSection = BookSectionText(currentNode, this, "section-${bookSections.size}")
                tags.put(section.sectionTag, section)
                tags.put(section.sectionTagEnd, section)
                currentTags.forEach { tags.put(it, section) }

                if (bookSections.lastIndex >= 0) {
                    bookSections[bookSections.lastIndex].nextSection = section
                    section.prevSection = bookSections[bookSections.lastIndex]
                }
                bookSections.add(section)
            }
        }
    }

}

abstract class BookSection(val entry: BookEntry, val sectionTag: String) {

    var nextSection: BookSection? = null
    var prevSection: BookSection? = null
    val sectionTagEnd = sectionTag + "-end"
    abstract fun create(tag: String): GuiBook?

}

class BookSectionText(val node: DataNode, entry: BookEntry, sectionTag: String) : BookSection(entry, sectionTag) {

    override fun create(tag: String): GuiBook? {
        val p = PageText(this, node, tag)
        if (tag == this.sectionTagEnd)
            p.jumpToPage(p.maxpPageJump())
        return p
    }

}

class BookSectionOther(val node: DataNode, entry: BookEntry, sectionTag: String) : BookSection(entry, sectionTag) {
    override fun create(tag: String): GuiBook? {
        val type = node["type"].asStringOr("NoType")
        val p = BookRegistry.getType(type)?.invoke(this, node, tag) ?: return null
        if (tag == this.sectionTagEnd)
            p.jumpToPage(p.maxpPageJump())
        return p
    }
}

class BookSectionError(val error: String, entry: BookEntry, sectionTag: String) : BookSection(entry, sectionTag) {
    override fun create(tag: String): GuiBook? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
