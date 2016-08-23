package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import java.awt.Color

/**
 * Interpolate colors based on their Hue, Saturation, and Value/Brightness.
 */
class InterpColorHSV private constructor(private val aTransp: Int, private val bTransp: Int) : InterpFunction<Color> {

    private val aHSB: FloatArray = FloatArray(3)
    private val bHSB: FloatArray = FloatArray(3)

    /**
     * Interpolate between the two colors [a] and [b]
     */
    constructor(a: Color, b: Color) : this(a.alpha, b.alpha){
        Color.RGBtoHSB(a.red, a.green, a.blue, aHSB)
        Color.RGBtoHSB(b.red, b.green, b.blue, bHSB)
    }

    /**
     * Extrapolate from color [a] by offsetting the hue by [hueOffsetDegrees].
     *
     * Also interpolate between [a]'s alpha component and [bAlpha] ([bAlpha] is from 0-255)
     */
    constructor(a: Color, bAlpha: Int, hueOffsetDegrees: Float) : this(a.alpha, bAlpha){
        Color.RGBtoHSB(a.red, a.green, a.blue, aHSB)
        Color.RGBtoHSB(a.red, a.green, a.blue, bHSB)
        bHSB[0] += hueOffsetDegrees/360f
    }

    override fun get(i: Float): Color {
        return Color(
                Color.HSBtoRGB(
                        aHSB[0] + (bHSB[0]-aHSB[0])*i,
                        aHSB[1] + (bHSB[1]-aHSB[1])*i,
                        aHSB[2] + (bHSB[2]-aHSB[2])*i
                ) or ( (aTransp + (bTransp-aTransp)*i ).toInt() shl 24)
        )
    }

}