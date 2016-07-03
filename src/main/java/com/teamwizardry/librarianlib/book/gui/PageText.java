package com.teamwizardry.librarianlib.book.gui;

import java.util.List;

import com.teamwizardry.librarianlib.api.gui.components.ComponentText;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.gui.book.bookcomponents.TextControl;
import com.teamwizardry.librarianlib.common.network.data.DataNode;

public class PageText extends GuiBook {

	String text;
	
	public PageText(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		int pageNum;
		List<DataNode> list = null;
        if (pageData.get("text").isList()) {
            list = pageData.get("text").asList();
        } else {
            DataNode texts = rootData.get("texts");
            list = texts.get(pageData.get("text").get("global").asString()).asList();
            pageNum = pageData.get("text").get("page").asInt();
        }
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            DataNode node = list.get(i);
            if (node.isString()) {
                if (i != 0 && list.get(i - 1).isString()) {
                    str += "\n\n"; // if it's two strings then it should have a paragraph break
                }
                String addStr = node.asString();
                addStr = addStr.replaceAll("(?<!\\\\)&([0-9a-fA-Fk-oK-OrR])", "§$1");
                str += addStr;
            }
            if (node.isMap()) {
//                TextControl c = new TextControl(node);
//                controls.add(c);
//                c.text = c.text.replaceAll("(?<!\\\\)&([0-9a-fA-Fk-oK-OrR])", "§$1");

//                c.start = str.replaceAll("§.", "").replaceAll("\n", "").length();
//                c.end = c.text.replaceAll("§.", "").replaceAll("\n", "").length();

                str += node.get("text").asString();
            }
        }

        text = str;
        
        contents.add(new ComponentText(0, 0).setup((c) -> {
        	c.text.setValue(text);
        	c.wrap.setValue(GuiBook.PAGE_WIDTH);
        }));
	}

}
