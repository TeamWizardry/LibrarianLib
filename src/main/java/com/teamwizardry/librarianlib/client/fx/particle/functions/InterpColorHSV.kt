package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class InterpColorHSV private constructor(private val aTransp: Int, private val bTransp: Int) : InterpFunction<Color> {

    private val aHSB: FloatArray = FloatArray(3)
    private val bHSB: FloatArray = FloatArray(3)

    constructor(a: Color, b: Color) : this(a.alpha, b.alpha){
        Color.RGBtoHSB(a.red, a.green, a.blue, aHSB)
        Color.RGBtoHSB(b.red, b.green, b.blue, bHSB)
    }

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