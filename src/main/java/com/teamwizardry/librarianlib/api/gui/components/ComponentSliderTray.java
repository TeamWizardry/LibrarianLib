package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentSliderTray extends GuiComponent<ComponentSliderTray> {
	
	boolean animatingIn = true;
	boolean animatingOut = false;
	int tickStart;
	
	int lifetime = 5;
	int offsetX, offsetY;
	Vec2 rootPos;
	
	public ComponentSliderTray(int posX, int posY, int offsetX, int offsetY) {
		super(posX, posY);
		setCalculateOwnHover(false);
		tickStart = GuiTickHandler.ticks;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		rootPos = pos;
	}

	public int getLifetime() {
		return lifetime;
	}
	
	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}
	
	public void close() {
		tickStart = GuiTickHandler.ticks;
		animatingIn = false;
		animatingOut = true;
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		float t = (float)(GuiTickHandler.ticks - tickStart)/(float)getLifetime();
		if(t > 1) {
			t = 1;
			if(animatingIn)
				animatingIn = false;
			if(animatingOut)
				invalidate();
		}
		if(animatingOut)
			t = 1-t;
		
		float xOffset = ( -t*(t-2) ) * offsetX;
		float yOffset = ( -t*(t-2) ) * offsetY;
		pos = new Vec2(rootPos.x+xOffset, rootPos.y+yOffset);
	}
	
	

}
