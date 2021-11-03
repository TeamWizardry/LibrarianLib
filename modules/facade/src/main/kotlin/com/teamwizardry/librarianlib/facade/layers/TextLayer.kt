package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.facade.layers.text.BitfontContainerLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.MutableAttributedString
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import java.awt.Color

/**
 * A managed wrapper around a [TextLayoutContainer]
 */
public open class TextLayer(posX: Int, posY: Int, width: Int, height: Int, text: String):
    GuiLayer(posX, posY, width, height) {
    public constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0, text)

    public constructor(text: String): this(0, 0, text)
    public constructor(posX: Int, posY: Int): this(posX, posY, "")
    public constructor(): this(0, 0, "")

    /**
     * Fired whenever the text is set, allowing you to modify the actual text that will be rendered.
     */
    public class TextChangedEvent(public val text: MutableAttributedString) : Event()
    /**
     * Fired before laying out text, to allow last-minute modification of the text.
     */
    public class PrepareTextEvent(public var text: AttributedString) : Event()

    public val containerLayer: BitfontContainerLayer = add(BitfontContainerLayer(0, 0, width, height))

    private val layoutManager: TextLayoutManager = TextLayoutManager(Fonts.classic)
    private val _renderText: MutableAttributedString = MutableAttributedString("")
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
     * Whether to enable the text shadow
     */
    public var shadow: Boolean = false

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
    public var textFitting: TextFit
        get() = containerLayer.textFitting
        set(value) {
            if(containerLayer.textFitting != value) {
                containerLayer.textFitting = value
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
        layoutManager.options.leading = 1
        layoutManager.textContainers.add(containerLayer.container)
        containerLayer.color_im.set { this.color }
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
        if(isTextDirty) {
            _renderText.delete(0, _renderText.length)
            _renderText.append(_attributedText)
            BUS.fire(TextChangedEvent(_renderText))
            if(shadow) {
                val shadowAttribute = _renderText.getAllAttributes()[BitfontFormatting.shadow]
                var start = 0
                for(entry in shadowAttribute?.entries.orEmpty()) {
                    if(start < entry.start) {
                        _renderText.setAttribute(start, entry.start, BitfontFormatting.shadow, Color(0, 0, 0, 0))
                    }
                    start = entry.end
                }
                if(start < _renderText.length) {
                    _renderText.setAttribute(start, _renderText.length, BitfontFormatting.shadow, Color(0, 0, 0, 0))
                }
            }
            isTextDirty = false
        }

        containerLayer.pos = vec(textMargins.left, textMargins.top)
        containerLayer.size = this.size - vec(textMargins.horizontalSum, textMargins.verticalSum)
        containerLayer.textFitting = textFitting
        containerLayer.prepareTextContainer()

        layoutManager.attributedString = BUS.fire(PrepareTextEvent(this._renderText)).text
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

        lastState = getDirtyState()
    }

    private var lastText: String? = null

    private fun updateMCText() {
        val plainText = text
        if (plainText != null && plainText != lastText) {
            lastText = plainText
            _attributedText = BitfontFormatting.convertMC(plainText)
            // mark the text dirty, signalling that a TextChangedEvent should be fired
            markTextDirty()
        }
    }

    private var lastState: List<Any> = emptyList()
    private fun getDirtyState(): List<Any> = listOf(
        options.copy(),
        width,
        height,
        textFitting,
        shadow
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
