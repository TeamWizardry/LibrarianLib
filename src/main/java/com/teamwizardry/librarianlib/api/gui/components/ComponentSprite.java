package com.teamwizardry.librarianlib.api.gui.components;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.math.Vec2;
import com.teamwizardry.librarianlib.client.Sprite;

public class ComponentSprite extends GuiComponent<ComponentSprite> {

	public Option<ComponentSprite, Boolean> depth = new Option<>(true);
	
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
		boolean alwaysTop = !depth.getValue(this);
		if(alwaysTop) {
			GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);// store the current depth function
			GL11.glDepthFunc(GL11.GL_ALWAYS); // don't disable depth because it
			// wouldn't write to the depth buffer, thus other things would be cut off even if they are in front of this
		}
		GlStateManager.color(1, 1, 1);
		sprite.getTex().bind();
		sprite.draw(pos.xf, pos.yf, size.xi, size.yi);
		if(alwaysTop) {
			GL11.glPopAttrib();
		}
	}
	
}
