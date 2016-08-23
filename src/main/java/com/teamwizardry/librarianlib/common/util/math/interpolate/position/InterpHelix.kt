package com.teamwizardry.librarianlib.common.util.math.interpolate.position

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

/**
 * Create a helix with end origins at [point1] and [point2]. Radius at [point1] is [radius1], radius at [point2] is
 * [radius2]. Completes [rotations] full rotations between 0 and 1, and the angle is offset by [offset].
 *
 * ([offset] is in complete rotations. 0.5 == 180°, 1 == 360°)
 */
class InterpHelix(
        val point1: Vec3d, val point2: Vec3d, val radius1: Float, val radius2: Float,
        val rotations: Float, val offset: Float
) : InterpFunction<Vec3d> {
    private val norm = (point2 - point1).normalize()
    private val perpX =
            if (norm cross Vec3d(0.0, 1.0, 0.0) == Vec3d(0.0,0.0,0.0))
                Vec3d(1.0, 0.0, 0.0)
            else
                norm cross Vec3d(0.0, 1.0, 0.0)
    private val perpY = norm cross perpX

    override fun get(i: Float): Vec3d {
        val t = i*rotations + offset

        val radius = radius1 + (radius2-radius1)*i

        val x = radius*MathHelper.cos((t * 2 * Math.PI).toFloat())
        val y = radius*MathHelper.sin((t * 2 * Math.PI).toFloat())

        return point1 + (point2-point1)*i + perpX*x + perpY*y
    }

}