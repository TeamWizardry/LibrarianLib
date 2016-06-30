package com.teamwizardry.librarianlib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * This class represents a texture and it's size. It is mostly used to create {@link Sprite}
 * objects
 */
public class Texture {

	private int width;
	private int height;
	private ResourceLocation loc;
	
	public Texture(ResourceLocation loc, int width, int height) {
		this.loc = loc;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Gets a sprite from the specified section of the texture
	 * @param u The left coordinate
	 * @param v The top coordinate
	 * @param width The width
	 * @param height The height
	 * @return The sprite
	 */
	public Sprite getSprite(int u, int v, int width, int height) {
		return new Sprite(this, u, v, width, height);
	}
	
	/**
	 * The width of the texture in pixels
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * The height of the texture in pixels
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * The location of the texture
	 */
	public ResourceLocation getLoc() {
		return loc;
	}
	
	/**
	 * Bind this texture
	 */
	public void bind() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(getLoc());
	}
	
}
