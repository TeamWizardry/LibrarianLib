package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.util.math.Vec2;
import com.teamwizardry.librarianlib.client.Sprite;

public class ComponentSprite extends GuiComponent<ComponentSprite> {

	protected Sprite sprite;
	
	public ComponentSprite(Sprite sprite, int x, int y) {
		this(sprite, x, y, sprite.getWidth(), sprite.getHeight());
	}
	
	public ComponentSprite(Sprite sprite, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.sprite = sprite;
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		sprite.draw(pos.xf, pos.yf, size.xi, size.yi);
	}
	
}
