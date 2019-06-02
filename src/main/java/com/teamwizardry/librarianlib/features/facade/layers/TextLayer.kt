package com.teamwizardry.librarianlib.features.facade.layers

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.text.Fonts
import com.teamwizardry.librarianlib.features.text.TypesetStringRenderer
import com.teamwizardry.librarianlib.features.text.fromMC
import games.thecodewarrior.bitfont.data.Bitfont
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.TypesetString
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.min

@ExperimentalBitfont
class TextLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0) {
        this.text = text
        this.fitToText()
    }
    /**
     * The text to draw
     */
    val text_im: IMValue<String> = IMValue("")
    /**
     * The colorPrimary of the text
     */
    val color_im: IMValue<Color> = IMValue(Color.BLACK)

    // options that change the layout
    var text: String by text_im
    var wrap: Boolean = false
    var font: Bitfont = Fonts.classic
    var align: Align2d = Align2d.TOP_LEFT
    var maxLines: Int = Int.MAX_VALUE
    var lineSpacing: Int = 0
    var truncate: Boolean = false

    // visual options
    var color: Color by color_im
    var fitToText: Boolean = false

    val lineCount: Int get() = typesetString.lines.size

    private var renderer = TypesetStringRenderer()
    private var typesetString: TypesetString = renderer.typesetString
    private var lastParams = emptyList<Any>()
    private var fullTextBounds: Rect2d = Rect2d.ZERO
    private var textBounds: Rect2d = Rect2d.ZERO

    override fun draw(partialTicks: Float) {
        updateLayout()

        if(fitToText) {
            if(wrap) {
                this.size = vec(size.x, fullTextBounds.height)
            } else {
                this.size = fullTextBounds.size
            }
        }

        GlStateManager.pushMatrix()

        GlStateManager.translate(-textBounds.x, -textBounds.y, 0.0)

        when(align.x) {
            Align2d.X.LEFT -> {}
            Align2d.X.CENTER -> GlStateManager.translate(((size.x-textBounds.width)/2).toInt().toDouble(), 0.0, 0.0)
            Align2d.X.RIGHT -> GlStateManager.translate(size.x-textBounds.width, 0.0, 0.0)
        }
        when(align.y) {
            Align2d.Y.TOP -> {}
            Align2d.Y.CENTER -> GlStateManager.translate(0.0, ((size.y - textBounds.height)/2).toInt() + 1.0, 0.0)
            Align2d.Y.BOTTOM -> GlStateManager.translate(0.0, size.y - textBounds.height, 0.0)
        }

        renderer.draw()

        GlStateManager.popMatrix()
    }

    private fun updateLayout() {
        val newString = text
        val newParams = listOf(
            text, font, if(wrap || truncate) size.xi else -1, lineSpacing
        )
        if(newParams == lastParams) return
        lastParams = newParams
        val attributedString = AttributedString.fromMC(newString)
        typesetString = TypesetString(font, attributedString, if(wrap) size.xi else -1, lineSpacing + 1)
        fullTextBounds = measureTextBounds()
        textBounds = fullTextBounds

        getTruncatedLength()?.also { truncatedLength ->
            val attributedString = AttributedString.fromMC(newString.substring(0, truncatedLength) + "§r…")
            typesetString = TypesetString(font, attributedString, if(wrap) size.xi else -1, lineSpacing + 1)
            textBounds = measureTextBounds()
        }

        renderer.typesetString = typesetString
    }

    private fun measureTextBounds(): Rect2d {
        if(typesetString.glyphs.isEmpty()) {
            return Rect2d.ZERO
        }

        val minY = typesetString.lines.first().let { it.baseline-it.maxAscent }
        val maxY = typesetString.lines.last().let { it.baseline+it.maxDescent }
        val minX = 0
        val maxX = typesetString.lines.map { it.endX }.max() ?: 0
        return rect(minX, minY, maxX-minX, maxY-minY)
    }

    private fun getTruncatedLength(): Int? {
        if(!truncate || typesetString.lines.isEmpty())
            return null
        val ellipses = font.glyphs['…'.toInt()]
        val lastLine = typesetString.lines[min(maxLines, typesetString.lines.size)-1]
        val lastGlyph = lastLine.glyphs.last { (it.posAfter.x-textBounds.x) < size.x-ellipses.calcAdvance(font.spacing) }
        return lastGlyph.characterIndex+1
    }

    fun fitToText() {
        updateLayout()

        if(wrap) {
            this.size = vec(size.x, fullTextBounds.height)
        } else {
            this.size = fullTextBounds.size
        }
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun stringSize(text: String, wrap: Int? = null, font: Bitfont = Fonts.classic)
            = stringSize(AttributedString.fromMC(text), wrap, font)

        @JvmStatic
        @JvmOverloads
        fun stringSize(text: AttributedString, wrap: Int? = null, font: Bitfont = Fonts.classic): Rect2d {
            val typesetString = TypesetString(font, text, wrap ?: -1, 1)
            if(typesetString.glyphs.isEmpty()) {
                return Rect2d.ZERO
            }

            val minY = typesetString.lines.first().let { it.baseline-it.maxAscent }
            val maxY = typesetString.lines.last().let { it.baseline+it.maxDescent }
            val minX = 0
            val maxX = typesetString.lines.map { it.endX }.max() ?: 0
            return rect(minX, minY, maxX-minX, maxY-minY)
        }
    }
}