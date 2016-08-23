package com.teamwizardry.librarianlib.common.util.math.interpolate.position

import com.teamwizardry.librarianlib.common.util.div
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.withY
import net.minecraft.util.math.Vec3d

/**
 * Create a Bézier curve from [start] to [end] with [startControl] and [endControl] as handles for the curvature
 *
 * ![Bézier curve image example](http://imgur.com/bkIoyyR) |
 * P0 is [start], P1 is [startControl], P2 is [endControl], and P3 is [end]
 */
class InterpBezier3D @JvmOverloads constructor(
        val start: Vec3d, val end: Vec3d,
        val startControl: Vec3d = ( (start+end)/2 ).withY(start.yCoord), val endControl: Vec3d = ( (start+end)/2 ).withY(end.yCoord)
) : InterpFunction<Vec3d> {
    override fun get(i: Float): Vec3d {
        return Vec3d(
                getBezierComponent(i.toDouble(), start.xCoord, end.xCoord, startControl.xCoord, endControl.xCoord),
                getBezierComponent(i.toDouble(), start.yCoord, end.yCoord, startControl.yCoord, endControl.yCoord),
                getBezierComponent(i.toDouble(), start.zCoord, end.zCoord, startControl.zCoord, endControl.zCoord)
        )
    }

    private fun getBezierComponent(t: Double, s: Double, e: Double, sc: Double, ec: Double): Double {
        val T = 1-t
        return T*T*T*s + 3*T*T*t*sc + 3*T*t*t*ec + t*t*t*e
    }
}