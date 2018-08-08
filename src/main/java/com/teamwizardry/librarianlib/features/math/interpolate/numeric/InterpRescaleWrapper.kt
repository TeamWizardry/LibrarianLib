package com.teamwizardry.librarianlib.features.math.interpolate.numeric

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction

class InterpRescaleWrapper(val min: Float, val max: Float, val wrapping: InterpFunction<Float>): InterpFunction<Float> {
    override fun get(i: Float): Float {
        return min + wrapping.get(i) * (max-min)
    }
}
