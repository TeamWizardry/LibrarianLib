package com.teamwizardry.librarianlib.book.gui;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.util.ResourceLocation;

import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Link;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.common.network.data.DataNode;

public class PageIndex extends GuiBook {

	public PageIndex(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		
		List<DataNode> icons = pageData.get("icons").asList();
		
		Color normalColor = Color.BLACK;
		Color hoverColor = Color.BLUE;
		
		int x = 0;
		int y = 0;
		int w = 32;
		int h = 32;
		int sep = 8;
		
		for (DataNode icon : icons) {
			contents.add(new ComponentSprite(new Sprite(new ResourceLocation(icon.get("icon").asStringOr("missingno"))), x, y, w, h).setup((i) -> {
				new ButtonMixin(i,
						() -> {
							i.color.setValue(normalColor);
						}, () -> {
							i.color.setValue(hoverColor);
						}, () -> {
							i.color.setValue(normalColor);
						},
						() -> {
							Link l = new Link(icon.get("link").asStringOr("/"));
							openPageRelative(l.path, l.page);
						}
				);
			}));
			x += w+sep;
			if(x > PAGE_WIDTH-w) {
				x = 0;
				y += h+sep;
			}
			
		}
		
	}

}
