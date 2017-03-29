package com.teamwizardry.librarianlib.features.particle.functions

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
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
        var alpha = color.alpha
        if (i <= fadeInEnd && fadeInEnd != 0f) {
            alpha = (alpha * (i / fadeInEnd)).toInt()
        }
        if (i >= fadeOutStart && fadeOutStart != 1f) {
            alpha *= (alpha * ((i - fadeOutStart) / (1 - fadeOutStart))).toInt()
        }
        return Color(color.rgb and 0x00FFFFFF or ((alpha * 255).toInt() shl 24), true)
    }

}
