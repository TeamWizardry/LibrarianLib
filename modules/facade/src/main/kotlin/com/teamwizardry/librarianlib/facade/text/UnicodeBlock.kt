package com.teamwizardry.librarianlib.facade.text

import com.google.common.collect.Range
import com.google.common.collect.TreeRangeMap

class UnicodeBlock private constructor(val name: String, val range: IntRange) {
    companion object {
        private val blocks = mutableMapOf<String, UnicodeBlock>()
        @Suppress("UnstableApiUsage")
        private val ranges = TreeRangeMap.create<Int, UnicodeBlock>()

        init {
            val blocksFile = UnicodeBlock::class.java.getResource("ucd/Blocks.txt").readText()
            val lineRegex = """([0-9A-F]+)\.\.([0-9A-F]+); (.+)""".toRegex()
            for(line in blocksFile.lineSequence()) {
                if(line.startsWith("#") || line.isBlank()) continue
                val (min, max, name) = lineRegex.matchEntire(line)?.destructured ?: continue
                val block = UnicodeBlock(name, min.toInt(16) .. max.toInt(16))
                blocks[name] = block
                ranges.put(Range.closed(block.range.start, block.range.endInclusive), block)
            }
        }

        operator fun get(ch: Int): UnicodeBlock? {
            return ranges[ch]
        }

        operator fun get(name: String): UnicodeBlock? {
            return blocks[name]
        }
    }
}