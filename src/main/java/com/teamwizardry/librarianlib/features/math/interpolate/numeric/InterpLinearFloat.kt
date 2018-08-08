package com.teamwizardry.librarianlib.features.math.interpolate.numeric

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction

/**
 * Interpolates between [a] and [b]
 */
class InterpLinearFloat(val a: Float, val b: Float) : InterpFunction<Float> {
    override fun get(i: Float): Float {
        return a + (b - a) * i
    }
}
