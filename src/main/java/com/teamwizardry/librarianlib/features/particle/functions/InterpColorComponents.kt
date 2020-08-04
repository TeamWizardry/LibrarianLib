package com.teamwizardry.librarianlib.features.particle.functions

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import java.awt.Color

/**
 * Interpolate each component of [a] to [b] separately
 */
class InterpColorComponents(val a: Color, val b: Color) : InterpFunction<Color> {
    override fun get(i: Float): Color {
        return Color(
            (a.red + (b.red - a.red) * i) / 255,
            (a.green + (b.green - a.green) * i) / 255,
            (a.blue + (b.blue - a.blue) * i) / 255,
            (a.alpha + (b.alpha - a.alpha) * i) / 255
        )
    }
}
