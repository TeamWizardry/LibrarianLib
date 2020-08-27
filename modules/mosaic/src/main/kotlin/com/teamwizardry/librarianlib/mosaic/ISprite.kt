package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix3d
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Abstraction for Sprites.
 * Most use cases will use the [Sprite] implementation, however
 * the [LTextureAtlasSprite] is needed in some cases.
 */
interface ISprite {

    /**
     * The render type to be used when drawing. The renderer expects this type to be drawing [GL11.GL_QUADS]
     * using [DefaultVertexFormats.POSITION_COLOR_TEX]
     */
    val renderType: RenderType

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
    val minUCap: Float get() = 0f

    /**
     * The fraction of the sprite along the minimum V edge that should not be distorted when stretching the sprite.
     */
    val minVCap: Float get() = 0f

    /**
     * The fraction of the sprite along the maximum U edge that should not be distorted when stretching the sprite.
     */
    val maxUCap: Float get() = 0f

    /**
     * The fraction of the sprite along the maximum V edge that should not be distorted when stretching the sprite.
     */
    val maxVCap: Float get() = 0f

    /**
     * Whether the top of this sprite should be pinned. If this is false the top of the texture will truncate or
     * repeat if the sprite is drawn shorter or taller than normal.
     *
     * If both this and [pinBottom] are false, this sprite will render as if both were true
     */
    val pinTop: Boolean get() = true

    /**
     * Whether the bottom of this sprite should be pinned. If this is false the bottom of the texture will truncate or
     * repeat if the sprite is drawn shorter or taller than normal.
     *
     * If both this and [pinTop] are false, this sprite will render as if both were true
     */
    val pinBottom: Boolean get() = true

    /**
     * Whether the left side of this sprite should be pinned. If this is false the left side of the texture will
     * truncate or repeat if the sprite is drawn narrower or wider than normal.
     *
     * If both this and [pinRight] are false, this sprite will render as if both were true
     */
    val pinLeft: Boolean get() = true

    /**
     * Whether the right side of this sprite should be pinned. If this is false the right side of the texture will
     * truncate or repeat if the sprite is drawn narrower or wider than normal.
     *
     * If both this and [pinLeft] are false, this sprite will render as if both were true
     */
    val pinRight: Boolean get() = true

    /**
     * The number of clockwise 90° rotations should be made when drawing this sprite.
     */
    val rotation: Int get() = 0

    /**
     * The minimum U coordinate (0-1)
     */
    fun minU(animFrames: Int): Float
    fun minU() = minU(0) // JVM overload

    /**
     * The minimum V coordinate (0-1)
     */
    fun minV(animFrames: Int): Float
    fun minV() = minU(0) // JVM overload

    /**
     * The maximum U coordinate (0-1)
     */
    fun maxU(animFrames: Int): Float
    fun maxU() = minU(0) // JVM overload

    /**
     * The maximum V coordinate (0-1)
     */
    fun maxV(animFrames: Int): Float
    fun maxV() = minU(0) // JVM overload

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * @param y The y position to draw at
     */
    fun draw(matrix: Matrix3d, x: Float, y: Float) {
        draw(matrix, x, y, width.toFloat(), height.toFloat(), 0, Color.WHITE)
    }

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * @param y The y position to draw at
     */
    fun draw(matrix: Matrix3d, x: Float, y: Float, animTicks: Int, tint: Color) {
        draw(matrix, x, y, width.toFloat(), height.toFloat(), animTicks, tint)
    }

    /**
     * Draws the sprite to the screen
     * @param x The x position to draw at
     * @param y The y position to draw at
     */
    fun draw(matrix: Matrix3d, x: Float, y: Float, width: Float, height: Float) {
        draw(matrix, x, y, width, height, 0, Color.WHITE)
    }

    /**
     * Draws the sprite to the screen with a custom width and height
     * @param x The x position to draw at
     * @param y The y position to draw at
     * @param width The width to draw the sprite
     * @param height The height to draw the sprite
     */
    fun draw(matrix: Matrix3d, x: Float, y: Float, width: Float, height: Float, animTicks: Int, tint: Color) {
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val builder = buffer.getBuffer(renderType)
        DrawingUtil.draw(this, builder, matrix, x, y, width, height, animTicks, tint)
        buffer.finish()
    }

    fun pinnedWrapper(top: Boolean, bottom: Boolean, left: Boolean, right: Boolean): ISprite {
        return PinnedWrapper(this, top, bottom, left, right)
    }
}