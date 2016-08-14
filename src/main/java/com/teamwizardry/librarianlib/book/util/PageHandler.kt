package com.teamwizardry.librarianlib.book.util

import java.util.HashMap

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.gui.*
import com.teamwizardry.librarianlib.data.DataNode

class PageHandler private constructor() {

    init {
        map = HashMap<String, IPageConstructor>()
        //error = PageError::new;
        register("index", IPageConstructor { book, rootData, pageData, page -> PageIndex(book, rootData, pageData, page) })
        register("text", IPageConstructor { book, rootData, pageData, page -> PageText(book, rootData, pageData, page) })
        register("subindex", IPageConstructor { book, rootData, pageData, page -> PageSubindex(book, rootData, pageData, page) })
        register("structure", IPageConstructor { book, rootData, pageData, page -> PageStructure(book, rootData, pageData, page) })
    }

    @FunctionalInterface
    interface IPageConstructor {
        fun create(book: Book, rootNode: DataNode, node: DataNode, page: Page): GuiBook
    }

    companion object {
        val INSTANCE = PageHandler()
        protected var map: MutableMap<String, IPageConstructor>
        protected var error: IPageConstructor? = null

        fun register(name: String, constructor: IPageConstructor) {
            (map as java.util.Map<String, IPageConstructor>).putIfAbsent(name, constructor)
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
                return map[type].create(book, data, pageData, page)
            } else {
                LibrarianLog.I.warn("Page type [%s] not found!", type)
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
