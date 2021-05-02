package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Abstraction for Sprites.
 * Most use cases will use the [MosaicSprite] implementation, however
 * the [MinecraftAtlasSprite] is needed in some cases.
 */
public interface Sprite {

    /**
     * The render type to be used when drawing. The renderer expects this type to be drawing [GL11.GL_QUADS]
     * using [VertexFormats.POSITION_COLOR_TEXTURE]
     */
    public val renderType: RenderLayer

    /**
     * The logical width of this sprite.
     */
    public val width: Int

    /**
     * The logical height of this sprite.
     */
    public val height: Int

    /**
     * The UV width of this sprite.
     */
    public val uSize: Float

    /**
     * The UV height of this sprite.
     */
    public val vSize: Float

    /**
     * Frames for this sprite (if animated). Must be >= 1
     */
    public val frameCount: Int

    /**
     * The fraction of the sprite along the minimum U edge that should not be distorted when stretching the sprite.
     */
    public val minUCap: Float get() = 0f

    /**
     * The fraction of the sprite along the minimum V edge that should not be distorted when stretching the sprite.
     */
    public val minVCap: Float get() = 0f

    /**
     * The fraction of the sprite along the maximum U edge that should not be distorted when stretching the sprite.
     */
    public val maxUCap: Float get() = 0f

    /**
     * The fraction of the sprite along the maximum V edge that should not be distorted when stretching the sprite.
     */
    public val maxVCap: Float get() = 0f

    /**
     * Whether the top of this sprite should be pinned. If this is false the top of the texture will truncate or
     * repeat if the sprite is drawn shorter or taller than normal.
     *
     * If both this and [pinBottom] are false, this sprite will render as if both were true
     */
    public val pinTop: Boolean get() = true

    /**
     * Whether the bottom of this sprite should be pinned. If this is false the bottom of the texture will truncate or
     * repeat if the sprite is drawn shorter or taller than normal.
     *
     * If both this and [pinTop] are false, this sprite will render as if both were true
     */
    public val pinBottom: Boolean get() = true

    /**
     * Whether the left side of this sprite should be pinned. If this is false the left side of the texture will
     * truncate or repeat if the sprite is drawn narrower or wider than normal.
     *
     * If both this and [pinRight] are false, this sprite will render as if both were true
     */
    public val pinLeft: Boolean get() = true

    /**
     * Whether the right side of this sprite should be pinned. If this is false the right side of the texture will
     * truncate or repeat if the sprite is drawn narrower or wider than normal.
     *
     * If both this and [pinLeft] are false, this sprite will render as if both were true
     */
    public val pinRight: Boolean get() = true

    /**
     * The number of clockwise 90° rotations should be made when drawing this sprite.
     */
    public val rotation: Int get() = 0

    /**
     * The minimum U coordinate (0-1)
     */
    public fun minU(animFrames: Int): Float
    public fun minU(): Float = minU(0)

    /**
     * The minimum V coordinate (0-1)
     */
    public fun minV(animFrames: Int): Float
    public fun minV(): Float = minU(0)

    /**
     * The maximum U coordinate (0-1)
     */
    public fun maxU(animFrames: Int): Float
    public fun maxU(): Float = minU(0)

    /**
     * The maximum V coordinate (0-1)
     */
    public fun maxV(animFrames: Int): Float
    public fun maxV(): Float = minU(0)

    /**
     * Get the actual U coordinate for the specified U inside this sprite
     */
    public fun interpU(animFrames: Int, u: Float): Float = minU(animFrames) + uSize * u
    public fun interpU(u: Float): Float = interpU(0, u)

    /**
     * Get the actval V coordinate for the specified V inside this sprite
     */
    public fun interpV(animFrames: Int, v: Float): Float = minV(animFrames) + vSize * v
    public fun interpV(v: Float): Float = interpV(0, v)

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * @param y The y position to draw at
     */
    public fun draw(matrix: Matrix4d, x: Float, y: Float) {
        draw(matrix, x, y, width.toFloat(), height.toFloat(), 0, Color.WHITE)
    }

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * @param y The y position to draw at
     */
    public fun draw(matrix: Matrix4d, x: Float, y: Float, animTicks: Int, tint: Color) {
        draw(matrix, x, y, width.toFloat(), height.toFloat(), animTicks, tint)
    }

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * @param y The y position to draw at
     */
    public fun draw(matrix: Matrix4d, x: Float, y: Float, width: Float, height: Float) {
        draw(matrix, x, y, width, height, 0, Color.WHITE)
    }

    /**
     * Draws the sprite to the screen with a custom width and height
     * @param x The x position to draw at
     * @param y The y position to draw at
     * @param width The width to draw the sprite
     * @param height The height to draw the sprite
     */
    public fun draw(matrix: Matrix4d, x: Float, y: Float, width: Float, height: Float, animTicks: Int, tint: Color) {
        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val builder = buffer.getBuffer(renderType)
        DrawingUtil.draw(this, builder, matrix, x, y, width, height, animTicks, tint)
        buffer.draw()
    }

    public fun pinnedWrapper(top: Boolean, bottom: Boolean, left: Boolean, right: Boolean): Sprite {
        return PinnedWrapper(this, top, bottom, left, right)
    }
}