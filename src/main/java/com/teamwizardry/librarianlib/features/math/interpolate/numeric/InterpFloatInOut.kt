package com.teamwizardry.librarianlib.features.math.interpolate.numeric

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import kotlin.math.max
import kotlin.math.min

/**
 * Linearly increases from 0 to 1 over the first [fadeInFraction] of an interval.
 * Holds 1 until the last [fadeOutFraction] of that interval, and then decreases to 0.
 */
class InterpFloatInOut(val fadeInFraction: Float, val fadeOutFraction: Float) : InterpFunction<Float> {

    /**
     * Goes from 0 to 1 in [fadeIn] units of time, holds 1 for [normal] units, then goes to 0 in [fadeOut] units.
     *
     * [fadeIn], [normal], and [fadeOut] are proportions
     */
    constructor(fadeIn: Int, normal: Int, fadeOut: Int) : this(fadeIn.toFloat() / (fadeIn + normal + fadeOut),
            fadeOut.toFloat() / (fadeIn + normal + fadeOut))

    @Deprecated(message = "Confusingly named", replaceWith = ReplaceWith("fadeInFraction"))
    val fadeInEnd = fadeInFraction
    @Deprecated(message = "Confusingly named", replaceWith = ReplaceWith("fadeOutFraction"))
    val fadeOutStart = fadeOutFraction

    override fun get(i: Float): Float {
        val fadeInResult = i / fadeInFraction
        val fadeOutResult = (1 - i) / fadeOutFraction
        return max(0f, min(1f, min(fadeInResult, fadeOutResult)))
    }

}
