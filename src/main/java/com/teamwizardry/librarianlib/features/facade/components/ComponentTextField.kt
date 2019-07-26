package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.facade.value.IMValueBoolean
import com.teamwizardry.librarianlib.features.facade.value.IMValueInt
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

@SideOnly(Side.CLIENT)
open class ComponentTextField(private val fontRenderer: FontRenderer, x: Int, y: Int, width: Int, height: Int) : GuiComponent(x, y, width, height) {

    constructor(x: Int, y: Int, width: Int, height: Int) : this(Minecraft.getMinecraft().fontRenderer, x, y, width, height)

    private var cursorCounter: Int = 0
    private var lineScrollOffset: Int = 0

    var text = ""
        set(value) {
            val max = maxStringLength
            val trimmed = if (value.length > max)
                value.substring(0, max)
            else value
            val result = if (filter != null) filter?.invoke(trimmed) else trimmed
            if (result != null) field = result

            if (text.isEmpty())
                cursorToEnd()
        }

    val maxStringLength_im: IMValueInt = IMValueInt(100)
    val canLoseFocus_im: IMValueBoolean = IMValueBoolean(true)
    val autoFocus_im: IMValueBoolean = IMValueBoolean(false)
    val useShadow_im: IMValueBoolean = IMValueBoolean(true)
    val useVanillaFilter_im: IMValueBoolean = IMValueBoolean(true)
    val useUnderscoreCursor_im: IMValueBoolean = IMValueBoolean(true)

    var maxStringLength: Int by maxStringLength_im
    var canLoseFocus: Boolean by canLoseFocus_im
    var autoFocus: Boolean by autoFocus_im
    var useShadow: Boolean by useShadow_im
    var useVanillaFilter: Boolean by useVanillaFilter_im
    var useUnderscoreCursor: Boolean by useUnderscoreCursor_im

    /**
     * Callback to filter input.
     * Argument is the trimmed down String (according to [maxStringLength]).
     * Output is the resulting String, or null to deny the change.
     */
    var filter: ((String) -> String?)? = null

    var isEnabled = true
    var cursorPosition: Int = 0
        set(pos) {
            field = MathHelper.clamp(pos, 0, this.text.length)
            this.setSelectionPosition(this.cursorPosition)
        }
    val enabledColor_im: IMValue<Color> = IMValue(Color(0xe0e0e0))
    val disabledColor_im: IMValue<Color> = IMValue(Color(0x707070))
    val selectionColor_im: IMValue<Color> = IMValue(Color(0x0000ff))
    val cursorColor_im: IMValue<Color> = IMValue(Color(0xd0d0d0))

    var enabledColor: Color by enabledColor_im
    var disabledColor: Color by disabledColor_im
    var selectionColor: Color by selectionColor_im
    var cursorColor: Color by cursorColor_im


    var selectionEnd: Int = 0
        private set

    val selectedText: String
        get() {
            val start = if (this.cursorPosition < this.selectionEnd) this.cursorPosition else this.selectionEnd
            val end = if (this.cursorPosition < this.selectionEnd) this.selectionEnd else this.cursorPosition
            return this.text.substring(start, end)
        }

    init {
        this.cursor = LibCursor.TEXT
        BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { mouseClicked(mousePos.xi, mousePos.yi, it.button.mouseCode) }
        BUS.hook(GuiComponentEvents.KeyDownEvent::class.java) {
            if (handleKeyTyped(it.key, it.keyCode))
                requestFocus()
        }
        BUS.hook(GuiLayerEvents.Tick::class.java) { updateCursorCounter() }
    }

    override fun draw(partialTicks: Float) {
        drawTextBox()
    }

    fun updateCursorCounter() {
        ++this.cursorCounter
    }

    fun writeText(textToWrite: String) {

        val allowed = if (useVanillaFilter) ChatAllowedCharacters.filterAllowedCharacters(textToWrite)  else textToWrite
        val max = maxStringLength

        val selectionStart = if (this.cursorPosition < this.selectionEnd) this.cursorPosition else this.selectionEnd
        val selectionEnd = if (this.cursorPosition < this.selectionEnd) this.selectionEnd else this.cursorPosition
        val remainingSpace = max - this.text.length - (selectionStart - selectionEnd)

        var build = if (this.text.isEmpty()) "" else this.text.substring(0, selectionStart)


        val fakeBuildStart = build

        val set: String

        set = if (remainingSpace < allowed.length)
            allowed.substring(0, remainingSpace)
        else
            allowed

        build += set

        var fakeBuildEnd = ""
        if (!this.text.isEmpty() && selectionEnd < this.text.length) {
            fakeBuildEnd = this.text.substring(selectionEnd)
            build += fakeBuildEnd
        }

        val editEvent = BUS.fire(PreTextEditEvent(set, build))
        if (!editEvent.isCanceled()) {
            val section = editEvent.section

            this.text = fakeBuildStart + section + fakeBuildEnd
            this.shiftCursor(selectionStart - this.selectionEnd + section.length)
            BUS.fire(PostTextEditEvent(set, build))
        }
    }

