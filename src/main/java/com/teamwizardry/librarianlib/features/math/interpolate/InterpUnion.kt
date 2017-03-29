package com.teamwizardry.librarianlib.features.math.interpolate

class InterpUnionImpl<T> internal constructor(val list: MutableList<UnionEntry<T>>) : InterpFunction<T> {
    override fun get(i: Float): T {
        var floored = Math.floor(i.toDouble()).toFloat()
        val fract = if (floored == i && i > 0) 1f else i - floored
        val func = list.first { fract >= it.start && fract <= it.end }
        val span = func.end - func.start
        val funcI = (fract - func.start) / span
        return func.func.get(funcI)
    }
}

/**
 * Creates InterpFunction unions.
 *
 * The InterpFunction weights are proportions
 */
class InterpUnion<T> {
    private val functions: MutableList<InterpFunction<T>> = mutableListOf()
    private val weights: MutableList<Float> = mutableListOf()

    /**
     * Add an interpFunction
     *
     * Returns self. Useful for chaining
     */
    fun with(f: InterpFunction<T>, w: Float): InterpUnion<T> {
        functions.add(f)
        weights.add(w)
        return this
    }

    /**
     * Add an InterpFunction
     *
     * Returns the final result of the passed function, useful for generating the starting value of the next function
     */
    fun add(f: InterpFunction<T>, w: Float): T {
        functions.add(f)
        weights.add(w)
        return f.get(1f)
    }

    /**
     * Create an instance of the InterpUnion implementation
     */
    fun build(): InterpUnionImpl<T> {
        val entries = mutableListOf<UnionEntry<T>>()
        val totalWeight = weights.sum()
        var currentPos = 0f
        for (i in 0..functions.size - 1) {
            val span = weights[i].toFloat() / totalWeight
            entries.add(UnionEntry(
                    functions[i],
                    currentPos,
                    currentPos + span
            ))
            currentPos += span
        }
        return InterpUnionImpl(entries)
    }
}

internal data class UnionEntry<T>(val func: InterpFunction<T>, val start: Float, val end: Float)
