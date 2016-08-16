package com.teamwizardry.librarianlib.math.shapes

import net.minecraft.util.math.Vec3d

import java.util.ArrayList

/**
 * Created by Saad on 13/7/2016.
 */
class BezierCurve3D @JvmOverloads constructor(point1: Vec3d, point2: Vec3d, pointCount: Int = 50) : IShape3D {

    /**
     * These will do the actual curves
     */
    private val controlPoint1: Vec3d
    private val controlPoint2: Vec3d

    /**
     * Will return a list of points in order that define every point of the shape

     * @return Will return the list of points required
     */
    override val points: ArrayList<Vec3d>

    init {
        points = ArrayList<Vec3d>()

        val midpoint = point1.subtract(point2).scale(1.0 / 2.0)

        controlPoint1 = point1.subtract(midpoint.xCoord, 0.0, midpoint.zCoord)
        controlPoint2 = point2.add(Vec3d(midpoint.xCoord, 0.0, midpoint.zCoord))

        // FORMULA: B(t) = (1-t)**3 p0 + 3(1 - t)**2 t P1 + 3(1-t)t**2 P2 + t**3 P3
        var i = 0f
        while (i < 1) {
            val x = (1 - i).toDouble() * (1 - i).toDouble() * (1 - i).toDouble() * point1.xCoord + 3.0 * (1 - i).toDouble() * (1 - i).toDouble() * i.toDouble() * controlPoint1.xCoord + 3.0 * (1 - i).toDouble() * i.toDouble() * i.toDouble() * controlPoint2.xCoord + i.toDouble() * i.toDouble() * i.toDouble() * point2.xCoord
            val y = (1 - i).toDouble() * (1 - i).toDouble() * (1 - i).toDouble() * point1.yCoord + 3.0 * (1 - i).toDouble() * (1 - i).toDouble() * i.toDouble() * controlPoint1.yCoord + 3.0 * (1 - i).toDouble() * i.toDouble() * i.toDouble() * controlPoint2.yCoord + i.toDouble() * i.toDouble() * i.toDouble() * point2.yCoord
            val z = (1 - i).toDouble() * (1 - i).toDouble() * (1 - i).toDouble() * point1.zCoord + 3.0 * (1 - i).toDouble() * (1 - i).toDouble() * i.toDouble() * controlPoint1.zCoord + 3.0 * (1 - i).toDouble() * i.toDouble() * i.toDouble() * controlPoint2.zCoord + i.toDouble() * i.toDouble() * i.toDouble() * point2.zCoord
            points.add(Vec3d(x, y, z))
            i += (1 / pointCount).toFloat()
        }
    }
}
