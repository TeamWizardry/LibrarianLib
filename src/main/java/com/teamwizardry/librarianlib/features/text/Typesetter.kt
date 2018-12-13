package com.teamwizardry.librarianlib.features.text

import net.minecraft.client.renderer.BufferBuilder
import java.awt.Color

object Typesetter {
    fun createRun(font: Font, text: String, maxWidth: Int): Pair<String, TextRun> {
        if(text.isEmpty()) return text to TextRun(emptyList())

        var glyphs = mutableListOf<Glyph>()
        var chars = 0
        var width = 0
        for(char in text) {
            if(char == '\n') {
                chars++
                break
            }
            val glyph = font[char]
            width += glyph.advance
            if(width > maxWidth) {
                val splitPoint = text.substring(0, chars+1).indexOfLast { it.isWhitespace() }
                if(splitPoint != -1) {
                    chars = splitPoint+1
                    glyphs = glyphs.subList(0, splitPoint)
                }
                break
            }

            chars++
            glyphs.add(glyph)
        }
        return text.substring(chars) to TextRun(glyphs)
    }
}

class TextRun(val glyphs: List<Glyph>) {
    fun place(vb: BufferBuilder, color: Color, x: Int, y: Int, size: Int) {
        var cursor = x
        glyphs.forEach { glyph ->
            glyph.place(vb, color, cursor, y, size)
            cursor += glyph.advance * size
        }
    }
}

class TypesetException: RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}