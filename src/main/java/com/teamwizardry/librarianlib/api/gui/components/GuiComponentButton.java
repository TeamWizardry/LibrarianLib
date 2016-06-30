package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.event.ButtonClickEvent;
import com.teamwizardry.librarianlib.api.util.math.Vec2;
import com.teamwizardry.librarianlib.client.DrawingUtil;
import com.teamwizardry.librarianlib.client.Sprite;

public class GuiComponentButton extends GuiComponent {

	public String id;
	Sprite sprite;
	boolean mouseDownInside = false;
	
	public GuiComponentButton(String id, int posX, int posY, int width, int height, Sprite sprite) {
		super(posX, posY, width, height);
		this.sprite = sprite;
		this.id = id;
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		sprite.tex.bind();
		sprite.draw(pos.xf, pos.yf, size.xi, size.yi);
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
