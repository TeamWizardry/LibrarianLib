package com.teamwizardry.librarianlib.book;

import java.util.HashMap;
import java.util.Map;

import com.teamwizardry.librarianlib.book.gui.GuiBook;
import com.teamwizardry.librarianlib.book.gui.PageIndex;
import com.teamwizardry.librarianlib.book.gui.PageText;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.gui.book.PageDataManager;
import com.teamwizardry.librarianlib.common.network.data.DataNode;

public class PageHandler {
	public static final PageHandler INSTANCE = new PageHandler();
	protected static Map<String, IPageConstructor> map;
	protected static IPageConstructor error;
	
	private PageHandler() {
		map = new HashMap<>();
		//error = PageError::new;
		register("index", PageIndex::new);
		register("text", PageText::new);
		//register("subindex", PageSubindex::new);
        //register("structure", PageStructure::new);
	}

    public static void register(String name, IPageConstructor constructor) {
        map.putIfAbsent(name, constructor);
    }
	public static GuiBook create(Book book, Page page) {
		if ("/".equals(page.path)) {
			page = new Page("index", page.page);
        }

        DataNode data = PageDataManager.getPageData(book.modid, page.path);

        DataNode pagesList = data.get("pages");
        DataNode pageData = pagesList.get(page.page);

        String type = pageData.get("type").asStringOr("error");
        if (map.containsKey(type)) {
            if (pageData.isMap()) {
                if (pagesList.get(page.page + 1).exists()) {
                    pageData.put("hasNext", "true");
                }
                if (pagesList.get(page.page + -1).exists()) {
                    pageData.put("hasPrev", "true");
                }
            }
            return map.get(type).create(book, data, pageData, page);
        }

        DataNode errorGlobal = DataNode.map();
        errorGlobal.put("title", "<ERROR>");
        DataNode errorNode = DataNode.map();
        DataNode errorDataList = DataNode.list();

        errorNode.put("data", errorDataList);
        errorNode.put("type", "error");

        errorDataList.add("`" + page.path + "` #" + page.page);

        if (book.history.peek() != null) {
        	Page parentPage = book.history.peek();
            errorDataList.add("");
            errorDataList.add("parent page:");
            errorDataList.add("`" + parentPage.path + "` #" + parentPage.page);
        }

        String errorCode = "Unknown";

        if (!data.exists()) {
            errorCode = "Guide not found";
            errorNode.put("type", "404");
        }

        errorNode.put("errorCode", errorCode);

        return null;//error.create(parent, book, errorNode, errorGlobal, "", 0);
	}
	
	@FunctionalInterface
    public interface IPageConstructor {
        GuiBook create(Book book, DataNode rootNode, DataNode node, Page page);
    }
}
