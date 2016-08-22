package com.teamwizardry.librarianlib.common.util.math.interpolate

/**
 * Created by TheCodeWarrior
 */
class InterpUnionImpl<T> internal constructor(val list: MutableList<UnionEntry<T>>): InterpFunction<T> {
    override fun get(i: Float): T {
        val fract = i % 1
        val func = list.first { fract >= it.start && fract <= it.end }
        val span = func.end - func.start
        val funcI = (i-func.start)/span
        return func.func.get(funcI)
    }
}

class InterpUnion<T>() {
    private val functions: MutableList<InterpFunction<T>> = mutableListOf()
    private val weights: MutableList<Float> = mutableListOf()

    /**
     * Returns self. Useful for chaining
     */
    fun with(f: InterpFunction<T>, w: Float): InterpUnion<T> {
        functions.add(f)
        weights.add(w)
        return this
    }

    /**
     * Returns the final result of the passed function, useful for generating the starting value of the next function
     */
    fun add(f: InterpFunction<T>, w: Float): T {
        functions.add(f)
        weights.add(w)
        return f.get(1f)
    }

    fun build(): InterpUnionImpl<T> {
        val entries = mutableListOf<UnionEntry<T>>()
        val totalWeight = weights.sum()
        var currentPos = 0f
        for(i in 0..functions.size-1) {
            val span = weights[i].toFloat()/totalWeight
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