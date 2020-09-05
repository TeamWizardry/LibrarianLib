package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.BitfontRenderer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.facade.text.fromMC
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.rect
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import dev.thecodewarrior.bitfont.utils.Vec2i
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

public open class TextLayer(posX: Int, posY: Int, width: Int, height: Int, text: String): GuiLayer(posX, posY, width, height) {
    public constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0, text) {
    }
    public constructor(text: String): this(0, 0, text)
    public constructor(posX: Int, posY: Int): this(posX, posY, "")
    public constructor(): this(0, 0, "")

    /**
     * The text to be drawn using MC formatting codes. If this is set to any non-null value it will overwrite
     * [attributedText], but if [attributedText] is manually set to a new value, this property will be reset to null.
     */
    public var text_im: IMValue<String?> = imValue()

    /**
     * The text to be drawn using MC formatting codes. If this is set to any non-null value it will overwrite
     * [attributedText], but if [attributedText] is manually set to a new value, this property will be reset to null.
     */
    public var text: String? by text_im
    private var lastText: String? = null

    private var _attributedText: AttributedString = AttributedString.fromMC(text)
    /**
     * The string to be drawn. if [text]
     * If this is set to a mutable attributed string, the user is responsible for calling
     * [updateText] when it changes. If this is set to an immutable attributed string, it will automatically be called.
     */
    public var attributedText: AttributedString
        get() = _attributedText
        set(value) {
            if(_attributedText !== value)
                text = null
            _attributedText = value
        }

    /**
     * The color of the text. (can be overridden by color attributes in the string)
     */
    public val color_im: IMValue<Color> = imValue(Color.BLACK)

    /**
     * The color of the text. (can be overridden by color attributes in the string)
     */
    public var color: Color by color_im

    /**
     * Whether to wrap the text
     */
    public var wrap: Boolean = false

    /**
     * Margins to offset the text by
     */
    public var textMargins: Margins = Margins(0.0, 0.0, 0.0, 0.0)

    /**
     * The logical bounds of the text
     */
    public var textBounds: Rect2d = rect(0, 0, 0, 0)
        private set

    private val container: TextContainer = TextContainer()
    private val layoutManager: TextLayoutManager = TextLayoutManager(listOf(Fonts.classic))

    init {
        layoutManager.textContainers.add(container)
        layoutManager.attributedString = this.attributedText
        updateText()
    }

    public fun fitToText() {
        updateText()
        var minX = 0
        var minY = 0
        var maxX = 0
        var maxY = 0
        container.lines.forEach { fragment ->
            if(fragment.glyphs.isEmpty())
                return@forEach
            fragment.glyphs.forEach { glyph ->
                minX = min(minX, glyph.posX)
                maxX = max(maxX, glyph.posX + glyph.glyph.calcAdvance())
            }
            minY = min(minY, fragment.posY)
            maxY = max(maxY, fragment.maxY)
        }
        textBounds = rect(minX, minY, maxX, maxY)
        size = vec(maxX + textMargins.horizontalSum, maxY + textMargins.verticalSum)
    }

    /**
     * Updates the text layout.
     */
    public fun updateText() {
        val plainText = text
        if(plainText != null && plainText != lastText) {
            lastText = plainText
            _attributedText = AttributedString.fromMC(plainText)
        }

        layoutManager.attributedString = this.attributedText
        if(wrap)
            container.size = Vec2i((this.width - textMargins.horizontalSum).toInt(), Int.MAX_VALUE)
        else
            container.size = Vec2i(Int.MAX_VALUE, Int.MAX_VALUE)
        layoutManager.layoutText()
    }

    override fun prepareLayout() {
        super.prepareLayout()
        updateText()
    }

    override fun draw(context: GuiDrawContext) {
        context.matrix.translate(textMargins.left, textMargins.top)
        BitfontRenderer.draw(context.matrix, container, color)
    }

    public data class Margins(val left: Double, val top: Double, val right: Double, val bottom: Double) {
        val horizontalSum: Double = left + right
        val verticalSum: Double = top + bottom
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
