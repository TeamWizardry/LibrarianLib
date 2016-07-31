package com.teamwizardry.librarianlib.api.gui;

import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;

public class GuiStyle {

	public final Texture TEXTURE;
	
	public GuiStyle(Texture tex) {
		this.TEXTURE = tex;
	}
	
	public Sprite getButtonSprite() {
		return null;
	}
	
	public int getButtonBorderSize() {
		return 0;
	}
}
