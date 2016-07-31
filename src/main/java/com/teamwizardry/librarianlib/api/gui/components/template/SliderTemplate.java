package com.teamwizardry.librarianlib.api.gui.components.template;

import com.teamwizardry.librarianlib.api.gui.components.ComponentSliderTray;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.api.gui.components.ComponentText;
import com.teamwizardry.librarianlib.book.gui.GuiBook;
import net.minecraft.item.ItemStack;

public class SliderTemplate {

	public static ComponentSliderTray text(int posY, String text) {
		ComponentSliderTray slider = new ComponentSliderTray(0, posY, -120, 0);
		ComponentText textComp = new ComponentText(7, 6).setup((comp)-> {
			comp.text.setValue(text);
			comp.wrap.setValue(113);
			comp.enableFontFlags();
		});
		
		slider.add(new ComponentSpriteTiled(GuiBook.SLIDER_NORMAL, 6, 0, 0, 133, 10 + textComp.getLogicalSize().heightI()));
		slider.add(textComp);
		
		return slider;
	}
	
	public static ComponentSliderTray recipe(int posY, ItemStack[][] recipe) {
		ComponentSliderTray slider = new ComponentSliderTray(0, posY, -120, 0);
		slider.add(new ComponentSprite(GuiBook.SLIDER_RECIPE, 0, 0));
		
		return slider;
	}
	
}
