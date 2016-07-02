package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.HandlerList;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentButton extends GuiComponent<ComponentButton> {

	/**
	 * The click handlers
	 */
	public final HandlerList<IClickHandler> click = new HandlerList<>();
	public final Option<ComponentButton, Boolean> enabled = new Option<>(true);
	Sprite sprite;
	Sprite hover;
	Sprite disabled;
	boolean mouseDownInside = false;
	
	public ComponentButton(int posX, int posY, Sprite sprite) {
		this(posX, posY, sprite.getWidth(), sprite.getHeight(), sprite);
	}
	
	public ComponentButton(int posX, int posY, int width, int height, Sprite sprite) {
		super(posX, posY, width, height);
		this.sprite = sprite;
	}
	
	public void setHover(Sprite hover) {
		this.hover = hover;
	}
	
	public void setDisabled(Sprite disabled) {
		this.disabled = disabled;
	}
	
	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {

		if(!enabled.getValue(this) && disabled != null) {
			disabled.getTex().bind();
			disabled.draw(pos.xf, pos.yf, size.xi, size.yi);
		} else if(isMouseOver(mousePos) && hover != null) {
			hover.getTex().bind();
			hover.draw(pos.xf, pos.yf, size.xi, size.yi);
		} else {
			sprite.getTex().bind();
			sprite.draw(pos.xf, pos.yf, size.xi, size.yi);
		}
	}

	@Override
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {
		super.mouseDown(mousePos, button);
		if(isMouseOver(mousePos)) {
			mouseDownInside = true;
		}
	}

	@Override
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {
		super.mouseUp(mousePos, button);
		if(mouseDownInside && isMouseOver(mousePos)) {
			click.fire((h) -> h.click());
		}
		mouseDownInside = false;
	}

	@FunctionalInterface
	public interface IClickHandler {
		void click();
	}
	
}
