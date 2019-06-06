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
    fun draw(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if (!isDrawing)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        if(sprite.pinTop && sprite.pinBottom && sprite.pinLeft && sprite.pinRight &&
            sprite.minUCap == 0f && sprite.minVCap == 0f && sprite.maxUCap == 0f && sprite.maxVCap == 0f) {
            drawSimple(sprite, animFrames, x, y, width, height)
        } else {
            drawComplex(sprite, animFrames, x, y, width, height)
        }

        if (!isDrawing)
            tessellator.draw()
    }

    private fun drawSimple(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {
        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(sprite.minU(animFrames).toDouble(), sprite.maxV(animFrames).toDouble()).endVertex()
        vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(sprite.maxU(animFrames).toDouble(), sprite.maxV(animFrames).toDouble()).endVertex()
        vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(sprite.maxU(animFrames).toDouble(), sprite.minV(animFrames).toDouble()).endVertex()
        vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(sprite.minU(animFrames).toDouble(), sprite.minV(animFrames).toDouble()).endVertex()
    }

    private fun drawComplex(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {

        val xSections = getSections(
            logicalSize = sprite.width.toFloat(),
            startCap = sprite.minUCap,
            endCap = sprite.maxUCap,
            pinStart = sprite.pinLeft,
            pinEnd = sprite.pinRight,
            targetSize = width
        )

        val ySections = getSections(
            logicalSize = sprite.height.toFloat(),
            startCap = sprite.minVCap,
            endCap = sprite.maxVCap,
            pinStart = sprite.pinTop,
            pinEnd = sprite.pinBottom,
            targetSize = height
        )

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val spriteMinU = sprite.minU(animFrames)
        val spriteMinV = sprite.minV(animFrames)
        val spriteUSpan = sprite.maxU(animFrames) - spriteMinU
        val spriteVSpan = sprite.maxV(animFrames) - spriteMinV
        xSections.forEach { xSection ->
            ySections.forEach { ySection ->
                val minX = x + xSection.minPos
                val minY = y + ySection.minPos
                val maxX = x + xSection.maxPos
                val maxY = y + ySection.maxPos
                val minU = spriteMinU + xSection.minTex * spriteUSpan
                val minV = spriteMinV + ySection.minTex * spriteVSpan
                val maxU = spriteMinU + xSection.maxTex * spriteUSpan
                val maxV = spriteMinV + ySection.maxTex * spriteVSpan
                vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
                vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
                vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()
                vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
            }
        }
    }

    private fun getSections(logicalSize: Float, startCap: Float, endCap: Float, pinStart: Boolean, pinEnd: Boolean, targetSize: Float): List<Section> {
        if(targetSize == 0f) {
            return emptyList()
        }
        val sections = mutableListOf<Section>()

        val logicalStartCap = logicalSize * startCap
        val logicalEndCap = logicalSize * endCap
        if(logicalStartCap + logicalEndCap != 0f && logicalStartCap + logicalEndCap > targetSize) {
            val factor = (logicalStartCap + logicalEndCap)/targetSize
            sections.add(Section(0f, logicalStartCap/factor, 0f, startCap/factor))
            sections.add(Section(logicalStartCap/factor, logicalEndCap/factor, 1-endCap/factor, 1f))
            return sections
        }

        var pos = 0f
        if(logicalStartCap != 0f) {
            sections.add(Section(0f, logicalStartCap, 0f, startCap))
            pos += logicalStartCap
        }

        if(pinStart == pinEnd) { // both true or both false. Both being false is unclear so it defaults to pinning
            sections.add(Section(logicalStartCap, targetSize - logicalEndCap, startCap, 1-endCap))
        } else {
            addMiddleSections(sections,
                1 - startCap - endCap,
                logicalSize - logicalStartCap - logicalEndCap,
                targetSize - logicalStartCap - logicalEndCap,
                pos, startCap, pinStart
            )
        }

        if(logicalEndCap != 0f) {
            sections.add(Section(targetSize - logicalEndCap, targetSize, 1-endCap, 1f))
        }
        return sections
    }

    /**
     * If [pinStart] is false, `pinEnd` is implied
     */
    private fun addMiddleSections(sections: MutableList<Section>,
        texSize: Float, logicalSize: Float, targetSize: Float,
        minPos: Float, minTex: Float, pinStart: Boolean
    ) {

        val fractionSize = targetSize % logicalSize
        val fractionTexSize = fractionSize * texSize / logicalSize
        val wholeCount = (targetSize % logicalSize).toInt()

        var pos = minPos
        var tex = minTex

        if(!pinStart) {
            sections.add(Section(pos, pos + fractionSize, tex + texSize - fractionTexSize, tex + texSize))
            pos += fractionSize
            tex += fractionTexSize
        }

        for(i in 0 until wholeCount) {
            sections.add(Section(pos, pos + logicalSize, tex, tex + texSize))
            pos += logicalSize
            tex += texSize
        }

        if(pinStart) {
            sections.add(Section(pos, pos + fractionSize, tex, tex + fractionTexSize))
            pos += fractionSize
            tex += fractionTexSize
        }
    }

    private class Section(var minPos: Float, var maxPos: Float, var minTex: Float, var maxTex: Float)
}
