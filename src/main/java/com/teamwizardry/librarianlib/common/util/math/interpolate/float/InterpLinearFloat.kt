package com.teamwizardry.librarianlib.common.util.math.interpolate.float

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction

/**
 * Interpolates between [a] and [b]
 */
class InterpLinearFloat(val a: Float, val b: Float) : InterpFunction<Float> {
    override fun get(i: Float): Float {
        return a + (b - a) * i
    }
}
