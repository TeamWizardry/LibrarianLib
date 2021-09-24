package com.teamwizardry.librarianlib.facade.layers

import com.ibm.icu.text.BreakIterator
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.LibLibFacade
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.text.BitfontContainerLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.math.clamp
import dev.thecodewarrior.bitfont.editor.TextEditor
import dev.thecodewarrior.bitfont.typesetting.*
import org.lwjgl.glfw.GLFW.*
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

public class TextInputLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    public val bitfontContainerLayer: BitfontContainerLayer = BitfontContainerLayer(0, 0, width, height)
    private val editor = TextEditor(Fonts.classic, listOf(bitfontContainerLayer.container), null)

    /**
     * The text layout and typesetting options
     */
    public val options: TextLayoutManager.Options = editor.layoutManager.options
    /**
     * The text backing this text input. Deleting, inserting or replacing text in this string should update the cursor
     * position appropriately
     */
    public val text: MutableAttributedString get() = editor.text

    private val cursorLayer = RectLayer(Color.GREEN, 0, 0, 1, 0)


    private val mainCursor = editor.createCursor()
    private val selectionRangeCursor = editor.createCursor()
    private var selectionActive = false
    private val selectionStart get() = min(selectionRangeCursor.index, mainCursor.index)
    private val selectionEnd get() = max(selectionRangeCursor.index, mainCursor.index)

    private var verticalNavigationX: Int? = null
    private var focused: Boolean = true

    private val input = InputLayout.system

    init {
        add(bitfontContainerLayer, cursorLayer)

        editor.layoutManager.delegate = object : TextLayoutDelegate.Wrapper(editor.layoutManager.delegate) {
            override fun textWillLayout() {
                bitfontContainerLayer.size = this@TextInputLayer.size
                bitfontContainerLayer.prepareTextContainer()
                super.textWillLayout()
            }

            override fun textDidLayout() {
                bitfontContainerLayer.applyTextLayout()
                this@TextInputLayer.size = bitfontContainerLayer.size
                super.textDidLayout()
            }
        }
    }

    override fun prepareLayout() {
        if(editor.layoutManager.isStringDirty())
            editor.layoutManager.layoutText()

        val position = mainCursor.position
        if(position == null) {
            cursorLayer.isVisible = false
        } else {
            cursorLayer.isVisible = true
            cursorLayer.pos = vec(position.x - 1, position.y - position.ascent)
            cursorLayer.heighti = position.ascent + position.descent
        }
    }

    public fun layoutText() {
        editor.layoutManager.layoutText()
    }

    public fun setCursor(pos: Int) {
        mainCursor.index = pos
    }

    public fun moveCursor(position: TextEditor.CursorPosition, selecting: Boolean) {
        if(selecting && !selectionActive) {
            selectionRangeCursor.position = mainCursor.position
            selectionActive = true
        }
        mainCursor.position = position
        if(mainCursor.index == selectionRangeCursor.index) {
            selectionActive = false
        }
        verticalNavigationX = null
    }

    public fun moveCursor(pos: Int, selecting: Boolean) {
        if(selecting && !selectionActive) {
            selectionRangeCursor.position = mainCursor.position
            selectionActive = true
        }
        if(pos !in 0 .. text.length) {
            logger.warn("Clamping out-of-bounds cursor position. (${pos} is outside of [0, ${text.length}])")
        }
        mainCursor.index = pos.clamp(0, text.length)
        if(mainCursor.index == selectionRangeCursor.index) {
            selectionActive = false
        }
        verticalNavigationX = null
    }

    @Hook
    private fun click(e: GuiLayerEvents.MouseDown) {
        focused = this.mouseOver
        if(!focused) return
        if(e.button == 0) {
            val line = editor.queryLine(bitfontContainerLayer.container, e.pos.yi) ?: return
            val position = editor.queryColumn(bitfontContainerLayer.container, line, e.pos.xi) ?: return
            moveCursor(position, input.isSelectModifierDown())
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
            this.text.getAttributes(max(0, selectionStart-1))
        } else {
            this.text.getAttributes(max(0, mainCursor.index-1))
        }
        write(AttributedString(text, formatting))
    }

    private fun write(text: AttributedString) {
        if(selectionActive) {
            this.text.replace(selectionStart, selectionEnd, text)
        } else {
            this.text.insert(mainCursor.index, text)
        }
        selectionActive = false
        markLayoutDirty()
    }

    private fun erase(jumpType: InputLayout.JumpType, count: Int) {
        val jumpPosition = getJumpPosition(mainCursor.position ?: return, jumpType, count)
        erase(jumpPosition - mainCursor.index)
    }

    private fun erase(offset: Int) {
        if(selectionActive) {
            this.text.delete(selectionStart, selectionEnd)
            selectionActive = false
        } else {
            val start = min(mainCursor.index, mainCursor.index+offset)
            val end = max(mainCursor.index, mainCursor.index+offset)
            this.text.delete(start, end)
        }
        markLayoutDirty()
    }

    private fun doSelectAll() {
        selectionRangeCursor.index = 0
        setCursor(text.length)
        selectionActive = true
    }

    private fun doSelectNone() {
        selectionActive = false
    }

    private fun doCopy() {
        Client.minecraft.keyboard.clipboard = text.plaintext.substring(selectionStart, selectionEnd)
    }

    private fun doPaste() {
        write(Client.minecraft.keyboard.clipboard)
    }

    private fun doCut() {
        Client.minecraft.keyboard.clipboard = text.plaintext.substring(selectionStart, selectionEnd)
        this.text.delete(selectionStart, selectionEnd)
        selectionActive = false
    }

    private fun doMoveVertically(lines: Int) {
        val position = mainCursor.position
        if(position == null) {
            logger.error("Main cursor position is null. Cursor is at ${mainCursor.index}")
            return
        }
        val targetX = verticalNavigationX ?: position.x
        val destination = position.line + lines
        if(destination < 0) {
            moveCursor(0, input.isSelectModifierDown())
        } else if(destination >= bitfontContainerLayer.container.lines.size) {
            moveCursor(text.length, input.isSelectModifierDown())
        } else {
            moveCursor(
                editor.queryColumn(
                    bitfontContainerLayer.container,
                    bitfontContainerLayer.container.lines[destination],
                    targetX
                ) ?: return,
                input.isSelectModifierDown()
            )
        }
        verticalNavigationX = targetX
    }

    private val characterIterator: BreakIterator = BreakIterator.getCharacterInstance()
    private val wordIterator: BreakIterator = BreakIterator.getWordInstance()

    private fun getJumpPosition(start: TextEditor.CursorPosition, jumpType: InputLayout.JumpType, count: Int): Int {
        val clamped = start.index.clamp(0, text.length)
        when(jumpType) {
            InputLayout.JumpType.CHARACTER -> {
                characterIterator.setText(text.plaintext)
                if(!characterIterator.isBoundary(clamped))
                    characterIterator.preceding(clamped)
                val pos = characterIterator.next(count)
                if(pos == BreakIterator.DONE)
                    return if (count < 0) 0 else text.length
                return pos
            }
            InputLayout.JumpType.WORD -> {
                wordIterator.setText(text.plaintext)
                if(!wordIterator.isBoundary(clamped))
                    wordIterator.preceding(clamped)
                val pos = wordIterator.next(count)
                if(pos == BreakIterator.DONE)
                    return if (count < 0) 0 else text.length
                return pos
            }
            InputLayout.JumpType.LINE -> {
                val position = mainCursor.position ?: return start.index
                val line = bitfontContainerLayer.container.lines.getOrNull(position.line) ?: return start.index
                return if(count < 0) line.startIndex else line.endIndex
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
                    GLFW_KEY_RIGHT -> moveCursor(getJumpPosition(mainCursor.position ?: return, input.jumpType(), 1), input.isSelectModifierDown())
                    GLFW_KEY_LEFT -> moveCursor(getJumpPosition(mainCursor.position ?: return, input.jumpType(), -1), input.isSelectModifierDown())
                    GLFW_KEY_DOWN -> doMoveVertically(1)
                    GLFW_KEY_UP -> doMoveVertically(-1)
                    GLFW_KEY_HOME -> moveCursor(0, input.isSelectModifierDown())
                    GLFW_KEY_END -> moveCursor(text.length, input.isSelectModifierDown())
                    GLFW_KEY_ENTER -> write("\n")
                }
            }
        }
    }

    private companion object {
        private val logger = LibLibFacade.makeLogger<TextInputLayer>()
    }
}