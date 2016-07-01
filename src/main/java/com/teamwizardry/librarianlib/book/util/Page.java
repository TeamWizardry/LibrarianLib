package com.teamwizardry.librarianlib.book.util;

import com.teamwizardry.librarianlib.book.gui.GuiBook;

public class Page {

	public final String path;
	public final int page;
	public GuiBook gui;
	
	public Page(String path, int page) {
		this.path = path;
		this.page = page;
	}
	
	public Page pageNum(int page) {
		return new Page(path, page);
	}
}
