package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class InterpColorHSV(a: Color, b: Color) : InterpFunction<Color> {

    private val aTransp = a.transparency
    private val bTransp = b.transparency

    private val aHSB: FloatArray
    private val bHSB: FloatArray

    init {
        aHSB = FloatArray(3)
        bHSB = FloatArray(3)
        Color.RGBtoHSB(a.red, a.green, a.blue, aHSB)
        Color.RGBtoHSB(b.red, b.green, b.blue, bHSB)
    }

    override fun get(i: Float): Color {
        return Color(
                Color.HSBtoRGB(
                        aHSB[0] + (bHSB[0]-aHSB[0])*i,
                        aHSB[1] + (bHSB[1]-aHSB[1])*i,
                        aHSB[2] + (bHSB[2]-aHSB[2])*i
                )
        )
    }

}