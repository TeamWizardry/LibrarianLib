package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.text.BitfontAtlas
import com.teamwizardry.librarianlib.features.text.Fonts
import com.teamwizardry.librarianlib.features.text.obfuscated
import games.thecodewarrior.bitfont.data.Bitfont
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.TypesetString
import games.thecodewarrior.bitfont.typesetting.color
import games.thecodewarrior.bitfont.typesetting.font
import games.thecodewarrior.bitfont.utils.Attribute
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.random.Random

class TextTestLayer(posX: Int, posY: Int, width: Int, height: Int, val defaultFont: Bitfont = Fonts.MCClassic): GuiLayer(posX, posY, width, height) {
    var text: AttributedString = AttributedString("")
        set(value) {
            field = value
            typesetString = TypesetString(defaultFont, value, wrap)
        }
    var wrap: Int = -1
        set(value) {
            field = value
            typesetString = TypesetString(defaultFont, text, value)
        }
    var typesetString = TypesetString(defaultFont, AttributedString(""))
        set(value) {
            field = value
            val map = mutableMapOf<Bitfont, MutableList<TypesetString.GlyphRender>>()
            value.glyphs.forEach {
                val font = it[Attribute.font] ?: defaultFont
                map.getOrPut(font) { mutableListOf() }.add(it)
            }
            batches = map
        }

    private var batches: Map<Bitfont, List<TypesetString.GlyphRender>> = emptyMap()

    override fun draw(partialTicks: Float) {
        GlStateManager.pushMatrix()

//        when(layout.align.x) {
//            Align2d.X.LEFT -> {}
//            Align2d.X.CENTER -> GlStateManager.translate(size.x/2, 0.0, 0.0)
//            Align2d.X.RIGHT -> GlStateManager.translate(size.x, 0.0, 0.0)
//        }
//        when(layout.align.y) {
//            Align2d.Y.TOP -> {}
//            Align2d.Y.CENTER -> GlStateManager.translate(0.0, ((size.y - layout.bounds.height)/2).toInt() + 1.0, 0.0)
//            Align2d.Y.BOTTOM -> GlStateManager.translate(0.0, size.y - layout.bounds.height, 0.0)
//        }

        GlStateManager.disableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()

        batches.forEach { font, glyphs ->
            val atlas = BitfontAtlas[font]
            atlas.bind()
            val solid = atlas.solidTex()
            val vb = Tessellator.getInstance().buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
            glyphs.forEach {
                val obf = it[Attribute.obfuscated] == true
                val codepoint = if(obf) atlas.obfTransform(it.codepoint) else it.codepoint
                val glyph = if(obf) font.glyphs[codepoint] else it.glyph

                val tex = atlas.texCoords(codepoint)
                var minX = it.pos.x + glyph.bearingX
                var minY = it.pos.y + glyph.bearingY
                var maxX = minX + glyph.image.width
                var maxY = minY + glyph.image.height
                var minU = tex.x
                var minV = tex.y
                var maxU = tex.x + tex.width
                var maxV = tex.y + tex.height
                val color = it[Attribute.color] ?: Color.BLACK

                vb.pos(minX, minY, 0).tex(minU, minV).color(color).endVertex()
                vb.pos(maxX, minY, 0).tex(maxU, minV).color(color).endVertex()
                vb.pos(maxX, maxY, 0).tex(maxU, maxV).color(color).endVertex()
                vb.pos(minX, maxY, 0).tex(minU, maxV).color(color).endVertex()

                var underline = it[Attribute.color]
                if(underline != null) {
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

                    vb.pos(minX, minY, 0).tex(minU, minV).color(color).endVertex()
                    vb.pos(maxX, minY, 0).tex(maxU, minV).color(color).endVertex()
                    vb.pos(maxX, maxY, 0).tex(maxU, maxV).color(color).endVertex()
                    vb.pos(minX, maxY, 0).tex(minU, maxV).color(color).endVertex()
                }
            }
            Tessellator.getInstance().draw()
        }

        GlStateManager.enableCull()

        GlStateManager.popMatrix()
    }
}