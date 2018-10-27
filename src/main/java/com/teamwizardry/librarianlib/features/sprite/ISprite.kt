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
     * The logical width of this sprite.
     */
    val width: Int

    /**
     * The logical height of this sprite.
     */
    val height: Int

    /**
     * The UV width of this sprite.
     */
    val uSize: Float

    /**
     * The UV height of this sprite.
     */
    val vSize: Float

    /**
     * Frames for this sprite (if animated). Must be >= 1
     */
    val frameCount: Int

    /**
     * The fraction of the sprite along the minimum U edge that should not be distorted when stretching the sprite.
     */
    val minUCap: Float

    /**
     * The fraction of the sprite along the minimum V edge that should not be distorted when stretching the sprite.
     */
    val minVCap: Float

    /**
     * The fraction of the sprite along the maximum U edge that should not be distorted when stretching the sprite.
     */
    val maxUCap: Float

    /**
     * The fraction of the sprite along the maximum V edge that should not be distorted when stretching the sprite.
     */
    val maxVCap: Float

    /**
     * Whether the sprite should be repeated/truncated rather than stretched/squished along the U axis
     */
    val hardScaleU: Boolean

    /**
     * Whether the sprite should be repeated/truncated rather than stretched/squished along the V axis
     */
    val hardScaleV: Boolean
}