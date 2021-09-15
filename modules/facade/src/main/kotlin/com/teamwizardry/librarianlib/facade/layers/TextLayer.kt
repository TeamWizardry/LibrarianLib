package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.BitfontRenderer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layers.text.TextContainerLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * A managed wrapper around a [TextLayoutContainer]
 */
public open class TextLayer(posX: Int, posY: Int, width: Int, height: Int, text: String):
    GuiLayer(posX, posY, width, height) {
    public constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0, text)

    public constructor(text: String): this(0, 0, text)
    public constructor(posX: Int, posY: Int): this(posX, posY, "")
    public constructor(): this(0, 0, "")

    private val containerLayer = add(TextContainerLayer(0, 0, width, height))

    private val layoutManager: TextLayoutManager = TextLayoutManager(Fonts.classic)
    private var _attributedText: AttributedString = BitfontFormatting.convertMC(text)

    /**
     * The text to be drawn using MC formatting codes. If this is set to any non-null value it will overwrite
     * [attributedText], but if [attributedText] is manually set to a new value, this property will be reset to null.
     */
    public var text_im: IMValue<String?> = imValue()

    /**
     * The text to be drawn using MC formatting codes. If this is set to any non-null value it will overwrite
     * [attributedText], but if [attributedText] is manually set to a new value, this property will be reset to null.
     */
    public var text: String?
        get() = text_im.get()
        set(value) {
            text_im.setValue(value)
            markTextDirty()
        }

    /**
     * The string to be drawn. If [text] is non-null, this will be reset based on that, and if this is explicitly set to
     * a value, [text] will be reset to null.
     *
     * If this is set to a mutable attributed string, the user is responsible for calling [markTextDirty] when it changes.
     */
    public var attributedText: AttributedString
        get() = _attributedText
        set(value) {
            if (_attributedText !== value) {
                text = null
                _attributedText = value
                markTextDirty()
            }
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
     * Margins to offset the text by
     */
    public var textMargins: Margins = Margins(0.0, 0.0, 0.0, 0.0)

    /**
     * The text layout and typesetting options
     */
    public val options: TextLayoutManager.Options = layoutManager.options

    public var maxLines: Int
        get() = containerLayer.container.maxLines
        set(value) {
            if(value != containerLayer.container.maxLines) {
                containerLayer.container.maxLines = value
                markTextDirty()
            }
        }

    /**
     * The horizontal alignment of text within lines.
     */
    public var textAlignment: TextLayoutManager.Alignment
        get() = options.alignment
        set(value) {
            if(value != options.alignment) {
                options.alignment = value
                markTextDirty()
            }
        }

    /**
     * If and how this layer should automatically fit its size to the contained text.
     */
    public var textFitting: TextFit = TextFit.NONE
        set(value) {
            if(field != value) {
                field = value
                markTextDirty()
            }
        }

    /**
     * The logical bounds of the text
     */
    public var textBounds: Rect2d = rect(0, 0, 0, 0)
        private set

    /**
     * Enable truncation with the default "..." truncation string
     */
    public fun enableDefaultTruncation() {
        options.truncationString = AttributedString("...")
    }

    /**
     * Disable truncation in the text layout options
     */
    public fun disableTruncation() {
        options.truncationString = null
    }

    init {
        layoutManager.textContainers.add(containerLayer.container)
        markTextDirty()
    }

    private var isTextDirty: Boolean = false

    public fun markTextDirty() {
        isTextDirty = true
    }

    /**
     * Lay out the text in this layer and set the layer's [size] to the contained text size.
     *
     * @param textFitting if and how to resize this layer based on the text layout
     */
    public fun fitToText(textFitting: TextFit) {
        layoutText(textFitting)
    }

    /**
     * Immediately lays out the text
     */
    public fun layoutText() {
        layoutText(this.textFitting)
    }

    private fun layoutText(textFitting: TextFit) {
        updateMCText()

        containerLayer.pos = vec(textMargins.left, textMargins.top)
        containerLayer.size = this.size - vec(textMargins.horizontalSum, textMargins.verticalSum)
        containerLayer.textFitting = textFitting
        containerLayer.prepareTextContainer()

        layoutManager.attributedString = this.attributedText
        layoutManager.layoutText()

        containerLayer.applyTextLayout()

        textBounds = containerLayer.textBounds.offset(vec(textMargins.left, textMargins.top))

        when(textFitting) {
            TextFit.NONE -> {}
            TextFit.VERTICAL -> {
                height = containerLayer.height + textMargins.verticalSum
            }
            TextFit.HORIZONTAL -> {
                width = containerLayer.width + textMargins.horizontalSum
            }
            TextFit.VERTICAL_SHRINK, TextFit.BOTH -> {
                height = containerLayer.height + textMargins.verticalSum
                width = containerLayer.width + textMargins.horizontalSum
            }
        }

        isTextDirty = false
        lastState = getDirtyState()
    }

    private var lastText: String? = null

    private fun updateMCText() {
        val plainText = text
        if (plainText != null && plainText != lastText) {
            lastText = plainText
            _attributedText = BitfontFormatting.convertMC(plainText)
        }
    }

    private var lastState: List<Any> = emptyList()
    private fun getDirtyState(): List<Any> = listOf(
        options.copy(),
        width,
        height,
        textFitting
    )

    private fun markTextDirtyIfDataChanged() {
        updateMCText()
        if(getDirtyState() != lastState) {
            markTextDirty()
        }
    }

    override fun prepareLayout() {
        super.prepareLayout()

        markTextDirtyIfDataChanged()
        if(isTextDirty) {
            layoutText()
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()
        markTextDirtyIfDataChanged()
        if(isTextDirty) {
            // too late to change the layer size. This also clears the text dirty flag, allowing layout code to assert
            // itself over the fitting, preventing infinite back and forth
            layoutText(TextFit.NONE)
        }
    }

    public data class Margins(val left: Double, val top: Double, val right: Double, val bottom: Double) {
        val horizontalSum: Double = left + right
        val verticalSum: Double = top + bottom
    }
}
