package com.teamwizardry.librarianlib.facade.layers

import com.ibm.icu.text.BreakIterator
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.LibLibFacade
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.text.BitfontLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.math.clamp
import dev.thecodewarrior.bitfont.typesetting.*
import org.lwjgl.glfw.GLFW.*
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

public class TextInputLayer(posX: Int, posY: Int, width: Int, height: Int, text: String): GuiLayer(posX, posY, width, height) {
    public val bitfontLayer: BitfontLayer = BitfontLayer(0, 0, width, height)
    private val layoutManager = TextLayoutManager(Fonts.classic)
    private val input = InputLayout.system

    /**
     * The text layout and typesetting options
     */
    public val options: TextLayoutManager.Options = layoutManager.options

    /**
     * If and how this layer should automatically fit its size to the contained text.
     */
    public var textFitting: TextFit by bitfontLayer::textFitting

    private val cursorLayer = RectLayer(Color.GREEN, 0, 0, 1, 0)

    public val attributedText: MutableAttributedString = MutableAttributedString(text)

    private var cursorMarker = object : MutableAttributedString.Marker(0) {
        override fun moved() {
            updateCursorPosition()
        }
    }
    private var selectionRangeMarker = MutableAttributedString.Marker(0)
    private var selectionActive = false
    private val selectionStart get() = min(selectionRangeMarker.position, cursorMarker.position)
    private val selectionEnd get() = max(selectionRangeMarker.position, cursorMarker.position)

    private var verticalNavigationX: Double? = null
    private var focused: Boolean = true

    init {
        attributedText.registerMarker(cursorMarker)
        attributedText.registerMarker(selectionRangeMarker)
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
        cursorMarker.position = pos.clamp(0, attributedText.length)
    }

    public fun moveCursor(pos: Int, selecting: Boolean) {
        if(selecting && !selectionActive) {
            selectionRangeMarker.position = cursorMarker.position
            selectionActive = true
        }
        if(pos !in 0 .. attributedText.length) {
            logger.warn("Clamping out-of-bounds cursor position. (${pos} is outside of [0, ${attributedText.length}])")
        }
        cursorMarker.position = pos.clamp(0, attributedText.length)
        if(cursorMarker.position == selectionRangeMarker.position) {
            selectionActive = false
        }
        verticalNavigationX = null
    }

    override fun layoutChildren() {
        super.layoutChildren()
        layoutText()
    }

    private fun updateCursorPosition() {
        val cursorInfo = BitfontLayer.CursorQuery.ByIndex(cursorMarker.position, true).apply(bitfontLayer.container)
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
        focused = this.mouseOver
        if(!focused) return
        if(e.button == 0) {
            val cursorInfo = BitfontLayer.CursorQuery.ByPosition(convertPointTo(e.pos, bitfontLayer), true).apply(bitfontLayer.container)
            if(cursorInfo != null) {
                if(cursorInfo.outOfBoundsType == BitfontLayer.CursorOutOfBoundsType.POSITION_AFTER_END)
                    moveCursor(attributedText.length, input.isSelectModifierDown())
                else
                    moveCursor(cursorInfo.clusterStart, input.isSelectModifierDown())
            }
        }
    }

    @Hook
    private fun charTyped(e: GuiLayerEvents.CharTyped) {
        if(!focused) return
        write(e.codepoint.toString())
        markLayoutDirty()
    }

    private fun write(text: String) {
        val formatting = if(selectionActive) {
            attributedText.getAttributes(max(0, selectionStart-1))
        } else {
            attributedText.getAttributes(max(0, cursorMarker.position-1))
        }
        write(AttributedString(text, formatting))
    }

    private fun write(text: AttributedString) {
        if(selectionActive) {
            this.attributedText.delete(selectionStart, selectionEnd)
        }
        this.attributedText.insert(cursorMarker.position, text)
        selectionActive = false
        markLayoutDirty()
    }

    private fun erase(jumpType: InputLayout.JumpType, count: Int) {
        val jumpPosition = getJumpPosition(cursorMarker.position, jumpType, count)
        erase(jumpPosition - cursorMarker.position)
    }

    private fun erase(offset: Int) {
        if(selectionActive) {
            this.attributedText.delete(selectionStart, selectionEnd)
            selectionActive = false
        } else {
            val start = min(cursorMarker.position, cursorMarker.position+offset)
            val end = max(cursorMarker.position, cursorMarker.position+offset)
            this.attributedText.delete(start, end)
        }
        markLayoutDirty()
    }

    private fun doSelectAll() {
        this.selectionRangeMarker.position = 0
        setCursor(attributedText.length)
        selectionActive = true
    }

    private fun doSelectNone() {
        selectionActive = false
    }

    private fun doCopy() {
        Client.minecraft.keyboard.clipboard = attributedText.plaintext.substring(selectionStart, selectionEnd)
    }

