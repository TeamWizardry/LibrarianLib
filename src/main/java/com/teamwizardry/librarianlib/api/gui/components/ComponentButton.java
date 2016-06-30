package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.HandlerList;
import com.teamwizardry.librarianlib.api.util.math.Vec2;
import com.teamwizardry.librarianlib.client.Sprite;

public class ComponentButton extends GuiComponent<ComponentButton> {

	Sprite sprite;
	boolean mouseDownInside = false;
	
	public final HandlerList<IClickHandler> handlers = new HandlerList<>();
	@FunctionalInterface public static interface IClickHandler { public void click(); }

	public ComponentButton(int posX, int posY, Sprite sprite) {
		this(posX, posY, sprite.getWidth(), sprite.getHeight(), sprite);
	}
	
	public ComponentButton(int posX, int posY, int width, int height, Sprite sprite) {
		super(posX, posY, width, height);
		this.sprite = sprite;
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		sprite.getTex().bind();
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
			handlers.fire((h) -> h.click());
		}
		mouseDownInside = false;
	}
	
}
