package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentCenterAlign extends GuiComponent<ComponentCenterAlign> {

	public boolean centerHorizontal, centerVertical;
	
	public ComponentCenterAlign(int posX, int posY, boolean horizontal, boolean vertical) {
		super(posX, posY);
		centerHorizontal = horizontal;
		centerVertical = vertical;
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		// noop
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		if(centerHorizontal || centerVertical) {
			for (GuiComponent<?> component : components) {
				Vec2 compPos = component.getPos();
				BoundingBox2D bb = component.getLogicalSize();
				Vec2 posOffsetFromBB = compPos.sub(bb.min);
				Vec2 centerPos = bb.max.sub(bb.min).mul(1f/2f).sub(posOffsetFromBB);
				Vec2 adjustedPos = centerPos.mul(-1);
				if(!centerHorizontal)
					adjustedPos = adjustedPos.setX(compPos.x);
				if(!centerVertical)
					adjustedPos = adjustedPos.setY(compPos.y);
				component.setPos(adjustedPos);
			}
		}
		
		super.draw(mousePos, partialTicks);
	}

}
