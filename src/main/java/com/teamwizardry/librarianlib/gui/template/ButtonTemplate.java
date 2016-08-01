package com.teamwizardry.librarianlib.gui.template;

import com.teamwizardry.librarianlib.gui.GuiStyle;
import com.teamwizardry.librarianlib.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ButtonTemplate extends ComponentTemplate<ComponentVoid> {
	
	protected GuiStyle style;
	protected ComponentSpriteTiled tiled;
	public ComponentVoid contents;
	
	public ButtonTemplate(GuiStyle style, int posX, int posY) {
		
		result = new ComponentVoid(posX, posY);
		contents = new ComponentVoid(style.BUTTON_BORDER, style.BUTTON_BORDER);
		
		tiled = new ComponentSpriteTiled(style.BUTTON, style.BUTTON_BORDER, 0, 0);
		tiled.setSize(new Vec2d(0, 0));
		
		result.add(tiled);
		result.add(contents);
		
		tiled.preDraw.add((c, pos, ticks) -> {
			BoundingBox2D box = contents.getLogicalSize();
			tiled.setSize(new Vec2d(( box.width() - 1 ) + ( 2 * style.BUTTON_BORDER ), ( box.height() - 1 ) + ( 2 * style.BUTTON_BORDER )));
		});
	}
	
}
