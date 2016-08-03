package com.teamwizardry.librarianlib.sprite;

import net.minecraft.util.ResourceLocation;

/**
 * This class represents a section of a {@link Texture}
 */
public class Sprite {

	protected Texture tex;
	protected int u, v, uvWidth, uvHeight;
	protected int[] frames, frameCounts;
	protected int offsetU, offsetV;
	
	/**
	 * The width on screen of the sprite.
	 * 
	 * Public for easy and concise access. Set to 1 by default.
	 */
	public int width  = 1;
	
	/**
	 * The height on screen of the sprite.
	 * 
	 * Public for easy and concise access. Set to 1 by default.
	 */
	public int height = 1;
	
	public Sprite(Texture tex, int u, int v, int width, int height, int[] frames, int offsetU, int offsetV) {
		this.tex = tex;
		init(u, v, width, height, frames, offsetU, offsetV);
	}
	
	public Sprite(ResourceLocation loc) {
		this.tex = new Texture(loc);
		this.u = 0;
		this.v = 0;
		this.uvWidth = 16;
		this.uvHeight = 16;
		this.width = 16;
		this.height = 16;
		this.frames = new int[0];
	}
	
	/**
	 * Initializes the sprite. Used to reinitialize on resource pack reload.
	 * 
	 * --Package private--
	 */
	void init(int u, int v, int width, int height, int[] frames, int offsetU, int offsetV) {
		this.u = u;
		this.v = v;
		this.uvWidth = width;
		this.uvHeight = height;
		this.offsetU = offsetU;
		this.offsetV = offsetV;
		this.frames = frames;
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
	 */
	public float minU(int animFrames) {
		return (float)(u + ( offsetU * (frames.length == 0 ? 0 : frames[animFrames % frames.length]) ))/(float)tex.getWidth();
	}
	
	/**
	 * The minimum V coordinate (0-1)
	 */
	public float minV(int animFrames) {
		return (float)(v + ( offsetV * (frames.length == 0 ? 0 : frames[animFrames % frames.length]) ))/(float)tex.getHeight();
	}
	
	/**
	 * The maximum U coordinate (0-1)
	 */
	public float maxU(int animFrames) {
		return (float)(u+uvWidth + ( offsetU * (frames.length == 0 ? 0 : frames[animFrames % frames.length]) ))/(float)tex.getWidth();
	}
	
	/**
	 * The maximum V coordinate (0-1)
	 */
	public float maxV(int animFrames) {
		return (float)(v+uvHeight + ( offsetV * (frames.length == 0 ? 0 : frames[animFrames % frames.length]) ))/(float)tex.getHeight();
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
		float uScale = (float)uvWidth/(float)this.width;
		float vScale = (float)uvHeight/(float)this.height;
		Sprite s = new Sprite(this.tex, this.u+(int)( u*uScale ), this.v+(int)( v*vScale ), (int)( width*uScale ), (int)( height*vScale ), frames, offsetU, offsetV);
		s.width = width;
		s.height = height;
		return s;
	}
	
	/**
	 * Draws the sprite to the screen
	 * @param x The x position to draw at
	 * @param y The y position to draw at
	 */
	public void draw(int animTicks, float x, float y) {
		DrawingUtil.draw(this, animTicks, x, y, width, height);
	}
	
	/**
	 * Draws the sprite to the screen with a custom width and height
	 * @param x The x position to draw at
	 * @param y The y position to draw at
	 * @param width The width to draw the sprite
	 * @param height The height to draw the sprite
	 */
	public void draw(int animTicks, float x, float y, int width, int height) {
		DrawingUtil.draw(this, animTicks, x, y, width, height);
	}
	
	/**
	 * Draws the sprite to the screen with a custom width and height by clipping or tiling instead of stretching/squashing
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawClipped(int animTicks, float x, float y, int width, int height) {
		DrawingUtil.drawClipped(this, animTicks, x, y, width, height);
	}
	
}
