package com.teamwizardry.librarianlib.features.sprite

/*
 * Created by bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * Abstraction for Sprites.
 * Most use cases will use the [Sprite] implementation, however
 * the [LTextureAtlasSprite] is needed in some cases.
 */
@SideOnly(Side.CLIENT)
interface ISprite {

    /**
     * Binds the texture to be used for rendering.
     */
    fun bind()

    /**
     * The minimum U coordinate (0-1)
     */
    fun minU(animFrames: Int = 0): Float

    fun minU() = minU(0) // JVM overload

    /**
     * The minimum V coordinate (0-1)
     */
    fun minV(animFrames: Int = 0): Float

    fun minV() = minU(0) // JVM overload

    /**
     * The maximum U coordinate (0-1)
     */
    fun maxU(animFrames: Int = 0): Float

    fun maxU() = minU(0) // JVM overload

    /**
     * The maximum V coordinate (0-1)
     */
    fun maxV(animFrames: Int = 0): Float

    fun maxV() = minU(0) // JVM overload

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * *
     * @param y The y position to draw at
     */
    fun draw(animTicks: Int, x: Float, y: Float) {
        DrawingUtil.draw(this, animTicks, x, y, width.toFloat(), height.toFloat())
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
    fun draw(animTicks: Int, x: Float, y: Float, width: Float, height: Float) {
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
    fun drawClipped(animTicks: Int, x: Float, y: Float, width: Int, height: Int, reverseX: Boolean = false, reverseY: Boolean = false) {
        DrawingUtil.drawClipped(this, animTicks, x, y, width, height, reverseX, reverseY)
    }

    fun drawClipped(animTicks: Int, x: Float, y: Float, width: Int, height: Int) =
            drawClipped(animTicks, x, y, width, height, false, false)


    /**
     * Width of this sprite.
     */
    val width: Int

    /**
     * Intended width for this sprite.
     * Used for automatic repetition (instead of bad scaling).
     */
    val inWidth
        get() = width

    /**
     * Height of this sprite.
     */
    val height: Int

    /**
     * Intended height for this sprite.
     * Used for automatic repetition (instead of bad scaling).
     */
    val inHeight
        get() = height

    /**
     * Frames for this sprite (if animated).
     */
    val frameCount: Int
}