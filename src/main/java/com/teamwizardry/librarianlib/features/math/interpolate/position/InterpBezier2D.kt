package com.teamwizardry.librarianlib.features.math.interpolate.position

import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.withY

/**
 * Create a Bézier curve from [start] to [end] with [startControl] and [endControl] as handles for the curvature
 *
 * ![Bézier curve image example](http://imgur.com/bkIoyyR) |
 * P0 is [start], P1 is [startControl], P2 is [endControl], and P3 is [end]
 */
class InterpBezier2D @JvmOverloads constructor(
        val start: Vec2d, val end: Vec2d,
        val startControl: Vec2d = ((end - start) / 2).withY(0),
        val endControl: Vec2d = ((start - end) / 2).withY(0)
) : InterpFunction<Vec2d> {
    
    private val absoluteStartControl = start + startControl
    private val absoluteEndControl = end + endControl
    
    override fun get(i: Float): Vec2d {
        return Vec2d(
                getBezierComponent(i.toDouble(), start.x, end.x, absoluteStartControl.x, absoluteEndControl.x),
                getBezierComponent(i.toDouble(), start.y, end.y, absoluteStartControl.y, absoluteEndControl.y)
        )
    }

    private fun getBezierComponent(t: Double, s: Double, e: Double, sc: Double, ec: Double): Double {
        val T = 1 - t
        return T * T * T * s + 3 * T * T * t * sc + 3 * T * t * t * ec + t * t * t * e
    }
}
