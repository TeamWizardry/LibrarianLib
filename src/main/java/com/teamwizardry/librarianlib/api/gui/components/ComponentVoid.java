package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentVoid extends GuiComponent<ComponentVoid> {

	public ComponentVoid(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
	}
	
	public ComponentVoid(int posX, int posY) {
		super(posX, posY);
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		// NOOP
	}

}
