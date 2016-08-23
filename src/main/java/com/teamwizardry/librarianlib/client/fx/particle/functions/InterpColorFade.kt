package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import java.awt.Color

/**
 * Fades from transparent to [color] in [fadeIn] units of time, holds the color for [normal] units, then fades to
 * transparent in [fadeOut] units.
 *
 * [fadeIn], [normal], and [fadeOut] are proportions
 */
class InterpColorFade(val color: Color, fadeIn: Int, normal: Int, fadeOut: Int) : InterpFunction<Color> {
    val fadeInEnd = fadeIn.toFloat() / (fadeIn + normal + fadeOut)
    val fadeOutStart = fadeOut.toFloat() / (fadeIn + normal + fadeOut)

    override fun get(i: Float): Color {
        var alpha = color.alpha/255f
        if(i <= fadeInEnd && fadeInEnd != 0f) {
            alpha = i / fadeInEnd
        }
        if(i >= fadeOutStart && fadeOutStart != 1f) {
            alpha = ( i - fadeOutStart ) / (1 - fadeOutStart)
        }
        return Color(color.rgb or (( alpha * 255 ).toInt() shl 24 ))
    }

}