package com.teamwizardry.librarianlib.book.util

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.gui.*
import com.teamwizardry.librarianlib.data.DataNode

class PageHandler private constructor() {

    init {
        //error = PageError::new;
        register("index", ::PageIndex)
        register("text", ::PageText)
        register("subindex", ::PageSubindex)
        register("structure", ::PageStructure)
    }

    @FunctionalInterface
    interface IPageConstructor {
        fun create(book: Book, rootNode: DataNode, node: DataNode, page: Page): GuiBook
    }

    companion object {
        val INSTANCE = PageHandler()
        protected val map: MutableMap<String, (Book, rootNode: DataNode, node: DataNode, Page) -> GuiBook> = mutableMapOf()
        protected var error: IPageConstructor? = null

        fun register(name: String, constructor: (Book, rootNode: DataNode, node: DataNode, Page) -> GuiBook) {
            map.getOrPut(name) { constructor }
        }

        fun create(book: Book, page: Page): GuiBook? {
            var page = page
            if ("/" == page.path) {
                page = Page("index", page.page)
            }

            val data = PageDataManager.getPageData(book.modid, page.path)

            val pagesList = data.get("pages")
            val pageData = pagesList.get(page.page)

            val type = pageData.get("type").asStringOr("error")
            if (map.containsKey(type)) {
                if (pageData.isMap) {
                    if (pagesList.get(page.page + 1).exists()) {
                        pageData.put("hasNext", "true")
                    }
                    if (pagesList.get(page.page + -1).exists()) {
                        pageData.put("hasPrev", "true")
                    }
                }
                return map[type]?.invoke(book, data, pageData, page)
            } else {
                LibrarianLog.warn("Page type [%s] not found!", type)
            }

            val errorGlobal = DataNode.map()
            errorGlobal.put("title", "<ERROR>")
            val errorNode = DataNode.map()
            val errorDataList = DataNode.list()

            errorNode.put("data", errorDataList)
            errorNode.put("type", "error")

            errorDataList.add("`" + page.path + "` #" + page.page)

            if (book.history.peek() != null) {
                val parentPage = book.history.peek()
                errorDataList.add("")
                errorDataList.add("parent page:")
                errorDataList.add("`" + parentPage.path + "` #" + parentPage.page)
            }

            var errorCode = "Unknown"

            if (!data.exists()) {
                errorCode = "Guide not found"
                errorNode.put("type", "404")
            }

            errorNode.put("errorCode", errorCode)

            return null//error.create(parent, book, errorNode, errorGlobal, "", 0);
        }
    }
}
