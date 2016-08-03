package com.teamwizardry.librarianlib.book.gui;

import com.teamwizardry.librarianlib.data.DataNodeParsers;
import com.teamwizardry.librarianlib.gui.components.ComponentGrid;
import com.teamwizardry.librarianlib.gui.components.ComponentSliderTray;
import com.teamwizardry.librarianlib.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Link;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.sprite.Sprite;
import com.teamwizardry.librarianlib.data.DataNode;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;

public class PageIndex extends GuiBook {

    public PageIndex(Book book, DataNode rootData, DataNode pageData, Page page) {
        super(book, rootData, pageData, page);

        List<DataNode> icons = pageData.get("icons").asList();

        Color normalColor = Color.rgb(Integer.parseInt(pageData.get("normalColor").asStringOr("0"), 16));
        Color hoverColor = Color.rgb(Integer.parseInt(pageData.get("hoverColor").asStringOr("00BFFF"), 16));
        Color pressColor = Color.rgb(0x191970);
	
	    int size = 32;
	    int sep = (PAGE_WIDTH-size*3)/2;
	    ComponentGrid grid = new ComponentGrid(0, 0, size+sep, size+sep, 3);
        
        for (DataNode icon : icons) {
	
	        Color iconNormalColor = icon.get("normalColor").exists() ? Color.rgb(Integer.parseInt(icon.get("normalColor").asString(), 16)) : normalColor;
	        Color iconHoverColor = icon.get("hoverColor").exists() ? Color.rgb(Integer.parseInt(icon.get("hoverColor").asString(), 16)) : hoverColor;
	        
	        
        	ComponentSprite sprite = new ComponentSprite(DataNodeParsers.parseSprite(icon.get("icon")), 0, 0, size, size);
	
	        MutableObject<ComponentSliderTray> s = new MutableObject<>(null);
	        new ButtonMixin(sprite,
		        () -> sprite.color.setValue(iconNormalColor), () -> sprite.color.setValue(iconHoverColor), () -> sprite.color.setValue(pressColor),
		        () -> {
			        Link l = new Link(icon.get("link").asStringOr("/"));
			        openPageRelative(l.path, l.page);
		        }
	        );
	        sprite.mouseIn.add((c, pos) -> {
	        	addTextSlider(sprite, c.getPos().yi, icon.get("text").asStringOr("<NULL>"));
		        return false;
	        });
	        sprite.mouseOut.add((c, pos) -> {
		        removeSlider(sprite);
		        return false;
	        });
	        
            grid.add(sprite);
        }
        
        contents.add(grid);
    }
}
