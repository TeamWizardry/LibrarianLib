package com.teamwizardry.librarianlib.facade.provided

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.StringTextComponent
import java.awt.Color
import kotlin.math.min

public class SafetyNetErrorScreen(private val message: String, private val e: Exception): Screen(StringTextComponent("§4§nSafety net caught an exception:")) {
    private val guiWidth: Int
    private val guiHeight: Int

    private val gap = 2

    private val parts = mutableListOf<ScreenPart>()
    private var hasLogged = false

    init {
        val maxWidth = 300

        parts.add(TextScreenPart(title.formattedText, maxWidth))
        parts.add(TextScreenPart("§1§l${e.javaClass.simpleName}"))
        parts.add(TextScreenPart("Exception caught while $message", maxWidth))
        e.message?.also { exceptionMessage ->
            parts.add(TextScreenPart(exceptionMessage, maxWidth))
        }

        guiWidth = min(maxWidth, parts.map { it.width }.maxOrNull() ?: 0) / 2 * 2

        guiHeight = parts.sumBy { it.height } + (parts.size - 1) * gap
    }

    override fun init() {
        super.init()
        if (!hasLogged) {
            logger.error("Safety net caught an exception while $message", e)
            hasLogged = true
        }
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground()

        val topLeft = vec(width - guiWidth, height - guiHeight) / 2
        val border = 8

        fill(
            topLeft.xi - border, topLeft.yi - border,
            topLeft.xi + guiWidth + border, topLeft.yi + guiHeight + border,
            Color.lightGray.rgb
        )

        RenderSystem.pushMatrix()
        RenderSystem.translatef(width / 2f, (height - guiHeight) / 2f, 0f)

        var y = 0
        parts.forEach { part ->
            part.render(y)
            y += part.height + gap
        }

        RenderSystem.popMatrix()
    }

    private fun drawCenteredStringNoShadow(fontRenderer: FontRenderer, text: String, x: Int, y: Int, color: Int) {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2f, y.toFloat(), color)
    }

    private abstract class ScreenPart {
        abstract val height: Int
        abstract val width: Int
        abstract fun render(yPos: Int)
    }

    private inner class DividerScreenPart(override val height: Int): ScreenPart() {
        override val width: Int = 0
        override fun render(yPos: Int) {
            fill(-guiWidth / 2, yPos, guiWidth / 2, yPos + height, Color.darkGray.rgb)
        }
    }

    private inner class TextScreenPart(val text: String, maxWidth: Int? = null): ScreenPart() {
        override val height: Int
        override val width: Int
        val lines: List<String>

        init {
            val fontRenderer = Client.minecraft.fontRenderer
            if (maxWidth == null) {
                lines = listOf(text)
            } else {
                lines = fontRenderer.listFormattedStringToWidth(text, maxWidth)
            }

            height = lines.size * fontRenderer.FONT_HEIGHT + // line height
                (lines.size - 1) // 1px between lines

            width = (lines.map { fontRenderer.getStringWidth(it) }.maxOrNull() ?: 0) / 2 * 2
        }

        override fun render(yPos: Int) {
            var y = yPos
            val fontRenderer = Client.minecraft.fontRenderer
            if (lines.isNotEmpty()) {
                if (lines.size == 1) {
                    drawCenteredStringNoShadow(fontRenderer, lines[0], 0, y, 0)
                    y += fontRenderer.FONT_HEIGHT + 1
                } else {
                    lines.forEach { line ->
                        fontRenderer.drawString(line, -guiWidth / 2f, y.toFloat(), 0)
                        y += fontRenderer.FONT_HEIGHT + 1
                    }
                }
            }
        }
    }

    private companion object {
        private val logger = LibrarianLibFacadeModule.makeLogger("Safety Net")
    }
}