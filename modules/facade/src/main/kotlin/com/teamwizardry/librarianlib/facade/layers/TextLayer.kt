package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.BitfontRenderer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.facade.value.IMValue
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import dev.thecodewarrior.bitfont.utils.Vec2i
import java.awt.Color

open class TextLayer(posX: Int, posY: Int, width: Int, height: Int, text: String): GuiLayer(posX, posY, width, height) {
    constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0, text) {
    }
    constructor(text: String): this(0, 0, text)
    constructor(posX: Int, posY: Int): this(posX, posY, "")
    constructor(): this(0, 0, "")

    var text: AttributedString = AttributedString(text)

    /**
     * The colorPrimary of the text
     */
    val color_im: IMValue<Color> = imValue(Color.BLACK)
    var color: Color by color_im

    private val container: TextContainer = TextContainer()
    private val layoutManager: TextLayoutManager = TextLayoutManager(listOf(Fonts.classic))

    init {
        layoutManager.textContainers.add(container)
        layoutManager.attributedString = this.text
    }

    fun updateText() {
        layoutManager.attributedString = this.text
        container.size = Vec2i(this.widthi, this.heighti)
        layoutManager.layoutText()
    }

    override fun draw(context: GuiDrawContext) {
        BitfontRenderer.draw(context.matrix, container, color)
    }

    //    /**