    fun deleteWords(count: Int) {
        if (!this.text.isEmpty())
            if (this.selectionEnd != this.cursorPosition)
                this.writeText("")
            else
                this.deleteFromCursor(this.getWordStartingFromCursor(count) - this.cursorPosition)
    }

    fun deleteFromCursor(count: Int) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition)
                this.writeText("")
            else {
                val backwards = count < 0
                val start = if (backwards) this.cursorPosition + count else this.cursorPosition
                val end = if (backwards) this.cursorPosition else this.cursorPosition + count
                var build = if (start >= 0) this.text.substring(0, start) else ""

                val buildStart = build

                var buildEnd = ""
                if (end < this.text.length) {
                    buildEnd = this.text.substring(end)
                    build += buildEnd
                }


                val editEvent = BUS.fire(PreTextEditEvent("", build))
                if (!editEvent.isCanceled()) {
                    val section = editEvent.section

                    this.text = buildStart + section + buildEnd
                    if (backwards) this.shiftCursor(count)

                    BUS.fire(PostTextEditEvent("", build))
                }
            }
        }
    }

    fun getWordStartingFromCursor(relativeIndex: Int): Int {
        return this.getWord(relativeIndex, this.cursorPosition)
    }

    fun getWord(relativeIndex: Int, startingPos: Int): Int {
        return this.getWordSkippingWhitespace(relativeIndex, startingPos, true)
    }

    fun getWordSkippingWhitespace(relativeIndex: Int, startingPos: Int, skipWhitespace: Boolean): Int {
        var pos = startingPos
        val backwards = relativeIndex < 0
        val toSearch = Math.abs(relativeIndex)

        for (i in 0 until toSearch)
            if (!backwards) {
                val length = this.text.length
                pos = this.text.indexOf(' ', pos)

                if (pos == -1)
                    pos = length
                else
                    while (skipWhitespace && pos < length && this.text[pos] == ' ')
                        pos++
            } else {
                while (skipWhitespace && pos > 0 && this.text[pos - 1] == ' ')
                    pos--
                while (pos > 0 && this.text[pos - 1] != ' ')
                    pos--
            }

        return pos
    }

    fun shiftCursor(num: Int) {
        this.cursorPosition = this.selectionEnd + num
    }

    fun cursorToStart() {
        this.cursorPosition = 0
    }

    fun cursorToEnd() {
        this.cursorPosition = this.text.length
    }

    fun handleKeyTyped(input: Char, inputCode: Int): Boolean {

        if (!this.isFocused && !this.autoFocus)
            return false

        when {
            GuiScreen.isKeyComboCtrlA(inputCode) -> {
                this.cursorToEnd()
                this.setSelectionPosition(0)
                return true
            }
            GuiScreen.isKeyComboCtrlC(inputCode) -> {
                GuiScreen.setClipboardString(this.selectedText)
                return true
            }
            GuiScreen.isKeyComboCtrlV(inputCode) -> {
                if (this.isEnabled)
                    this.writeText(GuiScreen.getClipboardString())
                return true
            }
            GuiScreen.isKeyComboCtrlX(inputCode) -> {
                GuiScreen.setClipboardString(this.selectedText)
                if (this.isEnabled)
                    this.writeText("")
                return true
            }
            else -> when (inputCode) {
                Keyboard.KEY_BACK -> {
                    if (isEnabled)
                        if (GuiScreen.isCtrlKeyDown())
                            this.deleteWords(-1)
                        else
                            this.deleteFromCursor(-1)
                    return true
                }
                Keyboard.KEY_HOME -> {

                    if (GuiScreen.isShiftKeyDown())
                        this.setSelectionPosition(0)
                    else
                        this.cursorToStart()

                    return true
                }
                Keyboard.KEY_LEFT -> {
                    if (GuiScreen.isShiftKeyDown())
                        if (GuiScreen.isCtrlKeyDown())
                            this.setSelectionPosition(this.getWord(-1, this.selectionEnd))
                        else
                            this.setSelectionPosition(this.selectionEnd - 1)
                    else if (GuiScreen.isCtrlKeyDown())
                        this.cursorPosition = this.getWordStartingFromCursor(-1)
                    else
                        this.shiftCursor(-1)

                    return true
                }
                Keyboard.KEY_RIGHT -> {
                    if (GuiScreen.isShiftKeyDown())
                        if (GuiScreen.isCtrlKeyDown())
                            this.setSelectionPosition(this.getWord(1, this.selectionEnd))
                        else
                            this.setSelectionPosition(this.selectionEnd + 1)
                    else if (GuiScreen.isCtrlKeyDown())
                        this.cursorPosition = this.getWordStartingFromCursor(1)
                    else
                        this.shiftCursor(1)

                    return true
                }
                Keyboard.KEY_END -> {

                    if (GuiScreen.isShiftKeyDown())
                        this.setSelectionPosition(this.text.length)
                    else
                        this.cursorToEnd()

                    return true
                }
                Keyboard.KEY_DELETE -> {
                    if (this.isEnabled)
                        if (GuiScreen.isCtrlKeyDown())
                            this.deleteWords(1)
                        else
                            this.deleteFromCursor(1)

                    return true
                }
                Keyboard.KEY_RETURN -> {
                    BUS.fire(TextSentEvent(text))
                    return true
                }
                else -> if (!useVanillaFilter || ChatAllowedCharacters.isAllowedCharacter(input)) {
                    if (this.isEnabled)
                        this.writeText(Character.toString(input))

                    return true
                }
            }
        }

        return false
    }

    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (this.canLoseFocus) {
            requestFocusedState(mouseOver)
        }

        return if (this.isFocused && mouseOver && mouseButton == 0) {
            val visible = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.widthi)
            this.cursorPosition = this.fontRenderer.trimStringToWidth(visible, mouseX).length + this.lineScrollOffset
            true
        } else
            false
    }

    private fun drawString(text: String, x: Float, y: Float, color: Int): Int {
        return this.fontRenderer.drawString(text, x, y, color, useShadow)
    }

    fun drawTextBox() {
        if (this.isVisible) {
            val max = maxStringLength

            val textColor = if (this.isEnabled) this.enabledColor.rgb else this.disabledColor.rgb
            val cursorRelativePosition = this.cursorPosition - this.lineScrollOffset
            var selectionEndPosition = this.selectionEnd - this.lineScrollOffset
            val visible = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.widthi - fontRenderer.getStringWidth("_"))
            val cursorVisible = cursorRelativePosition >= 0 && cursorRelativePosition <= visible.length
            val cursorBlinkActive = this.isFocused && this.cursorCounter / 12 % 2 == 0 && cursorVisible
            var offset = 0

            if (selectionEndPosition > visible.length)
                selectionEndPosition = visible.length

            if (!visible.isEmpty()) {
                val toCursor = if (cursorVisible) visible.substring(0, cursorRelativePosition) else visible
                offset = this.drawString(toCursor, offset.toFloat(), 0f, textColor)
            }

            val cursorInText = !this.useUnderscoreCursor || this.cursorPosition < this.text.length || this.text.length >= max
            var unselectedBound = offset

            if (!cursorVisible)
                unselectedBound = if (cursorRelativePosition > 0) widthi - fontRenderer.getStringWidth("_") else 0
            else if (cursorInText)
                unselectedBound = --offset

            if (!visible.isEmpty() && cursorVisible && cursorRelativePosition < visible.length)
                this.drawString(visible.substring(cursorRelativePosition), offset.toFloat(), 0f, textColor)

            if (cursorBlinkActive)
                if (cursorInText) {
                    Gui.drawRect(unselectedBound, -1, unselectedBound + 1, 2 + this.fontRenderer.FONT_HEIGHT, cursorColor.rgb)
                    GlStateManager.enableBlend()
                } else
                    this.drawString("_", unselectedBound.toFloat(), 1f, textColor)

            if (selectionEndPosition != cursorRelativePosition) {
                val selectionX = this.fontRenderer.getStringWidth(visible.substring(0, selectionEndPosition))
                this.drawSelectionBox(unselectedBound, 0, selectionX - 1, this.fontRenderer.FONT_HEIGHT)
            }

            GlStateManager.color(1f, 1f, 1f, 1f)
        }
    }

    private fun drawSelectionBox(startX: Int, startY: Int, endX: Int, endY: Int) {
        var minX = Math.min(startX, endX)
        var maxX = Math.max(startX, endX)
        val minY = Math.min(startY, endY)
        val maxY = Math.max(startY, endY)

        if (minX > width)
            minX = widthi

        if (maxX > width)
            maxX = widthi

        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        selectionColor.glColor()
        GlStateManager.disableTexture2D()
        GlStateManager.enableColorLogic()
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE)
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        bufferbuilder.pos(maxX.toDouble(), minY.toDouble(), 0.0).endVertex()
        bufferbuilder.pos(minX.toDouble(), minY.toDouble(), 0.0).endVertex()
        bufferbuilder.pos(minX.toDouble(), maxY.toDouble(), 0.0).endVertex()
        bufferbuilder.pos(maxX.toDouble(), maxY.toDouble(), 0.0).endVertex()
        tessellator.draw()
        GlStateManager.disableColorLogic()
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
    }

    fun setSelectionPosition(targetPosition: Int) {
        var position = targetPosition
        val length = this.text.length

        if (position > length) position = length

        if (position < 0) position = 0

        this.selectionEnd = position

        if (this.lineScrollOffset > length)
            this.lineScrollOffset = length

        val boxWidth = this.widthi - fontRenderer.getStringWidth("_")
        val visible = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), boxWidth)
        val positionInOverall = visible.length + this.lineScrollOffset

        if (position == this.lineScrollOffset)
            this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, boxWidth, true).length

        if (position > positionInOverall)
            this.lineScrollOffset += position - positionInOverall
        else if (position <= this.lineScrollOffset)
            this.lineScrollOffset -= this.lineScrollOffset - position

        this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, length)
    }

    class PreTextEditEvent(var section: String, val whole: String) : EventCancelable()
    class PostTextEditEvent(var section: String, val whole: String) : Event()
    class TextSentEvent(val content: String) : Event()
    class FocusEvent(val wasFocused: Boolean) : EventCancelable()
}

