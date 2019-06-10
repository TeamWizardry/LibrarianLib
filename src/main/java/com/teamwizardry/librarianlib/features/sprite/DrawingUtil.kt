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
    @Deprecated("Replaced with sprite pinning functionality", replaceWith = ReplaceWith(
        "draw(sprite.pinnedWrapper(!reverseY, reverseY, !reverseX, reverseX), " +
            "animTicks, x, y, width.toFloat(), height.toFloat())"
    ))
    fun drawClipped(sprite: ISprite, animTicks: Int, x: Float, y: Float, width: Int, height: Int, reverseX: Boolean, reverseY: Boolean) {
        draw(sprite.pinnedWrapper(!reverseY, reverseY, !reverseX, reverseX), animTicks, x, y, width.toFloat(), height.toFloat())
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

        val rotation = sprite.rotation

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
                val (u0, v0, u1, v1, u2, v2, u3, v3) = spinUVs(
                    rotation, spriteMinU, spriteMinV, spriteUSpan, spriteVSpan,
                    xSection.minTex, ySection.maxTex,
                    xSection.maxTex, ySection.maxTex,
                    xSection.maxTex, ySection.minTex,
                    xSection.minTex, ySection.minTex
                )

                vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(u0, v0).endVertex()
                vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(u1, v1).endVertex()
                vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(u2, v2).endVertex()
                vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(u3, v3).endVertex()
            }
        }
    }

    private data class DestructureUVs(
        var u0: Double, var v0: Double, var u1: Double, var v1: Double,
        var u2: Double, var v2: Double, var u3: Double, var v3: Double
    )
    private val spinValue = DestructureUVs(
        0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0
    )

    @Suppress("NAME_SHADOWING")
    private fun spinUVs(
        rotation: Int, spriteMinU: Float, spriteMinV: Float, spriteUSpan: Float, spriteVSpan: Float,
        u0: Float, v0: Float, u1: Float, v1: Float,
        u2: Float, v2: Float, u3: Float, v3: Float
    ): DestructureUVs {
        val u0 = u0 - 0.5; val v0 = v0 - 0.5
        val u1 = u1 - 0.5; val v1 = v1 - 0.5
        val u2 = u2 - 0.5; val v2 = v2 - 0.5
        val u3 = u3 - 0.5; val v3 = v3 - 0.5

        when(rotation % 4 + if(rotation < 0) 4 else 0) {
            1 -> {
                spinValue.u0 = v0; spinValue.v0 = -u0
                spinValue.u1 = v1; spinValue.v1 = -u1
                spinValue.u2 = v2; spinValue.v2 = -u2
                spinValue.u3 = v3; spinValue.v3 = -u3
            }
            2 -> {
                spinValue.u0 = -u0; spinValue.v0 = -v0
                spinValue.u1 = -u1; spinValue.v1 = -v1
                spinValue.u2 = -u2; spinValue.v2 = -v2
                spinValue.u3 = -u3; spinValue.v3 = -v3
            }
            3 -> {
                spinValue.u0 = -v0; spinValue.v0 = u0
                spinValue.u1 = -v1; spinValue.v1 = u1
                spinValue.u2 = -v2; spinValue.v2 = u2
                spinValue.u3 = -v3; spinValue.v3 = u3
            }
            else -> {
                spinValue.u0 = u0; spinValue.v0 = v0
                spinValue.u1 = u1; spinValue.v1 = v1
                spinValue.u2 = u2; spinValue.v2 = v2
                spinValue.u3 = u3; spinValue.v3 = v3
            }
        }

        spinValue.u0 = spriteMinU + (spinValue.u0 + 0.5) * spriteUSpan
        spinValue.v0 = spriteMinV + (spinValue.v0 + 0.5) * spriteVSpan
        spinValue.u1 = spriteMinU + (spinValue.u1 + 0.5) * spriteUSpan
        spinValue.v1 = spriteMinV + (spinValue.v1 + 0.5) * spriteVSpan
        spinValue.u2 = spriteMinU + (spinValue.u2 + 0.5) * spriteUSpan
        spinValue.v2 = spriteMinV + (spinValue.v2 + 0.5) * spriteVSpan
        spinValue.u3 = spriteMinU + (spinValue.u3 + 0.5) * spriteUSpan
        spinValue.v3 = spriteMinV + (spinValue.v3 + 0.5) * spriteVSpan

        return spinValue
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
        val wholeCount = (targetSize / logicalSize).toInt()

        var pos = minPos
        var tex = minTex

        if(!pinStart) {
            sections.add(Section(pos, pos + fractionSize, tex + texSize - fractionTexSize, tex + texSize))
            pos += fractionSize
        }

        for(i in 0 until wholeCount) {
            sections.add(Section(pos, pos + logicalSize, tex, tex + texSize))
            pos += logicalSize
        }

        if(pinStart) {
            sections.add(Section(pos, pos + fractionSize, tex, tex + fractionTexSize))
            pos += fractionSize
        }
    }

    private class Section(var minPos: Float, var maxPos: Float, var minTex: Float, var maxTex: Float)
}
