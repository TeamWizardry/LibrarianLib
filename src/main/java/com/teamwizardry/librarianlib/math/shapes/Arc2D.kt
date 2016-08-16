package com.teamwizardry.librarianlib.math.shapes

import com.teamwizardry.librarianlib.math.Vec2d

import java.util.ArrayList

/**
 * Created by Saad on 12/7/2016.
 */
class Arc2D @JvmOverloads constructor(val origin: Vec2d, val target: Vec2d, val height: Float = 2f, val particles: Int = 100) : IShape2D {

    /**
     * Will return a list of points in order that define every point of the arc

     * @return Will return the list of points required
     */
    override val points: ArrayList<Vec2d>
        get() {
            val locs = ArrayList<Vec2d>()
            val link = target.sub(origin)
            val length = link.length().toFloat()
            val pitch = (4 * height / Math.pow(length.toDouble(), 2.0)).toFloat()
            for (i in 0..particles - 1) {
                val tmp = Vec2d(link.x, link.y).normalize()
                val v = Vec2d(tmp.x * length.toDouble() * i.toDouble() / particles, tmp.y * length.toDouble() * i.toDouble() / particles)
                val x = i.toFloat() / particles * length - length / 2
                val y = (-pitch * Math.pow(x.toDouble(), 2.0) + height).toFloat()
                locs.add(origin.add(v).add(Vec2d(0.0, y.toDouble())))
            }
            return locs
        }
}