package com.teamwizardry.librarianlib.client;

import java.util.stream.IntStream;

import net.minecraft.util.ResourceLocation;

/**
 * This class represents a section of a {@link Texture}
 */
public class Sprite {

	protected Texture tex;
	protected int u, v, uvWidth, uvHeight;
	protected int[] frames, frameCounts;
	protected int frametotal;
	
	/**
	 * The width on screen of the sprite.
	 * 
	 * Public for easy and concise access. Set to 0 by default.
	 */
	public int width  = 0;
	
	/**
	 * The height on screen of the sprite.
	 * 
	 * Public for easy and concise access. Set to 0 by default.
	 */
	public int height = 0;
	
	public Sprite(Texture tex, int u, int v, int width, int height, int[] frameCounts) {
		this.tex = tex;
		init(u, v, width, height, frameCounts);
	}
	
	public Sprite(ResourceLocation loc) {
		this.tex = new Texture(loc);
		this.u = 0;
		this.v = 0;
		this.uvWidth = 16;
		this.uvHeight = 16;
	}
	
	/**
	 * Initializes the sprite. Used to reinitialize on resource pack reload.
	 * 
	 * --Package private--
	 */
	void init(int u, int v, int width, int height, int[] frameCounts) {
		this.u = u;
		this.v = v;
		this.uvWidth = width;
		this.uvHeight = height;
		this.frameCounts = frameCounts;
		this.frametotal = IntStream.of(frameCounts).sum();
		this.frames = new int[frametotal];
		int j = 0;
		for (int i = 0; i < frameCounts.length; i++) {
			for(int k = 0; k < frameCounts[i]; k++) {
				frames[j+k] = i;
			}
			j += frameCounts[i];
		}
	}
	
	/**
	 * The minimum U coordinate (0-1)
	 */
	public float minU() {
		return minU(0);
	}
	
	/**
	 * The minimum V coordinate (0-1)
	 */
	public float minV() {
		return minV(0);
	}
	
	/**
	 * The maximum U coordinate (0-1)
	 */
	public float maxU() {
		return maxU(0);
	}
	
	/**
	 * The maximum V coordinate (0-1)
	 */
	public float maxV() {
		return maxV(0);
	}
	
	/**
	 * The minimum U coordinate (0-1)
	 * 
	 * @param offset The offset in pixels toward the center of the texture
	 */
	public float minU(int offset) {
		return (float)(u+offset)/(float)tex.getWidth();
	}
	
	/**
	 * The minimum V coordinate (0-1)
	 *
	 * @param offset The offset in pixels toward the center of the texture
	 */
	public float minV(int offset) {
		return (float)(v+offset)/(float)tex.getHeight();
	}
	
	/**
	 * The maximum U coordinate (0-1)
	 *
	 * @param offset The offset in pixels toward the center of the texture
	 */
	public float maxU(int offset) {
		return (float)(u+uvWidth-offset)/(float)tex.getWidth();
	}
	
	/**
	 * The maximum V coordinate (0-1)
	 *
	 * @param offset The offset in pixels toward the center of the texture
	 */
	public float maxV(int offset) {
		return (float)(v+uvHeight-offset)/(float)tex.getHeight();
	}

	/**
	 * The {@link Texture} that this sprite is a part of
	 * @return
	 */
	public Texture getTex() {
		return tex;
	}

	/**
	 * The minimum U coordinate in pixels
	 */
	public int getU() {
		return u;
	}

	/**
	 * The minimum V coordinate in pixels
	 */
	public int getV() {
		return v;
	}

	/**
	 * The width in pixels
	 */
	public int getUVWidth() {
		return uvWidth;
	}

	/**
	 * The height in pixels
	 */
	public int getUVHeight() {
		return uvHeight;
	}
	
	public Sprite getSubSprite(int u, int v, int width, int height) {
		float uScale = uvWidth/width;
		float vScale = uvHeight/height;
		return new Sprite(this.tex, this.u+(int)( u*uScale ), this.v+(int)( v*vScale ), (int)( width*uScale ), (int)( height*vScale ), frameCounts);
	}
	
	/**
	 * Draws the sprite to the screen
	 * @param x The x position to draw at
	 * @param y The y position to draw at
	 */
	public void draw(float x, float y) {
		DrawingUtil.draw(this, x, y, width, height);
	}
	
	/**
	 * Draws the sprite to the screen with a custom width and height
	 * @param x The x position to draw at
	 * @param y The y position to draw at
	 * @param width The width to draw the sprite
	 * @param height The height to draw the sprite
	 */
	public void draw(float x, float y, int width, int height) {
		DrawingUtil.draw(this, x, y, width, height);
	}
	
	/**
	 * Draws the sprite to the screen with a custom width and height by clipping or tiling instead of stretching/squashing
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawClipped(float x, float y, int width, int height) {
		DrawingUtil.drawClipped(this, x, y, width, height);
	}
	
}
