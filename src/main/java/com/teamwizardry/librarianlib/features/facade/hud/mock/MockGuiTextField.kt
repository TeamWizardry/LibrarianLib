package com.teamwizardry.librarianlib.features.facade.hud.mock

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiTextField
import kotlin.math.max
import kotlin.math.min

/**
 * This component assumes it has already been positioned at the field's X and Y position, so all coordinates are
 * relative to that.
 */
class MockGuiTextField(): GuiLayer() {
    private val mc: Minecraft get() = Minecraft.getMinecraft()
    private val fontRenderer: FontRenderer get() = mc.fontRenderer

    val background = GuiLayer()
    val visibleText = GuiLayer()
    val fullText = GuiLayer()
    val selection = GuiLayer()
    val fullSelection = GuiLayer()
    val textCursor = GuiLayer()

    init {
        this.add(background, visibleText, fullText, selection, fullSelection, textCursor)
    }

    fun updateMock(field: GuiTextField) {
        if(!field.visible) {
            this.isVisible = false
            return
        }
        this.isVisible = true

        background.isVisible = field.enableBackgroundDrawing
        if (field.enableBackgroundDrawing) {
            this.size = vec(field.width, field.height)
            background.frame = rect(-1, -1, field.width + 2, field.height + 2)
        } else {
            this.size = vec(field.width, fontRenderer.FONT_HEIGHT)
            background.frame = rect(0, 0, field.width, fontRenderer.FONT_HEIGHT)
        }

        val relativeCursorIndex = field.cursorPosition - field.lineScrollOffset
        var relativeSelectionEnd = field.selectionEnd - field.lineScrollOffset
        val visibleString = fontRenderer.trimStringToWidth(field.text.substring(field.lineScrollOffset), field.width)
        val isCursorIndexVisible = relativeCursorIndex >= 0 && relativeCursorIndex <= visibleString.length
        val shouldShowCursor = field.isFocused && field.cursorCounter / 6 % 2 == 0 && isCursorIndexVisible
        val textStartX = if (field.enableBackgroundDrawing) 4 else 0
        val textStartY = if (field.enableBackgroundDrawing) (field.height - 8) / 2 else 0
        val useThinCursor = field.cursorPosition < field.text.length || field.text.length >= field.maxStringLength

        if (relativeSelectionEnd > visibleString.length) {
            relativeSelectionEnd = visibleString.length
        }

        if (visibleString.isEmpty()) {
            this.visibleText.frame = rect(textStartX, textStartY, 0, fontRenderer.FONT_HEIGHT)
        } else {
            this.visibleText.frame = rect(textStartX, textStartY, fontRenderer.getStringWidth(visibleString) + 1, fontRenderer.FONT_HEIGHT)
        }

        val cursorX: Int

        if (!isCursorIndexVisible) {
            cursorX = if (relativeCursorIndex > 0) textStartX + field.width else textStartX
        } else if(visibleString.isEmpty()) {
            cursorX = textStartX
        } else {
            val textBeforeCursor = visibleString.substring(0, relativeCursorIndex)
            cursorX = textStartX + fontRenderer.getStringWidth(textBeforeCursor) + if (useThinCursor) 0 else 1
        }

        textCursor.isVisible = shouldShowCursor
        if (shouldShowCursor) {
            if (useThinCursor) {
                textCursor.frame = rect(cursorX, textStartY - 1, 1, fontRenderer.FONT_HEIGHT + 2)
            } else {
                textCursor.frame = rect(cursorX, textStartY, fontRenderer.getStringWidth("_") + 1, fontRenderer.FONT_HEIGHT)
            }
        }

        if (relativeSelectionEnd != relativeCursorIndex) {
            selection.isVisible = false

            val selectionEndX = textStartX + fontRenderer.getStringWidth(visibleString.substring(0, relativeSelectionEnd)) - 1
            selection.frame = rect(min(cursorX, selectionEndX), textStartY - 1, max(selectionEndX, cursorX), fontRenderer.FONT_HEIGHT + 1)
        } else {
            selection.isVisible = false
        }
    }

    private val GuiTextField.lineScrollOffset by MethodHandleHelper.delegateForReadOnly<GuiTextField, Int>(
        GuiTextField::class.java, "lineScrollOffset", "field_146225_q", "r")
    private val GuiTextField.cursorCounter by MethodHandleHelper.delegateForReadOnly<GuiTextField, Int>(
        GuiTextField::class.java, "cursorCounter", "field_146214_l", "m")
}