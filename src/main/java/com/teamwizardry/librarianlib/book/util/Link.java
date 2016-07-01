package com.teamwizardry.librarianlib.book.util;

public class Link {

	
	public final String path;
	public final int page;
	
	
	public Link(String str) {
		int i = str.lastIndexOf(":");
        int page = 0;

        if (i != -1) {
            try {
                page = Integer.parseInt(str.substring(i));
            } catch (NumberFormatException e) {
                // TODO: logging
            }
        }
        String path = str.substring(0, i == -1 ? str.length() : i);
        
        this.page = page;
        this.path = path;
	}
	
	public Link(String path, int page) {
		this.path = path;
		this.page = page;
	}
	
}
