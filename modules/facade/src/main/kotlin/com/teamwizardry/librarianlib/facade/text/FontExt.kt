package com.teamwizardry.librarianlib.facade.text

import dev.thecodewarrior.bitfont.data.Bitfont
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet

private val obfTransforms = mutableMapOf<Bitfont, ObfTransforms>()

private class ObfTransforms(val font: Bitfont) {
    val cache = Int2ObjectOpenHashMap<IntSet>()

    init {
        for(i in ' '.toInt() .. '~'.toInt()) {
            get(i)
        }
    }

    fun get(codepoint: Int): Int {
        val glyph = font.glyphs[codepoint] ?: return codepoint
        val set = cache.getOrPut(glyph.calcAdvance(font.spacing)) { IntOpenHashSet() }
        set.add(codepoint)
        return set.random()
    }
}

fun Bitfont.obfTransform(codepoint: Int): Int {
    return obfTransforms.getOrPut(this) { ObfTransforms(this) }.get(codepoint)
}