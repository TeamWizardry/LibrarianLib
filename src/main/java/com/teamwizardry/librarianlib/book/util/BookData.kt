package com.teamwizardry.librarianlib.book.util

import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.gui.GuiBook
import com.teamwizardry.librarianlib.book.gui.PageText
import com.teamwizardry.librarianlib.book.gui.PageIndex
import com.teamwizardry.librarianlib.book.gui.PageSubindex
import com.teamwizardry.librarianlib.book.gui.PageStructure
import com.teamwizardry.librarianlib.data.DataNode
import com.teamwizardry.librarianlib.util.javainterfaces.SectionInitializer


object BookRegistry {
    private val entries = mutableMapOf<Book, MutableMap<String, BookEntry>>()
    private val map: MutableMap<String, SectionInitializer> = mutableMapOf()

    fun clearCache() {
        entries.clear()
    }

    fun getEntry(book: Book, path: String): BookEntry {
        if(!entries.containsKey(book))
            entries.put(book, mutableMapOf())
        if(entries[book]!!.contains(path))
            entries[book]!!.put(path, BookEntry(book, path))
        return entries[book]!![path]!!
    }

    fun getType(type: String) : SectionInitializer? {
        return map.get(type)
    }

    fun register(type: String, initializer: (section: BookSectionOther, node: DataNode, tag: String) -> GuiBook) {
        map.put(type, SectionInitializer(initializer))
    }

    fun register(type: String, initializer: SectionInitializer) {
        map.put(type, initializer)
    }

    init {
        register("index", ::PageIndex)
        register("subindex", ::PageSubindex)
        register("structure", ::PageStructure)
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
        var data = PageDataManager.getPageData(book.modid, path)
        if(!data.exists()) {
            title = "<<404: PAGE NOT FOUND>>"
            bookSections.add(BookSectionError("404: Page not found\n${book.modid}:$path", this, "section-${bookSections.size}"))
        } else {

            title = data["title"].asStringOr("<<NO TITLE>>")

            // parse text

            val pages = data["pages"]
            val text = data["text"]
            val len = text.asList().size

            var currentTags: MutableList<String> = mutableListOf("") // first page is tagged with ""
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
                    var section: BookSection = BookSectionText(currentNode, this, "section-${bookSections.size}")
                    tags.put(section.sectionTag, section)
                    currentTags.forEach { tags.put(it, section) }

                    if (bookSections.lastIndex >= 0) {
                        bookSections[bookSections.lastIndex].nextSection = section
                        section.prevSection = bookSections[bookSections.lastIndex]
                    }
                    bookSections.add(section)

                    currentNode = DataNode.list()
                    currentTags.clear()

                    val pageNode = pages[node["page"].asStringOr("<INVALID_PAGE>")]
                    section = BookSectionOther(pageNode, this, "section-${bookSections.size}")
                    tags.put(section.sectionTag, section)
                    pageNode["tags"].asList().forEach { tags.put(it.asStringOr("<INVALID_TAG>"), section) }

                    if (bookSections.lastIndex >= 0) {
                        bookSections[bookSections.lastIndex].nextSection = section
                        section.prevSection = bookSections[bookSections.lastIndex]
                    }
                    bookSections.add(section)
                }
            }

            if (currentNode.asList().size != 0) {
                var section: BookSection = BookSectionText(currentNode, this, "section-${bookSections.size}")
                tags.put(section.sectionTag, section)
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

    abstract fun create(tag: String) : GuiBook?

}

class BookSectionText(val node: DataNode, entry: BookEntry, sectionTag: String) : BookSection(entry, sectionTag) {

    override fun create(tag: String) : GuiBook? {
        return PageText(this, node, tag)
    }

}

class BookSectionOther(val node: DataNode, entry: BookEntry, sectionTag: String) : BookSection(entry, sectionTag) {
    override fun create(tag: String) : GuiBook? {
        var type = node.get("type").asStringOr("NoType")
        return BookRegistry.getType(type)?.invoke(this, node, tag)

    }
}

class BookSectionError(val error: String, entry: BookEntry, sectionTag: String) : BookSection(entry, sectionTag) {
    override fun create(tag: String) : GuiBook? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}