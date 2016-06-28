package com.teamwizardry.libarianlib.client.book;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;

import com.teamwizardry.libarianlib.client.book.gui.MainIndex;
import com.teamwizardry.libarianlib.client.book.pages.GuiPageCommon;
import com.teamwizardry.libarianlib.client.book.pages.GuiPageError;
import com.teamwizardry.libarianlib.client.book.pages.GuiPageStructure;
import com.teamwizardry.libarianlib.client.book.pages.GuiPageSubindex;
import com.teamwizardry.libarianlib.client.book.pages.GuiPageText;
import com.teamwizardry.libarianlib.common.data.DataNode;

/**
 * Stores the different types of pages and constructs pages based on their path
 *
 * @author Pierce Corcoran
 */
public class PageRegistry {

    public static final PageRegistry INSTANCE = new PageRegistry();
    private static Map<String, IPageGuiSupplier> map;
    private static IPageGuiSupplier error;

    private PageRegistry() {
        map = new HashMap<>();
        error = GuiPageError::new;
        register("text", GuiPageText::new);
        register("subindex", GuiPageSubindex::new);
        register("structure", GuiPageStructure::new);
    }

    public static void register(String name, IPageGuiSupplier supplier) {
        map.putIfAbsent(name, supplier);
    }

    public static GuiScreen construct(GuiScreen parent, String mod, String path, int pageNum) {

        if ("/".equals(path)) {
            return new MainIndex(mod);
        }

        DataNode data = PageDataManager.getPageData(mod, path);

        DataNode pagesList = data.get("pages");
        DataNode pageData = pagesList.get(pageNum);

        String type = pageData.get("type").asStringOr("error");
        if (map.containsKey(type)) {
            if (pageData.isMap()) {
                if (pagesList.get(pageNum + 1).exists()) {
                    pageData.put("hasNext", "true");
                }
                if (pagesList.get(pageNum + -1).exists()) {
                    pageData.put("hasPrev", "true");
                }
            }
            return map.get(type).create(parent, pageData, data, mod, path, pageNum);
        }

        DataNode errorGlobal = DataNode.map();
        errorGlobal.put("title", "<ERROR>");
        DataNode errorNode = DataNode.map();
        DataNode errorDataList = DataNode.list();

        errorNode.put("data", errorDataList);
        errorNode.put("type", "error");

        errorDataList.add("`" + path + "` #" + pageNum);

        if (parent instanceof GuiPageCommon) {
            GuiPageCommon parentPage = (GuiPageCommon) parent;
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

        return error.create(parent, errorNode, errorGlobal, mod, "", 0);
    }

    @FunctionalInterface
    public interface IPageGuiSupplier {
        GuiScreen create(GuiScreen parent, DataNode node, DataNode globalNode, String mod, String path, int page);
    }
}
