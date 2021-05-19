package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.texture
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import dev.thecodewarrior.bitfont.data.Glyph
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TypesetGlyph
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import java.awt.Color

public object BitfontRenderer {
    @JvmStatic
    public fun draw(matrix: Matrix4d, container: TextContainer, defaultColor: Color) {
        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderLayer)

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

        buffer.draw()

        deferredEmbeds.forEach {
            val color = it.glyph[BitfontFormatting.color] ?: defaultColor
            it.embed.draw(matrix, it.glyph, it.posX, it.posY, color)
        }
    }

    private class DeferredTextEmbed(val glyph: TypesetGlyph, val embed: FacadeTextEmbed, val posX: Int, val posY: Int)

    private fun drawGlyph(matrix: Matrix4d, vb: VertexConsumer, typesetGlyph: TypesetGlyph, textObject: Glyph, posX: Int, posY: Int, defaultColor: Color) {
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

        vb.vertex2d(matrix, minX, maxY).color(color).texture(minU, maxV).next()
        vb.vertex2d(matrix, maxX, maxY).color(color).texture(maxU, maxV).next()
        vb.vertex2d(matrix, maxX, minY).color(color).texture(maxU, minV).next()
        vb.vertex2d(matrix, minX, minY).color(color).texture(minU, minV).next()

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

            vb.vertex2d(matrix, minX, maxY).color(underline).texture(minU, maxV).next()
            vb.vertex2d(matrix, maxX, maxY).color(underline).texture(maxU, maxV).next()
            vb.vertex2d(matrix, maxX, minY).color(underline).texture(maxU, minV).next()
            vb.vertex2d(matrix, minX, minY).color(underline).texture(minU, minV).next()
        }
    }

    private val renderLayer = SimpleRenderLayers.flat(BitfontAtlas.ATLAS_LOCATION)
    private val newlines = intArrayOf(
        '\u000a'.code,
        '\u000b'.code,
        '\u000c'.code,
        '\u000d'.code,
        '\u0085'.code,
        '\u2028'.code,
        '\u2029'.code
    )
}