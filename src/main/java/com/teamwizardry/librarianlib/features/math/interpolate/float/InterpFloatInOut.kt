package com.teamwizardry.librarianlib.features.math.interpolate.float

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction

/**
 * Goes from 0 to 1 in [fadeIn] units of time, holds 1 for [normal] units, then goes to 0 in [fadeOut] units.
 *
 * [fadeIn], [normal], and [fadeOut] are proportions
 */
class InterpFloatInOut(fadeIn: Int, normal: Int, fadeOut: Int) : InterpFunction<Float> {
    val fadeInEnd = fadeIn.toFloat() / (fadeIn + normal + fadeOut)
    val fadeOutStart = fadeOut.toFloat() / (fadeIn + normal + fadeOut)

    override fun get(i: Float): Float {
        if (i <= fadeInEnd && fadeInEnd != 0f) {
            return i / fadeInEnd
        }
        if (i >= fadeOutStart && fadeOutStart != 1f) {
            return (i - fadeOutStart) / (1 - fadeOutStart)
        }
        return 1f
    }

}
