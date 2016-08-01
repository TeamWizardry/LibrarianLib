package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2;

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
public class ComponentList extends GuiComponent<ComponentList> {
	
	public ComponentList(int posX, int posY) {
		super(posX, posY);
	}
	
	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		int y = 0;
		
		for (GuiComponent<?> component : components) {
			component.setPos(new Vec2(component.getPos().x, y));
			BoundingBox2D bb = component.getLogicalSize();
			y = bb.max.yi;
		}
	}
}
