package com.teamwizardry.librarianlib.features.math.interpolate.position

import com.teamwizardry.librarianlib.features.kotlin.cross
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

/**
 * Create a circle with an [origin], a [normalVector] defining the direction it faces, a [radius],
 * the number of [rotations] to complete between 0-1, and an [offset] to begin the circle at.
 *
 * ([offset] is in full rotations, 0.5 == 180°, 1 == 360°)
 */
class InterpCircle @JvmOverloads constructor(val origin: Vec3d, normalVector: Vec3d, val radius: Float, val rotations: Float = 1f, val offset: Float = 0f) : InterpFunction<Vec3d> {
    val normal = normalVector.normalize()

    private val perpX =
            if (normal cross Vec3d(0.0, 1.0, 0.0) == Vec3d(0.0, 0.0, 0.0))
                Vec3d(1.0, 0.0, 0.0)
            else
                normal cross Vec3d(0.0, 1.0, 0.0)
    private val perpY = normal cross perpX

    override fun get(i: Float): Vec3d {
        val t = i * rotations + offset

        val x = radius * MathHelper.cos((t * 2 * Math.PI).toFloat())
        val y = radius * MathHelper.sin((t * 2 * Math.PI).toFloat())

        return origin + perpX * x + perpY * y
    }
}
