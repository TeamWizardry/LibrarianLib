package com.teamwizardry.librarianlib.features.math.interpolate.position

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.math.rotate
import com.teamwizardry.librarianlib.features.math.rotationMatrix
import com.teamwizardry.librarianlib.features.math.withTranslation
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
    private val disp = point2 - point1
    private val len = disp.length()
    private val radiansPerUnit = (rotations * 2 * Math.PI).toFloat()

    private val transform = rotationMatrix(vec(0.0, 1.0, 0.0), disp).withTranslation(point1)

    override fun get(i: Float): Vec3d {
        val radius = radius1 + (radius2 - radius1) * i.toDouble()

        return transform.rotate(
                radius * MathHelper.cos(i * radiansPerUnit),
                i * len,
                radius * MathHelper.sin(i * radiansPerUnit))
    }

}
