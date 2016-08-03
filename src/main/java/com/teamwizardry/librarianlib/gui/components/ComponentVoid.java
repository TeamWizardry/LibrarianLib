package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ComponentVoid extends GuiComponent<ComponentVoid> {

	public ComponentVoid(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
	}
	
	public ComponentVoid(int posX, int posY) {
		super(posX, posY);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		// NOOP
	}

}
