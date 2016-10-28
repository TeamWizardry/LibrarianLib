package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import java.awt.Color

/**
 * Interpolate each component of [a] to [b] separately
 */
class InterpColorComponents(val a: Color, val b: Color) : InterpFunction<Color> {
    override fun get(i: Float): Color {
        return Color(
                a.red + (b.red - a.red) * i,
                a.green + (b.green - a.green) * i,
                a.blue + (b.blue - a.blue) * i,
                a.transparency + (b.transparency - a.transparency) * i
        )
    }
}
