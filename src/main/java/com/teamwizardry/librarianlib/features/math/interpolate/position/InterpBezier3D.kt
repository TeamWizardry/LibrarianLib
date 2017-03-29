package com.teamwizardry.librarianlib.features.math.interpolate.position

import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.withY
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import net.minecraft.util.math.Vec3d

/**
 * Create a Bézier curve from [start] to [end] with [startControl] and [endControl] as relative positions of handles for the curvature
 *
 * ![Bézier curve image example](http://imgur.com/bkIoyyR) |
 * P0 is [start], P1 relative to P0 is [startControl], P2 relative to P3 is [endControl], and P3 is [end]
 */
class InterpBezier3D @JvmOverloads constructor(
        val start: Vec3d, val end: Vec3d,
        val startControl: Vec3d = ((end - start) / 2).withY(0), val endControl: Vec3d = ((start - end) / 2).withY(0)
) : InterpFunction<Vec3d> {
    
    private val absoluteStartControl = start + startControl
    private val absoluteEndControl = end + endControl
    
    override fun get(i: Float): Vec3d {
        return Vec3d(
                getBezierComponent(i.toDouble(), start.xCoord, end.xCoord, absoluteStartControl.xCoord, absoluteEndControl.xCoord),
                getBezierComponent(i.toDouble(), start.yCoord, end.yCoord, absoluteStartControl.yCoord, absoluteEndControl.yCoord),
                getBezierComponent(i.toDouble(), start.zCoord, end.zCoord, absoluteStartControl.zCoord, absoluteEndControl.zCoord)
        )
    }

    private fun getBezierComponent(t: Double, s: Double, e: Double, sc: Double, ec: Double): Double {
        val T = 1 - t
        return T * T * T * s + 3 * T * T * t * sc + 3 * T * t * t * ec + t * t * t * e
    }
}
