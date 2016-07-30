package com.teamwizardry.librarianlib.api.gui.components.template;

import com.teamwizardry.librarianlib.api.gui.GuiStyle;
import com.teamwizardry.librarianlib.api.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.api.gui.components.ComponentVoid;

public class ButtonTemplate extends ComponentTemplate<ComponentVoid> {
	
	protected GuiStyle style;
	protected ComponentCenterAlign center;
	protected ComponentSpriteTiled tiled;
	
	public ButtonTemplate(GuiStyle style, int posX, int posY) {
		
		result = new ComponentVoid(posX, posY);
		
		center = new ComponentCenterAlign(0, 0, true, true);
		
		tiled = new ComponentSpriteTiled(style.getButtonSprite(), style.getButtonBorderSize(), 0, 0);
		
		result.add(center);
		result.add(tiled);
		
		
	}
	
}
