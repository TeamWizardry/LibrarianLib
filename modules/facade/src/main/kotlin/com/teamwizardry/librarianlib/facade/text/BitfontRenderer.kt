package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatTextureRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.state.RenderState
import com.teamwizardry.librarianlib.math.Matrix4d
import dev.thecodewarrior.bitfont.data.Glyph
import dev.thecodewarrior.bitfont.typesetting.PositionedGlyph
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import java.awt.Color
import kotlin.random.Random

public object BitfontRenderer {
    @JvmStatic
    public fun draw(matrix: Matrix4d, container: TextContainer, defaultColor: Color) {
        val buffer = FlatTextureRenderBuffer.SHARED
        for (glyph in container.glyphs) {
            (glyph.textObject as? Glyph)?.also {
                BitfontAtlas.insert(it.image)
            }
        }

        val deferredEmbeds = mutableListOf<DeferredTextEmbed>()
        val seed = Random.nextInt()

        var rng = Random(seed)
        for (glyph in container.glyphs) {
            when(val textObject = glyph.textObject) {
                is Glyph -> {
                    drawGlyph(
                        matrix, buffer, rng,
                        glyph, textObject, glyph.posX + 1, glyph.posY + 1,
                        defaultColor, shadow = true
                    )
                }
            }
        }
        rng = Random(seed)
        for (glyph in container.glyphs) {
            when(val textObject = glyph.textObject) {
                is Glyph -> {
                    drawGlyph(
                        matrix, buffer, rng,
                        glyph, textObject, glyph.posX, glyph.posY,
                        defaultColor, shadow = false
                    )
                }
                is FacadeTextEmbed -> {
                    deferredEmbeds.add(DeferredTextEmbed(glyph, textObject, glyph.posX, glyph.posY))
                }
            }
        }

        RenderState.normal.apply()
        buffer.texture.set(BitfontAtlas.ATLAS_LOCATION)
        buffer.draw(Primitive.QUADS)
        RenderState.normal.cleanup()

        deferredEmbeds.forEach {
            val color = it.glyph[BitfontFormatting.color] ?: defaultColor
            it.embed.draw(matrix, it.glyph, it.posX, it.posY, color)
        }
    }

    private class DeferredTextEmbed(val glyph: PositionedGlyph, val embed: FacadeTextEmbed, val posX: Int, val posY: Int)

    private fun drawGlyph(
        matrix: Matrix4d, buffer: FlatTextureRenderBuffer, rng: Random,
        positionedGlyph: PositionedGlyph, textObject: Glyph, posX: Int, posY: Int,
        defaultColor: Color, shadow: Boolean
    ) {
        val solid = BitfontAtlas.solidTex()
        val obf = positionedGlyph[BitfontFormatting.obfuscated] == true
        val codepoint = if (obf) ObfTransform.transform(textObject.font, rng, positionedGlyph.codepoint) else positionedGlyph.codepoint
        val glyph = if (obf) textObject.font.glyphs[codepoint] else textObject

        val tex = BitfontAtlas.rectFor(glyph.image)
        var minX = posX + glyph.bearingX
        var minY = posY + glyph.bearingY
        var maxX = minX + glyph.image.width
        var maxY = minY + glyph.image.height
        var minU = tex.x
        var minV = tex.y
        var maxU = tex.x + tex.width
        var maxV = tex.y + tex.height
        var color = positionedGlyph[BitfontFormatting.color] ?: defaultColor

        if(shadow) {
            val shadowColor = positionedGlyph[BitfontFormatting.shadow] ?: return
            color = if(shadowColor == Color(0, 0, 0, 0)) {
                getShadowColor(color)
            } else {
                shadowColor
            }
        }

        buffer.pos(matrix, minX, maxY, 0).color(color).tex(minU, maxV).endVertex()
        buffer.pos(matrix, maxX, maxY, 0).color(color).tex(maxU, maxV).endVertex()
        buffer.pos(matrix, maxX, minY, 0).color(color).tex(maxU, minV).endVertex()
        buffer.pos(matrix, minX, minY, 0).color(color).tex(minU, minV).endVertex()

        var underline = positionedGlyph[BitfontFormatting.underline]
        if (underline != null && positionedGlyph.codepoint !in newlines) {
            if (underline == Color(0, 0, 0, 0)) {
                underline = color
            }
            minX = posX - 1
            minY = posY + 1
            maxX = posX + textObject.advance + 1
            maxY = posY + 2
            minU = solid.x
            minV = solid.y
            maxU = solid.x + solid.width
            maxV = solid.y + solid.height

            buffer.pos(matrix, minX, maxY, 0).color(underline).tex(minU, maxV).endVertex()
            buffer.pos(matrix, maxX, maxY, 0).color(underline).tex(maxU, maxV).endVertex()
            buffer.pos(matrix, maxX, minY, 0).color(underline).tex(maxU, minV).endVertex()
            buffer.pos(matrix, minX, minY, 0).color(underline).tex(minU, minV).endVertex()
        }
    }

    public val newlines: IntArray = intArrayOf(
        '\u000a'.code,
        '\u000b'.code,
        '\u000c'.code,
        '\u000d'.code,
        '\u0085'.code,
        '\u2028'.code,
        '\u2029'.code
    )

    public fun getShadowColor(color: Color): Color {
        // the vanilla shadow colors are just `floor(component / 4)`
        return Color(color.red / 4, color.green / 4, color.blue / 4, color.alpha)
    }
}