package com.teamwizardry.librarianlib.facade.layers

import com.ibm.icu.text.BreakIterator
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.state.DefaultRenderStates
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.state.RenderState
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.LibLibFacade
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.text.BitfontContainerLayer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.math.clamp
import dev.thecodewarrior.bitfont.editor.TextEditor
import dev.thecodewarrior.bitfont.typesetting.*
import org.lwjgl.glfw.GLFW.*
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * @param containers an explicit list of containers, or null to use the default container. If this is null this input
 *   layer's dimensions will be tied to the default container's dimensions. If additional containers are later added
 *   using [addContainer], this link will be broken.
 */
public class TextInputLayer @JvmOverloads constructor(
    posX: Int, posY: Int, width: Int, height: Int,
    containers: List<BitfontContainerLayer>? = null
): GuiLayer(posX, posY, width, height) {
    private val _containerLayers = mutableListOf<BitfontContainerLayer>()
    private val linkLayerSize: Boolean
    private val editor = TextEditor(Fonts.classic, listOf(), null)

    public val cursorColor: Color = Color.GREEN
    public val selectionColor: Color = Color(0f, 1f, 1f, 0.25f)

    /**
     * The attached container layers
     */
    public val containerLayers: List<BitfontContainerLayer> = _containerLayers.unmodifiableView()
    /**
     * The text layout and typesetting options
     */
    public val options: TextLayoutManager.Options = editor.layoutManager.options
    /**
     * The text backing this text input. Deleting, inserting or replacing text in this string should update the cursor
     * position appropriately
     */
    public val text: MutableAttributedString get() = editor.text

    private val mainCursor = editor.createCursor()
    private val selectionRangeCursor = editor.createCursor()
    private var selectionActive = false
    private val selectionStart get() = min(selectionRangeCursor.index, mainCursor.index)
    private val selectionEnd get() = max(selectionRangeCursor.index, mainCursor.index)

    private var verticalNavigationX: Int? = null
    private var focused: Boolean = true

    private val input = InputLayout.system

    init {
        if(containers == null) {
            addContainer(BitfontContainerLayer(0, 0, width, height))
            linkLayerSize = true
        } else {
            containers.forEach { addContainer(it) }
            linkLayerSize = false
        }

        editor.layoutManager.delegate = object : TextLayoutDelegate.Wrapper(editor.layoutManager.delegate) {
            override fun textWillLayout() {
                if(linkLayerSize && containerLayers.size == 1) {
                    containerLayers.single().size = this@TextInputLayer.size
                }
                for(layer in containerLayers) {
                    layer.prepareTextContainer()
                }
                super.textWillLayout()
            }

            override fun textDidLayout() {
                for(layer in containerLayers) {
                    layer.applyTextLayout()
                }
                if(linkLayerSize && containerLayers.size == 1) {
                    this@TextInputLayer.size = containerLayers.single().size
                }
                super.textDidLayout()
            }
        }
    }

    public fun addContainer(layer: BitfontContainerLayer) {
        _containerLayers.add(layer)
        editor.layoutManager.textContainers.add(layer.container)
        layer.add(InputOverlayLayer(layer))
        layer.BUS.hook<GuiLayerEvents.MouseDown> {
            mouseDown(layer, it)
        }
        add(layer)
    }

    override fun prepareLayout() {
        if(editor.layoutManager.isStringDirty())
            editor.layoutManager.layoutText()
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
        }
        selectionActive = selecting
        mainCursor.position = position
        if(mainCursor.index == selectionRangeCursor.index) {
            selectionActive = false
        }
        verticalNavigationX = null
    }

    public fun moveCursor(pos: Int, selecting: Boolean) {
        if(selecting && !selectionActive) {
            selectionRangeCursor.position = mainCursor.position
        }
        selectionActive = selecting
        if(pos !in 0 .. text.length) {
            logger.warn("Clamping out-of-bounds cursor position. (${pos} is outside of [0, ${text.length}])")
        }
        mainCursor.index = pos.clamp(0, text.length)
        if(mainCursor.index == selectionRangeCursor.index) {
            selectionActive = false
        }
        verticalNavigationX = null
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
    }

    private fun erase(jumpType: InputLayout.JumpType, count: Int) {
        if(selectionActive) {
            this.text.delete(selectionStart, selectionEnd)
            selectionActive = false
        } else {
            val jumpPosition = getJumpPosition(mainCursor.position ?: return, jumpType, count)
            erase(jumpPosition - mainCursor.index)
        }
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
        val containers = editor.layoutManager.textContainers
        val targetX = verticalNavigationX ?: position.x
        var containerIndex = containers.indexOf(position.container)
        if(containerIndex == -1) return
        var destination = position.line + lines

        run move@{
            while(destination < 0) {
                if(--containerIndex < 0) {
                    moveCursor(0, input.isSelectModifierDown())
                    return@move
                }
                destination += containers[containerIndex].lines.size
            }
            while(destination >= containers[containerIndex].lines.size) {
                destination -= containers[containerIndex].lines.size
                if(++containerIndex >= containers.size) {
                    moveCursor(text.length, input.isSelectModifierDown())
                    return@move
                }
            }


            moveCursor(
                editor.queryColumn(
                    containers[containerIndex],
                    containers[containerIndex].lines[destination],
                    targetX
                ) ?: return@doMoveVertically,
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
                val line = position.container.lines.getOrNull(position.line) ?: return start.index
                return if(count < 0) line.startIndex else line.endIndex
            }
        }
    }

    private fun mouseDown(containerLayer: BitfontContainerLayer, e: GuiLayerEvents.MouseDown) {
        focused = containerLayers.any { it.mouseOver }
        if(containerLayer.mouseOver && e.button == 0) {
            val line = editor.queryLine(containerLayer.container, e.pos.yi) ?: return
            val position = editor.queryColumn(containerLayer.container, line, e.pos.xi) ?: return
            moveCursor(position, input.isSelectModifierDown())
        }
    }

    @Hook
    private fun charTyped(e: GuiLayerEvents.CharTyped) {
        if(!focused) return
        write(e.codepoint.toString())
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

    private inner class InputOverlayLayer(private val containerLayer: BitfontContainerLayer): GuiLayer() {
        private val state = RenderState.normal.extend().add(DefaultRenderStates.Blend.DEFAULT).build()

        override fun draw(context: GuiDrawContext) {
            state.apply()
            val buffer = FlatColorRenderBuffer.SHARED
            if(selectionActive)
                for(line in containerLayer.container.lines)
                    drawSelection(context, buffer, line, selectionColor)
            drawCursor(context, buffer, mainCursor, cursorColor)
            buffer.draw(Primitive.QUADS)
            state.cleanup()
        }

        private fun drawCursor(
            context: GuiDrawContext,
            buffer: FlatColorRenderBuffer,
            cursor: TextEditor.Cursor,
            color: Color
        ) {
            val position = cursor.position ?: return
            if(position.container !== containerLayer.container) return

            val minX = position.x - 1
            val minY = position.y - position.ascent
            val maxX = minX + 1
            val maxY = position.y + position.descent

            buffer.pos(context.transform, minX, maxY, 0).color(color).endVertex()
            buffer.pos(context.transform, maxX, maxY, 0).color(color).endVertex()
            buffer.pos(context.transform, maxX, minY, 0).color(color).endVertex()
            buffer.pos(context.transform, minX, minY, 0).color(color).endVertex()
        }

        private fun drawSelection(
            context: GuiDrawContext,
            buffer: FlatColorRenderBuffer,
            line: TextContainer.TypesetLine,
            color: Color
        ) {
            if(line.endIndex <= selectionStart || line.startIndex > selectionEnd)
                return

            val minX = line.columnOf(selectionStart.clamp(line.startIndex, line.endIndex))
                ?.let { line.positionAt(it) }
                ?: return
            val maxX = line.columnOf(selectionEnd.clamp(line.startIndex, line.endIndex))
                ?.let { line.positionAt(it) }
                ?: return

            val minY = line.posY
            val maxY = line.posY + line.height

            buffer.pos(context.transform, minX, maxY, 0).color(color).endVertex()
            buffer.pos(context.transform, maxX, maxY, 0).color(color).endVertex()
            buffer.pos(context.transform, maxX, minY, 0).color(color).endVertex()
            buffer.pos(context.transform, minX, minY, 0).color(color).endVertex()
        }
    }

    private companion object {
        private val logger = LibLibFacade.makeLogger<TextInputLayer>()
    }
}