package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.provided.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.copy
import com.teamwizardry.librarianlib.features.kotlin.getValue
import com.teamwizardry.librarianlib.features.kotlin.setValue
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.text.Fonts
import com.teamwizardry.librarianlib.features.text.MCClipboard
import com.teamwizardry.librarianlib.features.text.TypesetStringRenderer
import com.teamwizardry.librarianlib.features.text.fromKeyboard
import com.teamwizardry.librarianlib.features.text.fromLwjgl
import com.teamwizardry.librarianlib.features.text.toBit
import com.teamwizardry.librarianlib.features.text.toLL
import games.thecodewarrior.bitfont.editor.Editor
import games.thecodewarrior.bitfont.editor.Key
import games.thecodewarrior.bitfont.editor.Modifiers
import games.thecodewarrior.bitfont.editor.MouseButton
import games.thecodewarrior.bitfont.editor.mode.DefaultEditorMode
import games.thecodewarrior.bitfont.editor.mode.CursorPosition
import games.thecodewarrior.bitfont.editor.mode.CursorRange
import games.thecodewarrior.bitfont.editor.mode.MacEditorMode
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.TypesetString
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

@ExperimentalBitfont
@ExperimentalPastryAPI
class PastryTextEditor(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
    var focused: Boolean = false
    val editor = Editor(Fonts.classic, size.xi)
    val mode = editor.mode as DefaultEditorMode
    init {
        mode.clipboard = MCClipboard
        editor.validate = { string ->
            if(singleLine) {
                val plaintext = string.plaintext
                for(i in string.length - 1 downTo 0) {
                    if(plaintext[i] == '\n' || plaintext[i] == '\r')
                        string.delete(i, i + 1)
                }
            }
            string
        }
    }

    var singleLine: Boolean = false
    var plaintext: String
        get() = attributedText.plaintext
        set(value) {
            attributedText = AttributedString(value)
        }
    var attributedText: AttributedString by editor::attributedString
    var wrap: Boolean = true
    var color: Color = Color.BLACK

    private var renderer = TypesetStringRenderer()
    private var lastParams = emptyList<Any>()
    private var textBounds: Rect2d = Rect2d.ZERO

    private var lastCursor: CursorPosition = CursorPosition(-1, false)
    private var lastCursorChange: Long = 0L
    private var selection: CursorRange? = null

    override fun draw(partialTicks: Float) {
        updateLayout()

        GlStateManager.pushMatrix()
        GlStateManager.translate(-textBounds.x, -textBounds.y, 0.0)

        if(mode.cursor != lastCursor) {
            lastCursor = mode.cursor
            lastCursorChange = System.currentTimeMillis()
        }
        selection = mode.selectionRange

        GlStateManager.disableTexture2D()
        selection?.let { selection ->
            val color = Color.cyan.copy(alpha = 0.5f)
            val vb = Tessellator.getInstance().buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            editor.typesetString.lines.forEach { line ->
                val start = line.glyphs.firstOrNull { it.characterIndex in selection.indexRange } ?: return@forEach
                val end = line.glyphs.lastOrNull { it.characterIndex in selection.indexRange && it.codepoint !in TypesetString.newlineInts } ?: return@forEach
                val min = vec(start.pos.x, line.baseline-line.maxAscent)
                val max = vec(end.posAfter.x, line.baseline+line.maxDescent)

                vb.pos(min.x, min.y, 0).color(color).endVertex()
                vb.pos(min.x, max.y, 0).color(color).endVertex()
                vb.pos(max.x, max.y, 0).color(color).endVertex()
                vb.pos(max.x, min.y, 0).color(color).endVertex()
            }
            Tessellator.getInstance().draw()
        }

        GlStateManager.enableTexture2D()

        renderer.draw()

        GlStateManager.disableTexture2D()

        val blinkSpeed = 500
        if(focused && selection == null && (System.currentTimeMillis()-lastCursorChange) % (blinkSpeed*2) < blinkSpeed) {
            val font = editor.typesetString.glyphMap[mode.cursor.index]?.font ?: Fonts.classic
            val min = mode.cursorPos.toLL() - vec(1, font.ascent)
            val max = mode.cursorPos.toLL() + vec(0, font.descent)

            val vb = Tessellator.getInstance().buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(min.x, min.y, 0).color(color).endVertex()
            vb.pos(min.x, max.y, 0).color(color).endVertex()
            vb.pos(max.x, max.y, 0).color(color).endVertex()
            vb.pos(max.x, min.y, 0).color(color).endVertex()
            Tessellator.getInstance().draw()
        }

        GlStateManager.popMatrix()
    }

    private fun updateLayout() {
        editor.width = if(wrap) size.xi else Int.MAX_VALUE
        val newParams = listOf(
            editor.typesetString
        )
        if(newParams == lastParams) return
        lastParams = newParams

        textBounds = measureTextBounds()
        renderer.typesetString = editor.typesetString
    }

    private var insertedText = ""
    private var modifiers = Modifiers()
        set(value) {
            if(field != value) {
                field = value
                editor.inputModifiers(value)
            }
        }
    private val consumedKeys = mutableSetOf<Char>()

    private fun updateModifiers() {
        modifiers = Modifiers.fromKeyboard()
    }

    override fun update() {
        updateModifiers()
        if(insertedText.isNotEmpty()) editor.inputText(insertedText)
        insertedText = ""
        editor.update()
    }

    @Hook
    fun keyRepeat(e: GuiComponentEvents.KeyRepeatEvent) {
        if(!focused)
            return
        updateModifiers()
        if(e.key.toInt() != Keyboard.CHAR_NONE)
            if(e.key !in consumedKeys)
                insertedText += e.key
    }

    @Hook
    fun keyDown(e: GuiComponentEvents.KeyDownEvent) {
        if(!focused)
            return
        updateModifiers()
        var consumed = false

        if(e.keyCode != Keyboard.KEY_NONE) Key.fromLwjgl(e.keyCode).also {
            consumed = editor.inputKeyDown(it)
        }

        if(e.key.toInt() != Keyboard.CHAR_NONE)
            if(consumed)
                consumedKeys.add(e.key)
            else
                insertedText += e.key
    }

    @Hook
    fun keyUp(e: GuiComponentEvents.KeyUpEvent) {
        updateModifiers()
        if(e.keyCode != Keyboard.KEY_NONE)
            editor.inputKeyUp(Key.fromLwjgl(e.keyCode))
        consumedKeys.remove(e.key)
    }

    @Hook
    fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(!focused)
            return
        updateModifiers()
        editor.inputMouseDown(MouseButton.fromLwjgl(e.button.mouseCode))
    }

    @Hook
    fun mouseUp(e: GuiComponentEvents.MouseUpEvent) {
        updateModifiers()
        editor.inputMouseUp(MouseButton.fromLwjgl(e.button.mouseCode))
    }

    @Hook
    fun mouseMove(e: GuiComponentEvents.MouseMoveEvent) {
        updateModifiers()
        editor.inputMouseMove(mousePos.toBit())
    }

    private fun measureTextBounds(): Rect2d {
        if(editor.typesetString.glyphs.isEmpty()) {
            return Rect2d.ZERO
        }

        val minY = editor.typesetString.lines.first().let { it.baseline-it.maxAscent }
        val maxY = editor.typesetString.lines.last().let { it.baseline+it.maxDescent }
        val minX = 0
        val maxX = editor.typesetString.lines.map { it.endX }.max() ?: 0
        return rect(minX, minY, maxX-minX, maxY-minY)
    }

    companion object {
        init {
            if(Minecraft.IS_RUNNING_ON_MAC)
                DefaultEditorMode.operatingSystemMode = ::MacEditorMode
        }
    }
}