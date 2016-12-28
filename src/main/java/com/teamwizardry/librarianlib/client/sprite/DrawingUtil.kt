package com.teamwizardry.librarianlib.client.sprite

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object DrawingUtil {
    var isDrawing = false

    /**
     * Start drawing multiple quads to be pushed to the GPU at once
     */
    fun startDrawingSession() {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        isDrawing = true
    }

    /**
     * Finish drawing multiple quads and push them to the GPU
     */
    fun endDrawingSession() {
        val tessellator = Tessellator.getInstance()
        tessellator.draw()
        isDrawing = false
    }

    /**
     * **!!! Use [Sprite.draw] or [Sprite.draw] instead !!!**

     *
     * Draw a sprite at a location with the width and height specified.
     * @param sprite The sprite to draw
     * *
     * @param x The x position to draw at
     * *
     * @param y The y position to draw at
     * *
     * @param width The width to draw the sprite
     * *
     * @param height The height to draw the sprite
     */
    fun draw(sprite: Sprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {

        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if (!isDrawing)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(sprite.minU(animFrames).toDouble(), sprite.maxV(animFrames).toDouble()).endVertex()
        vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(sprite.maxU(animFrames).toDouble(), sprite.maxV(animFrames).toDouble()).endVertex()
        vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(sprite.maxU(animFrames).toDouble(), sprite.minV(animFrames).toDouble()).endVertex()
        vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(sprite.minU(animFrames).toDouble(), sprite.minV(animFrames).toDouble()).endVertex()

        if (!isDrawing)
            tessellator.draw()
    }

    /**
     * **!!! Use [Sprite.drawClipped] instead !!!**

     *
     * Draw a sprite at a location with the width and height specified by clipping or tiling instead of stretching/squishing
     * @param sprite The sprite to draw
     * *
     * @param x The x position to draw at
     * *
     * @param y The y position to draw at
     * *
     * @param width The width to draw the sprite
     * *
     * @param height The height to draw the sprite
     */
    fun drawClipped(sprite: Sprite, animTicks: Int, x: Float, y: Float, width: Int, height: Int, reverseX: Boolean, reverseY: Boolean) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if (!isDrawing)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        val wholeSpritesX = Math.ceil((width.toFloat() / sprite.width.toFloat()).toDouble()).toInt() - 1
        val wholeSpritesY = Math.ceil((height.toFloat() / sprite.height.toFloat()).toDouble()).toInt() - 1

        var leftoverWidth = width % sprite.width
        var leftoverHeight = height % sprite.height

        if (leftoverWidth == 0)
            leftoverWidth = sprite.width
        if (leftoverHeight == 0)
            leftoverHeight = sprite.height

        for (xIndex in 0..wholeSpritesX) {
            for (yIndex in 0..wholeSpritesY) {

                val smallX = if (reverseX) xIndex == 0 else xIndex == wholeSpritesX
                val smallY = if (reverseY) yIndex == 0 else yIndex == wholeSpritesY

                val spriteWidth = if (smallX) if (wholeSpritesX == 0) width else leftoverWidth else sprite.width
                val spriteHeight = if (smallY) if (wholeSpritesY == 0) height else leftoverHeight else sprite.height
                val offsetX = if (reverseX) if (xIndex == 0) 0 else sprite.width * (xIndex - 1) + width else sprite.width * xIndex
                val offsetY = if (reverseY) if (yIndex == 0) 0 else sprite.height * (yIndex - 1) + height else sprite.height * yIndex

                val minX = x + offsetX
                val minY = y + offsetY
                val maxX = minX + spriteWidth
                val maxY = minY + spriteHeight
                val uSpan = sprite.maxU(animTicks) - sprite.minU(animTicks)
                val vSpan = sprite.maxV(animTicks) - sprite.minV(animTicks)

                val minU = sprite.minU(animTicks)
                val minV = sprite.minV(animTicks)
                val maxU = minU + uSpan * (spriteWidth.toFloat() / sprite.width.toFloat())
                val maxV = minV + vSpan * (spriteHeight.toFloat() / sprite.height.toFloat())

                vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
                vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
                vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()
                vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
                vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
            }
        }

        if (!isDrawing)
            tessellator.draw()
    }
}
