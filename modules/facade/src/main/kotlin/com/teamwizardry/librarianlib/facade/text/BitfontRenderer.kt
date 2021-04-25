package com.teamwizardry.librarianlib.facade.text

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderTypes
import com.teamwizardry.librarianlib.math.Matrix4d
import dev.thecodewarrior.bitfont.data.Glyph
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TypesetGlyph
import net.minecraft.client.renderer.IRenderTypeBuffer
import java.awt.Color

public object BitfontRenderer {
    @JvmStatic
    public fun draw(matrix: Matrix4d, container: TextContainer, defaultColor: Color) {
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        for (line in container.lines) {
            for (glyph in line) {
                (glyph.textObject as? Glyph)?.also {
                    BitfontAtlas.insert(it.image)
                }
            }
        }

        val deferredEmbeds = mutableListOf<DeferredTextEmbed>()

        for (line in container.lines) {
            for (glyph in line) {
                when(val textObject = glyph.textObject) {
                    is Glyph -> {
                        drawGlyph(matrix, vb, glyph, textObject, line.posX + glyph.posX, line.posY + glyph.posY, defaultColor)
                    }
                    is FacadeTextEmbed -> {
                        deferredEmbeds.add(DeferredTextEmbed(glyph, textObject, line.posX + glyph.posX, line.posY + glyph.posY))
                    }
                }
            }
        }

        buffer.finish()

        deferredEmbeds.forEach {
            val color = it.glyph[BitfontFormatting.color] ?: defaultColor
            it.embed.draw(matrix, it.glyph, it.posX, it.posY, color)
        }
    }

    private class DeferredTextEmbed(val glyph: TypesetGlyph, val embed: FacadeTextEmbed, val posX: Int, val posY: Int)

    private fun drawGlyph(matrix: Matrix4d, vb: IVertexBuilder, typesetGlyph: TypesetGlyph, textObject: Glyph, posX: Int, posY: Int, defaultColor: Color) {
        val solid = BitfontAtlas.solidTex()
        val font = textObject.font
        val obf = typesetGlyph[BitfontFormatting.obfuscated] == true
        val codepoint = if (obf) ObfTransform.transform(font, typesetGlyph.codepoint) else typesetGlyph.codepoint
        val glyph = if (obf) font.glyphs[codepoint] else textObject

        val tex = BitfontAtlas.rectFor(glyph.image)
        var minX = posX + glyph.bearingX
        var minY = posY + glyph.bearingY
        var maxX = minX + glyph.image.width
        var maxY = minY + glyph.image.height
        var minU = tex.x
        var minV = tex.y
        var maxU = tex.x + tex.width
        var maxV = tex.y + tex.height
        val color = typesetGlyph[BitfontFormatting.color] ?: defaultColor

        vb.pos2d(matrix, minX, maxY).color(color).tex(minU, maxV).endVertex()
        vb.pos2d(matrix, maxX, maxY).color(color).tex(maxU, maxV).endVertex()
        vb.pos2d(matrix, maxX, minY).color(color).tex(maxU, minV).endVertex()
        vb.pos2d(matrix, minX, minY).color(color).tex(minU, minV).endVertex()

        var underline = typesetGlyph[BitfontFormatting.underline]
        if (underline != null && typesetGlyph.codepoint !in newlines) {
            if (underline == Color(0, 0, 0, 0))
                underline = color
            minX = posX - 1
            minY = posY + 1
            maxX = posX + textObject.advance + 1
            maxY = posY + 2
            minU = solid.x
            minV = solid.y
            maxU = solid.x + solid.width
            maxV = solid.y + solid.height

            vb.pos2d(matrix, minX, maxY).color(underline).tex(minU, maxV).endVertex()
            vb.pos2d(matrix, maxX, maxY).color(underline).tex(maxU, maxV).endVertex()
            vb.pos2d(matrix, maxX, minY).color(underline).tex(maxU, minV).endVertex()
            vb.pos2d(matrix, minX, minY).color(underline).tex(minU, minV).endVertex()
        }
    }

    private val renderType = SimpleRenderTypes.flat(BitfontAtlas.ATLAS_LOCATION)
    private val newlines = intArrayOf(
        '\u000a'.toInt(),
        '\u000b'.toInt(),
        '\u000c'.toInt(),
        '\u000d'.toInt(),
        '\u0085'.toInt(),
        '\u2028'.toInt(),
        '\u2029'.toInt()
    )
}