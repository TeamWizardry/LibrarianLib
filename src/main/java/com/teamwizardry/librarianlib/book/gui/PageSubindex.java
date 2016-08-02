package com.teamwizardry.librarianlib.book.gui;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.components.*;
import com.teamwizardry.librarianlib.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Link;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.data.DataNode;
import com.teamwizardry.librarianlib.data.DataNodeParsers;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
public class PageSubindex extends GuiBook {
	
	public PageSubindex(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		int itemsPerPage = 8;
		
		int indexPage = pageData.get("page").asInt();
		
		DataNode items = pageData.get("index").isString() ? rootData.get("subindexes").get(pageData.get("index").asString()) : rootData.get("subindex");
		
		int begin = indexPage * itemsPerPage;
		
		ComponentList list = new ComponentList(0, 0);
		
		for (int i = 0; i < itemsPerPage; i++) {
			if(items.get(begin+i).exists())
				addIndex(list, items.get(begin+i));
		}
		
		contents.add(list);
	}
	
	private void addIndex(GuiComponent<?> parent, DataNode node) {
		ComponentVoid comp = new ComponentVoid(0, 0, PAGE_WIDTH, 20);
		parent.add(comp);
		
		GuiComponent<?> icon = null;
		if(node.get("item").exists()) {
			ComponentSlot slot = new ComponentSlot(0, 0);
			ItemStack stack = DataNodeParsers.parseStack(node.get("item"));
			
			String amountText = "";
			if(stack == null || stack.getItem() == null) {
				stack = new ItemStack(Blocks.STONE);
				amountText = stack == null ? "~s~" : "~i~";
			}
			final String _amountText = amountText;
			slot.quantityText.add((c, text) -> _amountText);
			slot.stack.setValue(stack);
			
			slot.tooltip.setValue(false);
			icon = slot;
		}
		if(node.get("icon").exists()) {
			ComponentSprite sprite = new ComponentSprite(DataNodeParsers.parseSprite(node.get("icon")), 0, 0, 16, 16);
			icon = sprite;
		}
		
		if(icon != null) {
			icon.setPos(new Vec2d(0, 2));
			comp.add(icon);
		}
		
		if(node.get("tip").exists()) {
			comp.mouseIn.add((c, pos) -> {
				addTextSlider(comp, c.getPos().yi, node.get("tip").asStringOr("<NULL>"));
				return false;
			});
			comp.mouseOut.add((c, pos) -> {
				removeSlider(comp);
				return false;
			});
		}
		
		ComponentText text = new ComponentText(18, 6);
		text.text.setValue(node.get("text").asString());
		comp.add(text);
		new ButtonMixin(comp,
			() ->
				text.text.setValue(node.get("text").asString()),
			() ->
				text.text.setValue("§n" + node.get("text").asString()), () -> {},
			() -> {
				Link l = new Link(node.get("link").asStringOr("/"));
				openPageRelative(l.path, l.page);
				GuiComponent<?> parnt = text.getParent();
				parnt = null;
			}
		);
	}
}
