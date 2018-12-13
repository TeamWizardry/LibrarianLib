package com.teamwizardry.librarianlib.features.text

import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import kotlin.math.min

class TextLayout {
    var fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRenderer
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var text: String = ""
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var wrapWidth: Int = Int.MAX_VALUE
        set(value) {
            if(field != value) changed = true
            field = value
        }

    var maxWidth: Int = Int.MAX_VALUE
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var maxLines: Int = Int.MAX_VALUE
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var truncationText: String? = null
        set(value) {
            if(field != value) changed = true
            field = value
        }

    var lineSpacing: Int = 0
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var align: Align2d = Align2d.LEFT_TOP
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var unicode: Boolean = false
        set(value) {
            if(field != value) changed = true
            field = value
        }
    var enableUnicodeBidi: Boolean = true
        set(value) {
            if(field != value) changed = true
            field = value
        }

    var changed = false

    var runs = mutableListOf<TextRun>()
    var bounds: Rect2d = Rect2d.ZERO

    fun genIfNeeded() {
        if(changed) {
            changed = false
            genRuns()
        }
    }

    fun genRuns() {
        runs.clear()
        bounds = Rect2d.ZERO
        if(text.isEmpty()) return

        if (unicode) {
            if(enableUnicodeBidi)
                fontRenderer.bidiFlag = true
            fontRenderer.unicodeFlag = true
        }

        createRuns()

        truncateRuns()

        var bounds: Rect2d? = null
        runs.forEach { run ->
            bounds = bounds?.expandToFit(run.rect) ?: run.rect
        }
        this.bounds = bounds ?: rect(0,0, 0,0)

        if (unicode) {
            if(enableUnicodeBidi)
                fontRenderer.bidiFlag = false
            fontRenderer.unicodeFlag = false
        }
    }

    private fun createRuns() {
        var y = 0
        var remaining = text
        while(remaining.isNotEmpty()) {
            val i = if(wrapWidth == 0) remaining.length else fontRenderer.sizeStringToWidth(remaining, wrapWidth)
            if(i == 0 && !remaining[0].isWhitespace())
                throw TextLayoutException("Could not wrap `$remaining` to width $wrapWidth")

            val runString: String

            if (remaining.length <= i) {
                runString = remaining
                remaining = ""
            } else {
                runString = remaining.substring(0, i)
                val c0 = remaining[i]
                val flag = c0 == ' ' || c0 == '\n'
                remaining = FontRenderer.getFormatFromString(runString) + remaining.substring(i + if (flag) 1 else 0)
            }

            val width = fontRenderer.getStringWidth(runString)
            val x = when(align.x) {
                Align2d.X.RIGHT -> -width
                Align2d.X.CENTER -> -width/2
                else -> 0
            }
            val rect = rect(
                x, y,
                width, fontRenderer.FONT_HEIGHT
            )
            y += fontRenderer.FONT_HEIGHT + lineSpacing
            val run = TextRun(runString, rect)
            runs.add(run)
        }
    }

    private fun truncateRuns() {
        val truncationText = truncationText
        if(truncationText != null && (runs.size > maxLines || runs.last().rect.width > maxWidth)) {
            runs = runs.dropLast(runs.size - maxLines).toMutableList()
            val lastRun = runs.last()

            val maxWidth = min(wrapWidth, maxWidth)
            val truncationWidth = fontRenderer.getStringWidth(truncationText)
            val remainingWidth = maxWidth - truncationWidth
            if(remainingWidth > 0) {
                val truncatedString = fontRenderer.trimStringToWidth(lastRun.text, remainingWidth) + truncationText
                runs[runs.lastIndex] = TextRun(truncatedString, rect(
                    lastRun.rect.x, lastRun.rect.y,
                    fontRenderer.getStringWidth(truncatedString), lastRun.rect.height
                ))
            }
        }
    }


    fun render(color: Int, dropShadow: Boolean) {
        if (unicode) {
            if(enableUnicodeBidi)
                fontRenderer.bidiFlag = true
            fontRenderer.unicodeFlag = true
        }
        runs.forEach {
            it.render(color, dropShadow)
        }
        if (unicode) {
            if(enableUnicodeBidi)
                fontRenderer.bidiFlag = false
            fontRenderer.unicodeFlag = false
        }
    }

    inner class TextRun(val text: String, val rect: Rect2d) {
        fun render(color: Int, dropShadow: Boolean) {
            fontRenderer.drawString(text, rect.xf, rect.yf, color, dropShadow)
        }
    }

    companion object {
        private val sizeStringToWidth_mh = MethodHandleHelper.wrapperForMethod(FontRenderer::class.java,
            "sizeStringToWidth", "func_78259_e", String::class.java, Int::class.javaPrimitiveType!!
        )

        private fun FontRenderer.sizeStringToWidth(str: String, wrapWidth: Int): Int {
            return sizeStringToWidth_mh(this, arrayOf(str, wrapWidth)) as Int
        }
    }
}

class TextLayoutException: RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}