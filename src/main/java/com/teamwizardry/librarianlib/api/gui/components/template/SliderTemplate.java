package com.teamwizardry.librarianlib.api.gui.components.template;

import net.minecraft.item.ItemStack;

import com.teamwizardry.librarianlib.api.gui.components.ComponentSlider;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.api.gui.components.ComponentText;
import com.teamwizardry.librarianlib.book.gui.GuiBook;

public class SliderTemplate {

	public static ComponentSlider text(int posY, String text) {
		ComponentSlider slider = new ComponentSlider(0, posY, -120, 0);
		ComponentText textComp = new ComponentText(7, 6).setup((comp)-> {
			comp.text.setValue(text);
			comp.wrap.setValue(113);
		});
		
		slider.add(new ComponentSpriteTiled(GuiBook.SLIDER_NORMAL, 6, 0, 0, 133, 10 + textComp.getLogicalSize().heightI()));
		slider.add(textComp);
		
		return slider;
	}
	
	public static ComponentSlider recipe(int posY, ItemStack[][] recipe) {
		ComponentSlider slider = new ComponentSlider(0, posY, -120, 0);
		slider.add(new ComponentSprite(GuiBook.SLIDER_RECIPE, 0, 0));
		
		
		return slider;
	}
	
}
