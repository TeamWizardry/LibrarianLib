package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.text.TextContainerLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.math.clamp
import dev.thecodewarrior.bitfont.typesetting.*
import java.awt.Color
import kotlin.math.max

public class TextInputLayer(posX: Int, posY: Int, width: Int, height: Int, text: String): GuiLayer(posX, posY, width, height) {
    public val containerLayer: TextContainerLayer = TextContainerLayer(0, 0, width, height)
    private val layoutManager = TextLayoutManager(Fonts.classic)

    /**
     * The text layout and typesetting options
     */
    public val options: TextLayoutManager.Options = layoutManager.options

    /**
     * If and how this layer should automatically fit its size to the contained text.
     */
    public var textFitting: TextFit by containerLayer::textFitting

    private var cursorLayer = RectLayer(Color.GREEN, 0, 0, 1, 0)

    private var attributedText: MutableAttributedString = MutableAttributedString(text)
    private var formatting: AttributeMap = AttributeMap()
    private var cursorIndex: Int = 0

    init {
        layoutManager.textContainers.add(containerLayer.container)
        layoutManager.attributedString = attributedText

        add(containerLayer, cursorLayer)

        attributedText.insert(12, "red ", BitfontFormatting.color to Color.RED)
        layoutText()
        setCursor(attributedText.length)
    }

    private fun layoutText() {
        containerLayer.size = this.size
        containerLayer.prepareTextContainer()
        layoutManager.layoutText()
        containerLayer.applyTextLayout()
        this.size = containerLayer.size
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
        if(cursorIndex == attributedText.length) {
            var lastChar = -1
            var last: Pair<TextContainer.TypesetLine, GraphemeCluster>? = null
            for (line in containerLayer.container.lines) {
                for (cluster in line.clusters) {
                    val maxChar = max(cluster.main.characterIndex, cluster.attachments.maxOfOrNull { it.characterIndex } ?: -1)
                    if(maxChar > lastChar) {
                        lastChar = maxChar
                        last = line to cluster
                    }
                }
            }
            if(last != null) {
                cursorLayer.xi = last.first.posX + last.second.main.afterX
                cursorLayer.yi = last.first.posY
                cursorLayer.heighti = last.first.height
            }
        } else {
            for (line in containerLayer.container.lines) {
                for (cluster in line.clusters) {
                    if (cluster.main.characterIndex == cursorIndex ||
                        cluster.attachments.any { it.characterIndex == cursorIndex }
                    ) {
                        cursorLayer.xi = line.posX + cluster.main.posX
                        cursorLayer.yi = line.posY
                        cursorLayer.heighti = line.height
                    }
                }
            }
        }
    }

    @Hook
    private fun click(e: GuiLayerEvents.MouseMove) {

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