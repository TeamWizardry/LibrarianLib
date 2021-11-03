package com.teamwizardry.librarianlib.facade.text

import dev.thecodewarrior.bitfont.data.Bitfont
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import kotlin.random.Random

internal class ObfTransform(val font: Bitfont) {
    val cache = Int2ObjectOpenHashMap<IntSet>()

    init {
        val r = Random(0)
        for (i in ' '.code..'~'.code) {
            get(r, i)
        }
    }

    fun get(rng: Random, codepoint: Int): Int {
        val glyph = font.glyphs[codepoint] ?: return codepoint
        val set = cache.getOrPut(glyph.advance) { IntOpenHashSet() }
        set.add(codepoint)
        return set.random(rng)
    }

    companion object {
        private val cache = mutableMapOf<Bitfont, ObfTransform>()
        fun transform(font: Bitfont, rng: Random, codepoint: Int): Int {
            return cache.getOrPut(font) { ObfTransform(font) }.get(rng, codepoint)
        }
    }
}
