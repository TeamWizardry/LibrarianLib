package com.teamwizardry.librarianlib.book.gui;

import com.teamwizardry.librarianlib.api.gui.components.ComponentGrid;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSliderTray;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.api.gui.components.template.SliderTemplate;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Link;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.common.network.data.DataNode;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PageIndex extends GuiBook {

    public PageIndex(Book book, DataNode rootData, DataNode pageData, Page page) {
        super(book, rootData, pageData, page);

        List<DataNode> icons = pageData.get("icons").asList();

        Color normalColor = Color.BLACK;
        Color hoverColor = Color.rgb(0x00BFFF);
        // TODO: pressed color not working
        Color pressColor = Color.rgb(0x191970);
	
	    int size = 32;
	    int sep = (PAGE_WIDTH-size*3)/2;
	    ComponentGrid grid = new ComponentGrid(0, 0, size+sep, size+sep, 3);
        
        for (DataNode icon : icons) {

        	ComponentSprite sprite = new ComponentSprite(new Sprite(new ResourceLocation(icon.get("icon").asStringOr("missingno"))), 0, 0, size, size);
	
	        MutableObject<ComponentSliderTray> s = new MutableObject<>(null);
	        new ButtonMixin(sprite,
		        () -> sprite.color.setValue(normalColor), () -> sprite.color.setValue(hoverColor), () -> sprite.color.setValue(pressColor),
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
