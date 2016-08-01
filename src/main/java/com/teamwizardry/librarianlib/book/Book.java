package com.teamwizardry.librarianlib.book;

import com.teamwizardry.librarianlib.api.LibrarianLog;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.gui.GuiBook;
import com.teamwizardry.librarianlib.book.util.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Stack;

/**
 * The class that defines a book and has helpers to open it. It defines:
 * <ul>
 * <li>What mod the book is from </li>
 * <li>What the color of the book is</li>
 * <li>Whether the player has unlocked a page - implementation TODO</li>
 * </ul>
 * @author Pierce Corcoran
 *
 */
public class Book {

	public final String modid;
	/**
	 * The history of the pages used to get to the current page. The top element is the current page.
	 */
	public final Stack<Page> history = new Stack<>();
	
	public Book(String modid) {
		this.modid = modid;
		history.push(new Page("/", 0));
	}

	protected boolean canOpenPage(String path) {
		return true;
	}

	protected GuiScreen getScreen(Page page) {
		GuiBook scr = PageHandler.create(this, page);
		if (scr == null) {
			LibrarianLog.I.warn("Page [%s:%d] not found! Going to [/:0] ", page.path, page.page);
			page = new Page("/", 0);
			scr = PageHandler.create(this, page);
		}
		page.gui = scr;
		if(history.empty())
			history.push(new Page("/", 0));
		if(history.peek().path.equals(page.path))
			history.pop();
		history.push(page);
		return scr;
	}
	
	public void display() {
		Minecraft.getMinecraft().displayGuiScreen(getScreen(history.pop())); // the page is pushed back on, so we have to pop it
	}
	
	public void back() {
		if(history.empty())
			return;
		history.pop();
		if(history.peek().gui != null)
			Minecraft.getMinecraft().displayGuiScreen(history.peek().gui); // if a capability was saved, display that
		else
			Minecraft.getMinecraft().displayGuiScreen(getScreen(history.pop())); // the page is pushed back on, so we have to pop it
	}
	
	public void display(Page page) {
		Minecraft.getMinecraft().displayGuiScreen(getScreen(page));
	}
	
	
}
