package com.teamwizardry.librarianlib.features.neogui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.copy
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
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
import games.thecodewarrior.bitfont.editor.mode.MacEditorMode
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.TypesetString
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class PastryTextEditor(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
    val editor = Editor(Fonts.classic, size.xi)
    val mode = editor.mode as DefaultEditorMode
    init {
        mode.clipboard = MCClipboard
    }

    val text: AttributedString
        get() = editor.attributedString
    var wrap: Boolean = true
    var color: Color = Color.BLACK

    private var renderer = TypesetStringRenderer()
    private var lastParams = emptyList<Any>()
    private var textBounds: Rect2d = Rect2d.ZERO

    private var lastCursor: Int = -1
    private var lastCursorChange: Long = 0L
    private var selection: IntRange? = null

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
                val start = line.glyphs.firstOrNull { it.characterIndex in selection } ?: return@forEach
                val end = line.glyphs.lastOrNull { it.characterIndex in selection && it.codepoint !in TypesetString.newlineInts } ?: return@forEach
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
        if(selection == null && (System.currentTimeMillis()-lastCursorChange) % (blinkSpeed*2) < blinkSpeed) {
            val font = mode.cursorGlyph?.font ?: Fonts.classic
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

    @Hook
    fun update(e: GuiComponentEvents.ComponentUpdateEvent) {
        updateModifiers()
        if(insertedText.isNotEmpty()) editor.inputText(insertedText)
        insertedText = ""
        editor.update()
    }

    @Hook
    fun keyRepeat(e: GuiComponentEvents.KeyRepeatEvent) {
        updateModifiers()
        if(e.key.toInt() != Keyboard.CHAR_NONE)
            if(e.key !in consumedKeys)
                insertedText += e.key
    }

    @Hook
    fun keyDown(e: GuiComponentEvents.KeyDownEvent) {
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
        editor.inputMouseMove(pos.toBit())
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