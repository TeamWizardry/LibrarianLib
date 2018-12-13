package com.teamwizardry.librarianlib.features.text

import net.minecraft.client.renderer.BufferBuilder
import java.awt.Color

object Typesetter {
    fun createRun(text: String, maxWidth: Int): TextRun {
        if(text.isEmpty()) return TextRun(emptyList())
        val font = Font.tiny

        val glyphs = mutableListOf<Glyph>()
        var width = 0
        for(char in text) {
            val glyph = font[char]
            width += glyph.advance
            if(width > maxWidth)
                break
            glyphs.add(glyph)
        }
        return TextRun(glyphs)
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