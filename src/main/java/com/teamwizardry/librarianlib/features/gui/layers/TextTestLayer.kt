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
import games.thecodewarrior.bitfont.typesetting.Attribute
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.TypesetString
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.random.Random

class TextTestLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    var text: AttributedString = AttributedString("")
        set(value) {
            field = value
            typesetString = TypesetString(Fonts.MCClassic, value, wrap)
        }
    var wrap: Int = -1
        set(value) {
            field = value
            typesetString = TypesetString(Fonts.MCClassic, text, value)
        }
    var typesetString = TypesetString(Fonts.MCClassic, AttributedString(""))
        set(value) {
            field = value
            val map = mutableMapOf<Bitfont, MutableList<TypesetString.GlyphRender>>()
            value.glyphs.forEach {
                val font = it[Attribute.font] ?: Fonts.MCClassic
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
            val vb = Tessellator.getInstance().buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
            glyphs.forEach {
                val obf = it[Attribute.obfuscated] == true
                val codepoint = if(obf) atlas.obfTransform(it.codepoint) else it.codepoint
                val glyph = if(obf) font.glyphs[codepoint] else it.glyph

                val minX = it.pos.x + glyph.bearingX
                val minY = it.pos.y + glyph.bearingY
                val maxX = minX + glyph.image.width
                val maxY = minY + glyph.image.height
                val tex = atlas.texCoords(codepoint)
                val minU = tex.x
                val minV = tex.y
                val maxU = tex.x + tex.width
                val maxV = tex.y + tex.height

                vb.pos(minX, minY, 0).tex(minU, minV).color(it[Attribute.color] ?: Color.BLACK).endVertex()
                vb.pos(maxX, minY, 0).tex(maxU, minV).color(it[Attribute.color] ?: Color.BLACK).endVertex()
                vb.pos(maxX, maxY, 0).tex(maxU, maxV).color(it[Attribute.color] ?: Color.BLACK).endVertex()
                vb.pos(minX, maxY, 0).tex(minU, maxV).color(it[Attribute.color] ?: Color.BLACK).endVertex()
            }
            Tessellator.getInstance().draw()
        }

        GlStateManager.enableCull()

        GlStateManager.popMatrix()
    }
}