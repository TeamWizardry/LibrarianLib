package com.teamwizardry.librarianlib.math.shapes

import net.minecraft.util.math.Vec3d

import java.util.ArrayList

/**
 * Created by Saad on 5/7/2016.
 */
class Arc3D @JvmOverloads constructor(private val origin: Vec3d, private val target: Vec3d, private val height: Float = 2f, private val particles: Int = 100) : IShape3D {
    /**
     * Will return a list of points in order that define every point of the arc

     * @return Will return the list of points required
     */
    override val points: ArrayList<Vec3d>
        get() {
            val locs = ArrayList<Vec3d>()
            val link = target.subtract(origin)
            val length = link.lengthVector().toFloat()
            val pitch = (4 * height / Math.pow(length.toDouble(), 2.0)).toFloat()
            for (i in 0..particles - 1) {
                val tmp = Vec3d(link.xCoord, link.yCoord, link.zCoord).normalize()
                val v = Vec3d(tmp.xCoord * length.toDouble() * i.toDouble() / particles, tmp.yCoord * length.toDouble() * i.toDouble() / particles, tmp.zCoord * length.toDouble() * i.toDouble() / particles)
                val x = i.toFloat() / particles * length - length / 2
                val y = (-pitch * Math.pow(x.toDouble(), 2.0) + height).toFloat()
                locs.add(origin.add(v).add(Vec3d(0.0, y.toDouble(), 0.0)))
            }
            return locs
        }
}
