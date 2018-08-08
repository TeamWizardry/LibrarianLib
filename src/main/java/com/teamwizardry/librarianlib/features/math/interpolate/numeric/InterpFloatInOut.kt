package com.teamwizardry.librarianlib.features.math.interpolate.numeric

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import kotlin.math.max
import kotlin.math.min

/**
 * Goes from 0 to 1 in [fadeIn] units of time, holds 1 for [normal] units, then goes to 0 in [fadeOut] units.
 *
 * [fadeIn], [normal], and [fadeOut] are proportions
 */
class InterpFloatInOut(fadeIn: Int, normal: Int, fadeOut: Int) : InterpFunction<Float> {
    val fadeInFraction = fadeIn.toFloat() / (fadeIn + normal + fadeOut)
    val fadeOutFraction = fadeOut.toFloat() / (fadeIn + normal + fadeOut)

    @Deprecated(message = "Confusingly named", replaceWith = ReplaceWith("fadeInFraction"))
    val fadeInEnd = fadeInFraction
    @Deprecated(message = "Confusingly named", replaceWith = ReplaceWith("fadeOutFraction"))
    val fadeOutStart = fadeOutFraction

    override fun get(i: Float): Float {
        val fadeInResult = i/fadeInFraction
        val fadeOutResult = (1-i)/fadeOutFraction
        return max(0f, min(1f, min(fadeInResult, fadeOutResult)))
    }

}
