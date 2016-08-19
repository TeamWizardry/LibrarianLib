package com.teamwizardry.librarianlib.client.sprite

import net.minecraft.util.ResourceLocation

/**
 * This class represents a section of a [Texture]
 */
class Sprite {

    /**
     * The [Texture] that this sprite is a part of
     * @return
     */
    var tex: Texture
        protected set
    /**
     * The minimum U coordinate in pixels
     */
    var u: Int = 0
        protected set
    /**
     * The minimum V coordinate in pixels
     */
    var v: Int = 0
        protected set
    /**
     * The width in pixels
     */
    var uvWidth: Int = 0
        protected set
    /**
     * The height in pixels
     */
    var uvHeight: Int = 0
        protected set
    protected var frames: IntArray = IntArray(0)
    protected var offsetU: Int = 0
    protected var offsetV: Int = 0

    /**
     * The width on screen of the sprite.

     * Public for easy and concise access. Set to 1 by default.
     */
    var width = 1

    /**
     * The height on screen of the sprite.

     * Public for easy and concise access. Set to 1 by default.
     */
    var height = 1

    constructor(tex: Texture, u: Int, v: Int, width: Int, height: Int, frames: IntArray, offsetU: Int, offsetV: Int) {
        this.tex = tex
        init(u, v, width, height, frames, offsetU, offsetV)
    }

    constructor(loc: ResourceLocation) {
        this.tex = Texture(loc)
        this.u = 0
        this.v = 0
        this.uvWidth = 16
        this.uvHeight = 16
        this.width = 16
        this.height = 16
        this.frames = IntArray(0)
    }

    /**
     * Initializes the sprite. Used to reinitialize on resource pack reload.

     * --Package private--
     */
    internal fun init(u: Int, v: Int, width: Int, height: Int, frames: IntArray, offsetU: Int, offsetV: Int) {
        this.u = u
        this.v = v
        this.uvWidth = width
        this.uvHeight = height
        this.offsetU = offsetU
        this.offsetV = offsetV
        this.frames = frames
    }

    /**
     * The minimum U coordinate (0-1)
     */
    @JvmOverloads fun minU(animFrames: Int = 0): Float {
        return (u + offsetU * if (frames.size == 0) 0 else frames[animFrames % frames.size]).toFloat() / tex.width.toFloat()
    }

    /**
     * The minimum V coordinate (0-1)
     */
    @JvmOverloads fun minV(animFrames: Int = 0): Float {
        return (v + offsetV * if (frames.size == 0) 0 else frames[animFrames % frames.size]).toFloat() / tex.height.toFloat()
    }

    /**
     * The maximum U coordinate (0-1)
     */
    @JvmOverloads fun maxU(animFrames: Int = 0): Float {
        return (u + uvWidth + offsetU * if (frames.size == 0) 0 else frames[animFrames % frames.size]).toFloat() / tex.width.toFloat()
    }

    /**
     * The maximum V coordinate (0-1)
     */
    @JvmOverloads fun maxV(animFrames: Int = 0): Float {
        return (v + uvHeight + offsetV * if (frames.size == 0) 0 else frames[animFrames % frames.size]).toFloat() / tex.height.toFloat()
    }

    fun getSubSprite(u: Int, v: Int, width: Int, height: Int): Sprite {
        val uScale = uvWidth.toFloat() / this.width.toFloat()
        val vScale = uvHeight.toFloat() / this.height.toFloat()
        val s = Sprite(this.tex, this.u + (u * uScale).toInt(), this.v + (v * vScale).toInt(), (width * uScale).toInt(), (height * vScale).toInt(), frames, offsetU, offsetV)
        s.width = width
        s.height = height
        return s
    }

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * *
     * @param y The y position to draw at
     */
    fun draw(animTicks: Int, x: Float, y: Float) {
        DrawingUtil.draw(this, animTicks, x, y, width, height)
    }

    /**
     * Draws the sprite to the screen with a custom width and height
     * @param x The x position to draw at
     * *
     * @param y The y position to draw at
     * *
     * @param width The width to draw the sprite
     * *
     * @param height The height to draw the sprite
     */
    fun draw(animTicks: Int, x: Float, y: Float, width: Int, height: Int) {
        DrawingUtil.draw(this, animTicks, x, y, width, height)
    }

    /**
     * Draws the sprite to the screen with a custom width and height by clipping or tiling instead of stretching/squashing
     * @param x
     * *
     * @param y
     * *
     * @param width
     * *
     * @param height
     */
    fun drawClipped(animTicks: Int, x: Float, y: Float, width: Int, height: Int) {
        DrawingUtil.drawClipped(this, animTicks, x, y, width, height)
    }

}
/**
 * The minimum U coordinate (0-1)
 */
/**
 * The minimum V coordinate (0-1)
 */
/**
 * The maximum U coordinate (0-1)
 */
/**
 * The maximum V coordinate (0-1)
 */