//     * The text to draw
//     */
//    val text_im: IMValue<String> = IMValue("")
//    /**
//     * The colorPrimary of the text
//     */
//    val color_im: IMValue<Color> = IMValue(Color.BLACK)
//
//    // options that change the layout
//    var text: String by text_im
//    var wrap: Boolean = false
//    var font: Bitfont = Fonts.classic
//    var align: Align2d = Align2d.TOP_LEFT
//    var maxLines: Int = Int.MAX_VALUE
//    var lineSpacing: Int = 0
//    var truncate: Boolean = false
//
//    // visual options
//    var color: Color by color_im
//    var fitToText: Boolean = false
//    var margins: Margins2d = Margins2d(0.0, 0.0, 0.0, 0.0)
//
//    val lineCount: Int get() = typesetString.lines.size
//
//    private var renderer = TypesetStringRenderer()
//    private var typesetString: TypesetString = renderer.typesetString
//    private var lastParams = emptyList<Any>()
//    /**
//     * The bounds of the text pre-truncation.
//     */
//    var fullTextBounds: Rect2d = Rect2d.ZERO
//        private set
//    /**
//     * The bounds of the text post-truncation. If the text was not truncated this is equal to [fullTextBounds]
//     */
//    var textBounds: Rect2d = Rect2d.ZERO
//        private set
//    /**
//     * @see fullTextBounds
//     */
//    val fullTextFrame: Rect2d
//        get() = this.convertRectToParent(fullTextBounds)
//    /**
//     * @see textBounds
//     */
//    val textFrame: Rect2d
//        get() = this.convertRectToParent(textBounds)
//
//    override fun draw(partialTicks: Float) {
//        updateLayout()
//
//        if(fitToText) {
//            if(wrap) {
//                this.size = vec(size.x, fullTextBounds.height + margins.height)
//            } else {
//                this.size = fullTextBounds.size + vec(margins.width, margins.height)
//            }
//        }
//
//        GlStateManager.pushMatrix()
//
//        GlStateManager.translate(margins.left, margins.top, 0.0)
//
//
//        typesetString.lines.forEach { line ->
//            val lineHGap = size.x - margins.width - (line.endX - line.startX)
//            line.offset = when(align.x) {
//                Align2d.X.LEFT -> Vec2i(0, 0)
//                Align2d.X.CENTER -> Vec2i((lineHGap/2).toInt(), 0)
//                Align2d.X.RIGHT -> Vec2i(lineHGap.toInt(), 0)
//            }
//        }
//
//        val contentVGap = size.y - margins.height - textBounds.height
//        when(align.y) {
//            Align2d.Y.TOP -> {}
//            Align2d.Y.CENTER -> GlStateManager.translate(0.0, (contentVGap/2).toInt().toDouble(), 0.0)
//            Align2d.Y.BOTTOM -> GlStateManager.translate(0.0, contentVGap, 0.0)
//        }
//
//        renderer.defaultColor = color
//        renderer.draw()
//
//        GlStateManager.popMatrix()
//    }
//
//    private fun updateLayout() {
//        val contentWidth = max(0, size.xi - margins.width.toInt())
//
//        val newString = text
//        val newParams = listOf(
//            text, font, if(wrap || truncate) contentWidth else -1, lineSpacing
//        )
//        if(newParams == lastParams) return
//        lastParams = newParams
//        val attributedString = AttributedString.fromMC(newString)
//        typesetString = TypesetString(font, attributedString, if(wrap) contentWidth else -1, lineSpacing + 1)
//        fullTextBounds = measureTextBounds()
//        textBounds = fullTextBounds
//
//        if(textBounds !in this.bounds)
//            getTruncatedLength()?.also { truncatedLength ->
//                val truncatedString = AttributedString.fromMC(newString.substring(0, truncatedLength) + "§r…")
//                typesetString = TypesetString(font, truncatedString, if(wrap) contentWidth else -1, lineSpacing + 1)
//                textBounds = measureTextBounds()
//            }
//
//        renderer.typesetString = typesetString
//    }
//
//    private fun measureTextBounds(): Rect2d {
//        if(typesetString.glyphs.isEmpty()) {
//            return rect(margins.left, margins.top, 0, 0)
//        }
//
//        return rect(
//            0, 0,
//            typesetString.lines.map { it.endX }.max() ?: 0,
//            typesetString.lines.last().let { it.baseline+it.maxDescent }
//        )
//    }
//
//    private fun getTruncatedLength(): Int? {
//        if(!truncate || typesetString.lines.isEmpty())
//            return null
//        val ellipses = font.glyphs['…'.toInt()]
//        val trimX = size.x - margins.width - ellipses.calcAdvance(font.spacing)
//        val lastLine = typesetString.lines[min(maxLines, typesetString.lines.size)-1]
//        val lastGlyph = lastLine.glyphs.last { it.posAfter.x < trimX }
//        return lastGlyph.characterIndex+1
//    }
//
//    /**
//     * Fits this layer's [size] to its text. If [wrap] is set this function will not set this layer's width in order to
//     * maintain wrapping behavior on subsequent layouts.
//     */
//    fun fitToText() {
//        updateLayout()
//
//        if(wrap) {
//            this.size = vec(size.x, fullTextBounds.maxY + margins.height)
//        } else {
//            this.size = vec(fullTextBounds.maxX + margins.width, fullTextBounds.maxY + margins.height)
//        }
//    }
//
//    companion object {
//        @JvmStatic
//        @JvmOverloads
//        fun stringSize(text: String, wrap: Int? = null, font: Bitfont = Fonts.classic): Rect2d
//            = stringSize(AttributedString.fromMC(text), wrap, font)
//
//        @JvmStatic
//        @JvmOverloads
//        fun stringSize(text: AttributedString, wrap: Int? = null, font: Bitfont = Fonts.classic): Rect2d {
//            val typesetString = TypesetString(font, text, wrap ?: -1, 1)
//            if(typesetString.glyphs.isEmpty()) {
//                return Rect2d.ZERO
//            }
//
//            val minY = typesetString.lines.first().let { it.baseline-it.maxAscent }
//            val maxY = typesetString.lines.last().let { it.baseline+it.maxDescent }
//            val minX = 0
//            val maxX = typesetString.lines.map { it.endX }.max() ?: 0
//            return rect(minX, minY, maxX-minX, maxY-minY)
//        }
//    }
}
