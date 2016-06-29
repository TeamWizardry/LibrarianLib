package com.teamwizardry.libarianlib.client.book;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class BookHandler {

	public static Map<String, GuidePage> openGuides = new HashMap<>();
	
	public static GuiScreen getScreen(GuiScreen parent, String mod, String path, int page) {
        GuiScreen scr = PageRegistry.construct(parent, mod, path, page);
        if (scr == null) {
            scr = PageRegistry.construct(parent, mod, "/", 0);
            openGuides.put(mod, new GuidePage("/", 0));
        } else {
            openGuides.put(mod, new GuidePage(path, page));
        }
        return scr;
	}
	
	public static void display(String mod, GuiScreen parent, String path, int page) {
    	Minecraft.getMinecraft().displayGuiScreen(getScreen(parent, mod, path, page));
	}
	
	public static void display(String mod) {
		GuiScreen scr = null;
		if(openGuides.containsKey(mod)) {
			scr = getScreen(null, mod, openGuides.get(mod).path, openGuides.get(mod).page);
		} else {
			scr = getScreen(null, mod, "/", 0);
		}
		Minecraft.getMinecraft().displayGuiScreen(scr);
	}
	
	public static class GuidePage {
		public String path;
		public int page;
		public GuidePage(String path, int page) {
			super();
			this.path = path;
			this.page = page;
		}
	}
}
