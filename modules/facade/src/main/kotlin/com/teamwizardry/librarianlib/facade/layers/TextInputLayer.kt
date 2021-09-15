package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.text.BitfontLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.math.clamp
import dev.thecodewarrior.bitfont.typesetting.*
import java.awt.Color
import kotlin.math.max

public class TextInputLayer(posX: Int, posY: Int, width: Int, height: Int, text: String): GuiLayer(posX, posY, width, height) {
    public val bitfontLayer: BitfontLayer = BitfontLayer(0, 0, width, height)
    private val layoutManager = TextLayoutManager(Fonts.classic)

    /**
     * The text layout and typesetting options
     */
    public val options: TextLayoutManager.Options = layoutManager.options

    /**
     * If and how this layer should automatically fit its size to the contained text.
     */
    public var textFitting: TextFit by bitfontLayer::textFitting

    private var cursorLayer = RectLayer(Color.GREEN, 0, 0, 1, 0)

    private var attributedText: MutableAttributedString = MutableAttributedString(text)
    private var formatting: AttributeMap = AttributeMap()
    private var cursorIndex: Int = 0

    init {
        layoutManager.textContainers.add(bitfontLayer.container)
        layoutManager.attributedString = attributedText

        add(bitfontLayer, cursorLayer)

        attributedText.insert(12, "red ", BitfontFormatting.color to Color.RED)
        layoutText()
        setCursor(attributedText.length)
    }

    private fun layoutText() {
        bitfontLayer.size = this.size
        bitfontLayer.prepareTextContainer()
        layoutManager.layoutText()
        bitfontLayer.applyTextLayout()
        this.size = bitfontLayer.size
        updateCursorPosition()
    }

    public fun setCursor(pos: Int) {
        cursorIndex = pos.clamp(0, attributedText.length)
        formatting = attributedText.getAttributes(max(0, cursorIndex-1))
        updateCursorPosition()
    }

    override fun layoutChildren() {
        super.layoutChildren()
        layoutText()
    }

    private fun updateCursorPosition() {
        val cursorInfo = BitfontLayer.CursorQuery.ByIndex(cursorIndex, true).apply(bitfontLayer.container)
        if(cursorInfo == null) {
            cursorLayer.isVisible = false
        } else {
            cursorLayer.isVisible = true
            cursorLayer.x = cursorInfo.pos.x - 1
            cursorLayer.y = cursorInfo.pos.y - cursorInfo.ascent
            cursorLayer.height = cursorInfo.ascent + cursorInfo.descent
        }
    }

    @Hook
    private fun click(e: GuiLayerEvents.MouseDown) {
        if(e.button == 0) {
            val cursorInfo = BitfontLayer.CursorQuery.ByPosition(convertPointTo(e.pos, bitfontLayer), true).apply(bitfontLayer.container)
            if(cursorInfo != null) {
                if(cursorInfo.outOfBoundsType == BitfontLayer.CursorOutOfBoundsType.POSITION_AFTER_END)
                    setCursor(attributedText.length)
                else
                    setCursor(cursorInfo.clusterStart)
            }
        }
    }

    @Hook
    private fun charTyped(e: GuiLayerEvents.CharTyped) {
        attributedText.insert(cursorIndex, e.codepoint.toString(), formatting)
        cursorIndex++
        markLayoutDirty()
    }

    @Hook
    private fun keyPressed(e: GuiLayerEvents.KeyDown) {
//        if (this.isActive()) {
//            this.selecting = Screen.hasShiftDown()
//            if (Screen.isSelectAll(keyCode)) {
//                this.setCursorToEnd()
//                this.setSelectionEnd(0)
//                true
//            } else if (Screen.isCopy(keyCode)) {
//                MinecraftClient.getInstance().keyboard.clipboard = this.getSelectedText()
//                true
//            } else if (Screen.isPaste(keyCode)) {
//                if (this.editable) {
//                    this.write(MinecraftClient.getInstance().keyboard.clipboard)
//                }
//                true
//            } else if (Screen.isCut(keyCode)) {
//                MinecraftClient.getInstance().keyboard.clipboard = this.getSelectedText()
//                if (this.editable) {
//                    this.write("")
//                }
//                true
//            } else {
//                when (keyCode) {
//                    259 -> {
//                        if (this.editable) {
//                            this.selecting = false
//                            this.erase(-1)
//                            this.selecting = Screen.hasShiftDown()
//                        }
//                        true
//                    }
//                    260, 264, 265, 266, 267 -> false
//                    261 -> {
//                        if (this.editable) {
//                            this.selecting = false
//                            this.erase(1)
//                            this.selecting = Screen.hasShiftDown()
//                        }
//                        true
//                    }
//                    262 -> {
//                        if (Screen.hasControlDown()) {
//                            setCursor(this.getWordSkipPosition(1))
//                        } else {
//                            this.moveCursor(1)
//                        }
//                        true
//                    }
//                    263 -> {
//                        if (Screen.hasControlDown()) {
//                            setCursor(this.getWordSkipPosition(-1))
//                        } else {
//                            this.moveCursor(-1)
//                        }
//                        true
//                    }
//                    268 -> {
//                        this.setCursorToStart()
//                        true
//                    }
//                    269 -> {
//                        this.setCursorToEnd()
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
    }
}