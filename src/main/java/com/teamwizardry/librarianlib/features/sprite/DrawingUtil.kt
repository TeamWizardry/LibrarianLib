package com.teamwizardry.librarianlib.features.sprite

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
        Tessellator.getInstance().buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        isDrawing = true
    }

    /**
     * Finish drawing multiple quads and push them to the GPU
     */
    fun endDrawingSession() {
        Tessellator.getInstance().draw()
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
        if (!isDrawing) {
            val hor = Math.ceil(width / sprite.width.toDouble()).toInt()
            val vert = Math.ceil(height / sprite.height.toDouble()).toInt()

            for (xIndex in 0 until hor) {
                for (yIndex in 0 until vert) {
                    val croppedX = (reverseX && xIndex == 0) || xIndex == hor - 1
                    val croppedY = (reverseY && yIndex == 0) || yIndex == vert - 1

                    var cropW = if (croppedX) sprite.width - width % sprite.width else 0
                    if (cropW == sprite.width) cropW = 0
                    var cropH = if (croppedY) sprite.height - height % sprite.height else 0
                    if (cropH == sprite.height) cropH = 0

                    val sX = x + xIndex * sprite.width
                    val sY = y + yIndex * sprite.height

                    val realX = (if (reverseX) sX + cropW else sX).toDouble()
                    val realY = (if (reverseY) sY + cropH else sY).toDouble()

                    val maxX = realX + if (croppedX) sprite.width - cropW else sprite.width
                    val maxY = realY + if (croppedY) sprite.height - cropH else sprite.height

                    var minU = sprite.minU(animTicks).toDouble()
                    var minV = sprite.minV(animTicks).toDouble()

                    var maxU = sprite.maxU(animTicks).toDouble()
                    var maxV = sprite.maxV(animTicks).toDouble()

                    val uSpan = maxU - minU
                    val vSpan = maxV - minV

                    if (croppedX) {
                        if (reverseX) minU += uSpan * (cropW / sprite.width.toDouble())
                        else maxU -= uSpan * (cropW / sprite.width.toDouble())
                    }
                    if (croppedY) {
                        if (reverseY) minV += vSpan * (cropH / sprite.height.toDouble())
                        else maxV -= vSpan * (cropH / sprite.height.toDouble())
                    }

                    val vb1 = Tessellator.getInstance().buffer
                    startDrawingSession()
                    vb1.pos(realX, realY, 0.0).tex(minU, minV).endVertex()
                    vb1.pos(realX, maxY, 0.0).tex(minU, maxV).endVertex()
                    vb1.pos(maxX, maxY, 0.0).tex(maxU, maxV).endVertex()
                    vb1.pos(maxX, realY, 0.0).tex(maxU, minV).endVertex()
                    endDrawingSession()
                }
            }
        }
    }
}
