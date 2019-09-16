package com.teamwizardry.librarianlib.features.text

import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.kotlin.color
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.TypesetString
import games.thecodewarrior.bitfont.utils.Attribute
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

@ExperimentalBitfont
class TypesetStringRenderer {
    var typesetString = TypesetString(Fonts.classic, AttributedString(""))
    var defaultColor: Color = Color.BLACK

    fun draw() {
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()

        BitfontAtlas.bind()
        val solid = BitfontAtlas.solidTex()
        val vb = Tessellator.getInstance().buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        typesetString.glyphs.forEach {
            val obf = it.attributes[Attribute.obfuscated] == true
            val codepoint = if(obf) it.font.obfTransform(it.codepoint) else it.codepoint
            val glyph = if(obf) it.font.glyphs[codepoint] else it.glyph

            val tex = BitfontAtlas.rectFor(glyph.image)
            var minX = it.pos.x + glyph.bearingX
            var minY = it.pos.y + glyph.bearingY
            var maxX = minX + glyph.image.width
            var maxY = minY + glyph.image.height
            var minU = tex.x
            var minV = tex.y
            var maxU = tex.x + tex.width
            var maxV = tex.y + tex.height
            val color = it.attributes[Attribute.color] ?: defaultColor

            vb.pos(minX, minY, 0).tex(minU, minV).color(color).endVertex()
            vb.pos(maxX, minY, 0).tex(maxU, minV).color(color).endVertex()
            vb.pos(maxX, maxY, 0).tex(maxU, maxV).color(color).endVertex()
            vb.pos(minX, maxY, 0).tex(minU, maxV).color(color).endVertex()

            var underline = it.attributes[Attribute.underline]
            if(underline != null && it.codepoint !in TypesetString.newlineInts) {
                if(underline == Color(0, 0, 0, 0))
                    underline = color
                minX = it.pos.x-1
                minY = it.pos.y+1
                maxX = it.posAfter.x+1
                maxY = it.pos.y+2
                minU = solid.x
                minV = solid.y
                maxU = solid.x + solid.width
                maxV = solid.y + solid.height

                vb.pos(minX, minY, 0).tex(minU, minV).color(underline).endVertex()
                vb.pos(maxX, minY, 0).tex(maxU, minV).color(underline).endVertex()
                vb.pos(maxX, maxY, 0).tex(maxU, maxV).color(underline).endVertex()
                vb.pos(minX, maxY, 0).tex(minU, maxV).color(underline).endVertex()
            }
        }
        Tessellator.getInstance().draw()

        GlStateManager.enableCull()

        GlStateManager.popMatrix()
    }
}