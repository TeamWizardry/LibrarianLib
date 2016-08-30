package com.teamwizardry.librarianlib.common.util.math.interpolate

/**
 * A function designed to interpolate based on an input between 0 and 1, inclusive
 */
interface InterpFunction<T> {
    /**
     * `i` is from 0-1
     */
    fun get(i: Float): T

    fun reverse(): InterpFunction<T> = ReversedInterpFunction(this)

    fun list(points: Int): List<T> = InterpListGenerator.getList(this, points)

    companion object {
        val ONE_TO_ONE: InterpFunction<Float> = OneToOneInterp()
    }
}

internal class ReversedInterpFunction<T>(val wrap: InterpFunction<T>) : InterpFunction<T> {

    override fun get(i: Float): T {
        return wrap.get(1-i);
    }

    override fun reverse(): InterpFunction<T> {
        return wrap
    }
}

private class OneToOneInterp : InterpFunction<Float> {
    override fun get(i: Float): Float {
        return i
    }
}

class StaticInterp<T> (val v: T) : InterpFunction<T> {
    override fun get(i: Float): T {
        return v
    }
}