    private fun doPaste() {
        write(Client.minecraft.keyboard.clipboard)
    }

    private fun doCut() {
        Client.minecraft.keyboard.clipboard = attributedText.plaintext.substring(selectionStart, selectionEnd)
        this.attributedText.delete(selectionStart, selectionEnd)
        selectionActive = false
    }

    private fun doMoveVertically(lines: Int) {
        val current = BitfontLayer.CursorQuery.ByIndex(cursorMarker.position, true)
            .apply(bitfontLayer.container) ?: return
        val targetX = verticalNavigationX ?: current.pos.x
        val destination = current.line + lines
        if(destination < 0) {
            moveCursor(0, input.isSelectModifierDown())
        } else if(current.line >= bitfontLayer.container.lines.size) {
            moveCursor(attributedText.length, input.isSelectModifierDown())
        } else {
            val targetInfo = BitfontLayer.CursorQuery.ByPosition(vec(targetX, 0), true, destination)
                .apply(bitfontLayer.container) ?: return
            if(targetInfo.outOfBoundsType == BitfontLayer.CursorOutOfBoundsType.POSITION_AFTER_END)
                moveCursor(attributedText.length, input.isSelectModifierDown())
            else
                moveCursor(targetInfo.clusterStart, input.isSelectModifierDown())
        }
        verticalNavigationX = targetX
    }

    private val characterIterator: BreakIterator = BreakIterator.getCharacterInstance()
    private val wordIterator: BreakIterator = BreakIterator.getWordInstance()

    private fun getJumpPosition(start: Int, jumpType: InputLayout.JumpType, count: Int): Int {
        val clamped = start.clamp(0, attributedText.length)
        when(jumpType) {
            InputLayout.JumpType.CHARACTER -> {
                characterIterator.setText(attributedText.plaintext)
                if(!characterIterator.isBoundary(clamped))
                    characterIterator.preceding(clamped)
                val pos = characterIterator.next(count)
                if(pos == BreakIterator.DONE)
                    return if (count < 0) 0 else attributedText.length
                return pos
            }
            InputLayout.JumpType.WORD -> {
                wordIterator.setText(attributedText.plaintext)
                if(!wordIterator.isBoundary(clamped))
                    wordIterator.preceding(clamped)
                val pos = wordIterator.next(count)
                if(pos == BreakIterator.DONE)
                    return if (count < 0) 0 else attributedText.length
                return pos
            }
            InputLayout.JumpType.LINE -> {
                val line = BitfontLayer.CursorQuery.ByIndex(cursorMarker.position, true)
                    .apply(bitfontLayer.container)?.line ?: return start
                val cursorInfo = BitfontLayer.CursorQuery.ByPosition(vec(Double.POSITIVE_INFINITY * count, 0), true, line)
                    .apply(bitfontLayer.container) ?: return start
                if(cursorInfo.outOfBoundsType == BitfontLayer.CursorOutOfBoundsType.POSITION_AFTER_END)
                    return attributedText.length
                return cursorInfo.clusterStart
            }
        }
    }

    @Hook
    private fun keyPressed(e: GuiLayerEvents.KeyDown) {
        if(!focused) return
        layoutText()

        when {
            input.isSelectAll(e.keyCode) -> doSelectAll()
            input.isSelectNone(e.keyCode) -> doSelectNone()
            input.isCopy(e.keyCode) && selectionActive -> doCopy()
            input.isPaste(e.keyCode) -> doPaste()
            input.isCut(e.keyCode) && selectionActive -> doCut()
            else -> {
                when (e.keyCode) {
                    GLFW_KEY_INSERT, GLFW_KEY_PAGE_UP, GLFW_KEY_PAGE_DOWN -> {}
                    GLFW_KEY_BACKSPACE -> erase(input.jumpType(), -1)
                    GLFW_KEY_DELETE -> erase(input.jumpType(), 1)
                    GLFW_KEY_RIGHT -> moveCursor(getJumpPosition(cursorMarker.position, input.jumpType(), 1), input.isSelectModifierDown())
                    GLFW_KEY_LEFT -> moveCursor(getJumpPosition(cursorMarker.position, input.jumpType(), -1), input.isSelectModifierDown())
                    GLFW_KEY_DOWN -> doMoveVertically(1)
                    GLFW_KEY_UP -> doMoveVertically(-1)
                    GLFW_KEY_HOME -> moveCursor(0, input.isSelectModifierDown())
                    GLFW_KEY_END -> moveCursor(attributedText.length, input.isSelectModifierDown())
                    GLFW_KEY_ENTER -> write("\n")
                }
            }
        }
    }

    private companion object {
        private val logger = LibLibFacade.makeLogger<TextInputLayer>()
    }
}