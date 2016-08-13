package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.client.gui.Gui;

/**
 * Created by TheCodeWarrior
 */
public class ComponentRect extends GuiComponent<ComponentRect> {
	
	public final Option<ComponentRect, Color> color = new Option<>(Color.WHITE);
	
	public ComponentRect(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
	}
	
	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		Gui.drawRect(pos.xi, pos.yi, pos.xi + size.xi, pos.yi + size.yi, color.getValue(this).hexARGB());
	}
}
