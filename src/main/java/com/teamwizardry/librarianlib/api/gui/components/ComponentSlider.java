package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentSlider extends GuiComponent<ComponentSlider> {
	
	boolean animatingIn = true;
	boolean animatingOut = true;
	
	public ComponentSlider(int posY) {
		super(0, posY);
		
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		float t = 1;// progress from 0-1 from not showing to all the way out.
		
		pos = new Vec2(-( -t*(t-2) ), pos.y);
	}
	
	

}
