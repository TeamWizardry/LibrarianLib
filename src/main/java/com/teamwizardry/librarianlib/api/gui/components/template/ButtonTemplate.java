package com.teamwizardry.librarianlib.api.gui.components.template;

import com.teamwizardry.librarianlib.api.gui.GuiStyle;
import com.teamwizardry.librarianlib.api.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.api.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2;

public class ButtonTemplate extends ComponentTemplate<ComponentVoid> {
	
	protected GuiStyle style;
	protected ComponentSpriteTiled tiled;
	public ComponentVoid contents;
	
	public ButtonTemplate(GuiStyle style, int posX, int posY) {
		
		result = new ComponentVoid(posX, posY);
		contents = new ComponentVoid(style.BUTTON_BORDER, style.BUTTON_BORDER);
		
		tiled = new ComponentSpriteTiled(style.BUTTON, style.BUTTON_BORDER, 0, 0);
		tiled.setSize(new Vec2(0, 0));
		
		result.add(tiled);
		result.add(contents);
		
		tiled.preDraw.add((c, pos, ticks) -> {
			BoundingBox2D box = contents.getLogicalSize();
			tiled.setSize(new Vec2(( box.width() - 1 ) + ( 2 * style.BUTTON_BORDER ), ( box.height() - 1 ) + ( 2 * style.BUTTON_BORDER )));
		});
	}
	
}
