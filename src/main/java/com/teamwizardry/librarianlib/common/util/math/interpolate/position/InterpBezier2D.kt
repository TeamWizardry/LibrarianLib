package com.teamwizardry.librarianlib.common.util.math.interpolate.position

import com.teamwizardry.librarianlib.common.util.div
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.withY

/**
 * Create a Bézier curve from [start] to [end] with [startControl] and [endControl] as handles for the curvature
 *
 * ![Bézier curve image example](http://imgur.com/bkIoyyR) |
 * P0 is [start], P1 is [startControl], P2 is [endControl], and P3 is [end]
 */
class InterpBezier2D @JvmOverloads constructor(
        val start: Vec2d, val end: Vec2d,
        val startControl: Vec2d = ((start + end) / 2).withY(start.y),
        val endControl: Vec2d = ((start + end) / 2).withY(end.y)
) : InterpFunction<Vec2d> {
    override fun get(i: Float): Vec2d {
        return Vec2d(
                getBezierComponent(i.toDouble(), start.x, end.x, startControl.x, endControl.x),
                getBezierComponent(i.toDouble(), start.y, end.y, startControl.y, endControl.y)
        )
    }

    private fun getBezierComponent(t: Double, s: Double, e: Double, sc: Double, ec: Double): Double {
        val T = 1 - t
        return T * T * T * s + 3 * T * T * t * sc + 3 * T * t * t * ec + t * t * t * e
    }
}
