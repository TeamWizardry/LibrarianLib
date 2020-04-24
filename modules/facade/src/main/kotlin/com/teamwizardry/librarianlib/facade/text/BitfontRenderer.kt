package com.teamwizardry.librarianlib.facade.text

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.core.util.kotlin.tex
import com.teamwizardry.librarianlib.math.Matrix3d
import dev.thecodewarrior.bitfont.typesetting.LineFragment
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import dev.thecodewarrior.bitfont.typesetting.TypesetGlyph
import dev.thecodewarrior.bitfont.typesetting.font
import dev.thecodewarrior.bitfont.utils.Attribute
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Tessellator
import java.awt.Color

object BitfontRenderer {
    fun draw(matrix: Matrix3d, container: TextContainer, defaultColor: Color) {
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        for(line in container.lines) {
            for(glyph in line.glyphs) {
                BitfontAtlas.insert(glyph.glyph.image)
                glyph.attachments?.forEach { BitfontAtlas.insert(it.glyph.image) }
            }
        }

        for (line in container.lines) {
            for(glyph in line.glyphs) {
                draw(matrix, vb, glyph, line.posX + glyph.posX, line.posY + glyph.posY, defaultColor)
                glyph.attachments?.forEach { attachment ->
                    draw(matrix, vb, attachment, line.posX + glyph.posX + attachment.posX, line.posY + glyph.posY + attachment.posY, defaultColor)
                }
            }
        }

        buffer.finish()
    }

    fun draw(matrix: Matrix3d, vb: IVertexBuilder, typesetGlyph: TypesetGlyph, posX: Int, posY: Int, defaultColor: Color) {
        val solid = BitfontAtlas.solidTex()
        val font = typesetGlyph.glyph.font
        val obf = typesetGlyph[Attribute.obfuscated] == true
        val codepoint = if(obf && font != null) font.obfTransform(typesetGlyph.codepoint) else typesetGlyph.codepoint
        val glyph = if(obf && font != null) font.glyphs[codepoint] else typesetGlyph.glyph

        val tex = BitfontAtlas.rectFor(glyph.image)
        var minX = posX + glyph.bearingX
        var minY = posY + glyph.bearingY
        var maxX = minX + glyph.image.width
        var maxY = minY + glyph.image.height
        var minU = tex.x
        var minV = tex.y
        var maxU = tex.x + tex.width
        var maxV = tex.y + tex.height
        val color = typesetGlyph[Attribute.color] ?: defaultColor

        vb.pos2d(matrix, minX, maxY).color(color).tex(minU, maxV).endVertex()
        vb.pos2d(matrix, maxX, maxY).color(color).tex(maxU, maxV).endVertex()
        vb.pos2d(matrix, maxX, minY).color(color).tex(maxU, minV).endVertex()
        vb.pos2d(matrix, minX, minY).color(color).tex(minU, minV).endVertex()

        var underline = typesetGlyph[Attribute.underline]
        if(underline != null && typesetGlyph.codepoint !in newlines) {
            if(underline == Color(0, 0, 0, 0))
                underline = color
            minX = posX-1
            minY = posY+1
            maxX = posX + typesetGlyph.glyph.calcAdvance() + 1
            maxY = posY+2
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