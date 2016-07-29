package com.teamwizardry.librarianlib.book.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.teamwizardry.librarianlib.api.gui.components.ComponentMarkup;
import com.teamwizardry.librarianlib.api.gui.components.ComponentMarkup.MarkupElement;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.common.network.data.DataNode;

public class PageText extends GuiBook {
	
	int pageNum = -1;
	
	public PageText(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		
		ComponentMarkup markup = new ComponentMarkup(0, 0, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT);
		contents.add(markup);
		markup.preDraw.add((c, pos, ticks) -> fr.setUnicodeFlag(true));
		fr.setUnicodeFlag(true);
		
		List<DataNode> list = null;
        if (pageData.get("text").isList()) {
            list = pageData.get("text").asList();
        } else {
            DataNode texts = rootData.get("texts");
            list = texts.get(pageData.get("text").get("global").asString()).asList();
            pageNum = pageData.get("text").get("page").asInt();
        }
        
        String formats = "";
        
        for (int i = 0; i < list.size(); i++) {
            DataNode node = list.get(i);
            String str = null;
            
            if (node.isString()) {
            	str = node.asString();
            	if (i != 0 && list.get(i-1).isString()) {
                    str = "\n\n" + str; // if it's two strings then it should have a paragraph break
                }
            }
            if (node.isMap()) {
                str = node.get("text").asString();
            }
            
            if(str != null) {
                str = str.replace("\n", "§r§0\n");
            	MarkupElement elem = markup.create(str);
            	elem.format.setValue(formats);
            	
            	if (node.isString()) {
                    // do nothing
                } else if (node.isMap()) {
                	String type = node.get("type").asStringOr("<err>").toLowerCase();
                	if(type.equals("link")) {
                		Color hoverColor = Color.argb(0xff0000EE),
                      		  normalColor = Color.argb(0xff0F00B0);
                		elem.format.func((hover) -> hover ? "§n" : "");
                      	elem.color.func((hover) -> hover ? hoverColor : normalColor);
                      	
                      	String ref = node.get("ref").asStringOr("/error");
                        int colon = ref.lastIndexOf(":");
                        int linkPage = 0;

                        if (colon != -1) {
                            try {
                                linkPage = Integer.parseInt(ref.substring(colon));
                            } catch (NumberFormatException e) {
                                // TODO: logging
                            }
                        }
                        
                        String path = ref.substring(0, colon == -1 ? ref.length() : colon);
                        final int _page = linkPage;
                        elem.click.add(() -> openPageRelative(path, _page));
                	}
                }
            	formats = FontRenderer.getFormatFromString(formats + str);
            }
        }
        
        fr.setUnicodeFlag(false);
		markup.postDraw.add((c, pos, ticks) -> fr.setUnicodeFlag(false));
		
		if(pageNum != -1) {
			// the / font_height ) * font_height is to round it to the nearest font_height multiple (int division)
			markup.start.setValue((( pageNum*PAGE_HEIGHT+1 )/fr.FONT_HEIGHT)*fr.FONT_HEIGHT);
			markup.end.setValue((((pageNum+1)*PAGE_HEIGHT+1 )/fr.FONT_HEIGHT)*fr.FONT_HEIGHT);
		}
	}

}
