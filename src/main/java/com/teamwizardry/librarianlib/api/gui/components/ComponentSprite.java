package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.math.Vec2;
import org.lwjgl.opengl.GL11;

public class ComponentSprite extends GuiComponent<ComponentSprite> {

	public Option<ComponentSprite, Boolean> depth = new Option<>(true);
	public Option<ComponentSprite, Color> color = new Option<>(Color.WHITE);
	
	protected Sprite sprite;
	
	public ComponentSprite(Sprite sprite, int x, int y) {
		this(sprite, x, y, sprite.getWidth(), sprite.getHeight());
	}
	
	public ComponentSprite(Sprite sprite, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.sprite = sprite;
	}
	
	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		boolean alwaysTop = !depth.getValue(this);
		
		if(alwaysTop) {
			// store the current depth function
			GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);

			// by using GL_ALWAYS instead of disabling depth it writes to the depth buffer
			// imagine a mountain, that is the depth buffer. this causes the sprite to write
			// it's value to the depth buffer, cutting a hole down wherever it's drawn.
			GL11.glDepthFunc(GL11.GL_ALWAYS);
		}
		color.getValue(this).glColor();
		sprite.getTex().bind();
		sprite.draw(pos.xf, pos.yf, size.xi, size.yi);
		if(alwaysTop)
			GL11.glPopAttrib();
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
}
