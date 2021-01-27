package com.teamwizardry.librarianlib.mosaic

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.core.util.kotlin.tex
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import java.awt.Color

internal object DrawingUtil {
    fun draw(sprite: ISprite, vb: IVertexBuilder, matrix: Matrix4d, x: Float, y: Float, width: Float, height: Float, animFrames: Int, tint: Color) {
        if(sprite.pinTop && sprite.pinBottom && sprite.pinLeft && sprite.pinRight &&
            sprite.minUCap == 0f && sprite.minVCap == 0f && sprite.maxUCap == 0f && sprite.maxVCap == 0f) {
            drawSimple(sprite, vb, matrix, x, y, width, height, animFrames, tint)
        } else {
            drawComplex(sprite, vb, matrix, x, y, width, height, animFrames, tint)
        }
    }

    private fun drawSimple(sprite: ISprite, vb: IVertexBuilder, matrix: Matrix4d, x: Float, y: Float, width: Float, height: Float, animFrames: Int, tint: Color) {
        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val minU = sprite.minU(animFrames)
        val maxU = sprite.maxU(animFrames)
        val minV = sprite.minV(animFrames)
        val maxV = sprite.maxV(animFrames)

        vb.pos2d(matrix, minX, maxY).color(tint).tex(minU, maxV).endVertex()
        vb.pos2d(matrix, maxX, maxY).color(tint).tex(maxU, maxV).endVertex()
        vb.pos2d(matrix, maxX, minY).color(tint).tex(maxU, minV).endVertex()
        vb.pos2d(matrix, minX, minY).color(tint).tex(minU, minV).endVertex()
    }

    private fun drawComplex(sprite: ISprite, vb: IVertexBuilder, matrix: Matrix4d, x: Float, y: Float, width: Float, height: Float, animFrames: Int, tint: Color) {

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

                vb.pos2d(matrix, minX, maxY).color(tint).tex(u0, v0).endVertex()
                vb.pos2d(matrix, maxX, maxY).color(tint).tex(u1, v1).endVertex()
                vb.pos2d(matrix, maxX, minY).color(tint).tex(u2, v2).endVertex()
                vb.pos2d(matrix, minX, minY).color(tint).tex(u3, v3).endVertex()
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
            sections.add(Section(0f, logicalStartCap / factor, 0f, startCap / factor))
            sections.add(Section(logicalStartCap / factor, targetSize, 1 - endCap / factor, 1f))
            return sections
        }

        var pos = 0f
        if(logicalStartCap != 0f) {
            sections.add(Section(0f, logicalStartCap, 0f, startCap))
            pos += logicalStartCap
        }

        if(pinStart == pinEnd) { // both true or both false. Both being false is unclear so it defaults to pinning
            sections.add(Section(logicalStartCap, targetSize - logicalEndCap, startCap, 1 - endCap))
        } else {
            addMiddleSections(sections,
                1 - startCap - endCap,
                logicalSize - logicalStartCap - logicalEndCap,
                targetSize - logicalStartCap - logicalEndCap,
                pos, startCap, pinStart
            )
        }

        if(logicalEndCap != 0f) {
            sections.add(Section(targetSize - logicalEndCap, targetSize, 1 - endCap, 1f))
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
        val tex = minTex

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
