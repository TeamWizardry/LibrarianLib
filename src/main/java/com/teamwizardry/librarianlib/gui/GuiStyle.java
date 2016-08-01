package com.teamwizardry.librarianlib.gui;

import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.sprite.Sprite;
import com.teamwizardry.librarianlib.sprite.Texture;
import net.minecraft.util.ResourceLocation;

public class GuiStyle {
	
	public static final GuiStyle NORMAL = new GuiStyle(new Texture(new ResourceLocation(LibrarianLib.MODID, "textures/styles/normal.png")));
	
	public final Texture TEXTURE;
	public final Sprite
		BUTTON
	;
	public final int
		BUTTON_BORDER = 4
	;
	public GuiStyle(Texture tex) {
		this.TEXTURE = tex;
		
		BUTTON = TEXTURE.getSprite("button", 32, 32);
	}
}
