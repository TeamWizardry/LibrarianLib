package com.teamwizardry.librarianlib.facade.provided

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.LibLibFacade
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.min

public class SafetyNetErrorScreen(private val message: String, private val e: Exception): Screen(Text.literal("§4§nSafety net caught an exception:")) {
    private val guiWidth: Int
    private val guiHeight: Int

    private val gap = 2

    private val parts = mutableListOf<ScreenPart>()
    private var hasLogged = false

    init {

        val maxWidth = 300

        parts.add(TextScreenPart(title.string, maxWidth))
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

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)
        context.matrices.push()

        val topLeft = vec(width - guiWidth, height - guiHeight) / 2
        val border = 8

        context.fill(
            topLeft.xi - border, topLeft.yi - border,
            topLeft.xi + guiWidth + border, topLeft.yi + guiHeight + border,
            Color.lightGray.rgb
        )

        context.matrices.translate(width / 2.0, (height - guiHeight) / 2.0, 0.0)

        parts.forEach { part ->
            part.render(context)
            context.matrices.translate(0.0, part.height + gap.toDouble(), 0.0)
        }

        context.matrices.pop()
    }

    private abstract class ScreenPart {
        abstract val height: Int
        abstract val width: Int
        abstract fun render(context: DrawContext)
    }

    private inner class DividerScreenPart(override val height: Int): ScreenPart() {
        override val width: Int = 0
        override fun render(context: DrawContext) {
            context.fill(-guiWidth / 2, 0, guiWidth / 2, height, Color.darkGray.rgb)
        }
    }

    private inner class TextScreenPart(val text: String, maxWidth: Int? = null): ScreenPart() {
        override val height: Int
        override val width: Int
        val lines: List<OrderedText>
        val widths: List<Int>

        init {
            val fontRenderer = Client.minecraft.textRenderer
            if (maxWidth == null) {
                lines = listOf(Text.literal(text).asOrderedText())
            } else {
                lines = fontRenderer.wrapLines(Text.literal(text), maxWidth)
            }
            widths = lines.map { fontRenderer.getWidth(it) }

            height = lines.size * fontRenderer.fontHeight + // line height
                (lines.size - 1) // 1px between lines

            width = (widths.maxOrNull() ?: 0) / 2 * 2
        }

        override fun render(context: DrawContext) {
            val fontRenderer = Client.minecraft.textRenderer

            if (lines.isNotEmpty()) {
                var y = 0
                if (lines.size == 1) {
                    context.drawText(fontRenderer, lines[0], -widths[0] / 2, y, 0, false)
                } else {
                    lines.forEach { line ->
                        context.drawText(fontRenderer, line, -guiWidth / 2, y, 0, false)
                        y += fontRenderer.fontHeight + 1
                    }
                }
            }
        }
    }

    private companion object {
        private val logger = LibLibFacade.makeLogger("Safety Net")
    }
}