package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer

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

    val runs = mutableListOf<TextRun>()
    var bounds: Rect2d = Rect2d.ZERO

    fun genIfNeeded() {
        if(changed) {
            changed = false
            genRuns()
        }
    }

    fun genRuns() {
        runs.clear()
        if (unicode) {
            if(enableUnicodeBidi)
                fontRenderer.bidiFlag = true
            fontRenderer.unicodeFlag = true
        }

        bounds = Rect2d.ZERO
        var y = 0
        var remaining = text
        while(remaining.isNotEmpty()) {
            val i = fontRenderer.sizeStringToWidth(remaining, wrapWidth)

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
            bounds = bounds.expandToFit(run.rect)
        }

        if (unicode) {
            if(enableUnicodeBidi)
                fontRenderer.bidiFlag = false
            fontRenderer.unicodeFlag = false
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