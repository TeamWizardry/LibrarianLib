package com.teamwizardry.libarianlib.client.gui.components;

import com.teamwizardry.libarianlib.client.DrawingUtil;
import com.teamwizardry.libarianlib.client.TextureDefinition;
import com.teamwizardry.libarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.libarianlib.client.gui.GuiComponent;
import com.teamwizardry.libarianlib.client.gui.events.ButtonClickEvent;
import com.teamwizardry.libarianlib.math.Vec2;

public class GuiComponentButton extends GuiComponent {

	public String id;
	TextureDefinition def;
	boolean mouseDownInside = false;
	
	public GuiComponentButton(String id, int posX, int posY, int width, int height, TextureDefinition def) {
		super(posX, posY, width, height);
		this.def = def;
		this.id = id;
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		def.bind();
		DrawingUtil.drawRect(pos.xi, pos.yi, size.xi, size.yi, def);
	}

	@Override
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {
		if(isMouseOver(mousePos)) {
			mouseDownInside = true;
		}
	}
	
	@Override
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {
		if(mouseDownInside && isMouseOver(mousePos)) {
			postEvent(new ButtonClickEvent(this));
		}
		mouseDownInside = false;
	}
	
}
