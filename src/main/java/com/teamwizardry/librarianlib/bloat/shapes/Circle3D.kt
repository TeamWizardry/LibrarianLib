package com.teamwizardry.librarianlib.bloat.shapes

import com.teamwizardry.librarianlib.common.util.math.Matrix4
import net.minecraft.util.math.Vec3d
import java.util.*

/**
 * Created by Saad on 16/7/2016.
 */
class Circle3D : IShape3D {

    /**
     * Particles per arc
     */
    private var particles = 100

    /**
     * The two points the arc will connect from and to
     */
    private var origin: Vec3d

    /**
     * The radius of the circle
     */
    private var radius: Double = 0.toDouble()

    /**
     * Orientation of the circle
     */
    private var pitch: Float = 0.toFloat()
    private var yaw: Float = 0.toFloat()

    private val theta = 0.0

    constructor(origin: Vec3d, radius: Double, particleCount: Int) {
        this.particles = particleCount
        this.origin = origin
        this.radius = radius
        pitch = 0f
        yaw = 0f
    }

    constructor(origin: Vec3d, radius: Double, particleCount: Int, pitch: Float, yaw: Float) {
        this.particles = particleCount
        this.origin = origin
        this.radius = radius
        this.pitch = pitch
        this.yaw = yaw
    }

    /**
     * Will return a list of points in order that define every point of the shape

     * @return Will return the list of points required
     */
    override val points: ArrayList<Vec3d>
        get() {
            val points = ArrayList<Vec3d>()
            for (i in 0..particles) {
                val tempTheta = i * Math.toRadians(360.0 / particles)
                val matrix = Matrix4()
                matrix.rotate(pitch.toDouble(), Vec3d(0.0, -1.0, 0.0))
                matrix.rotate(yaw.toDouble(), Vec3d(1.0, 0.0, 0.0))
                val x = origin.xCoord + radius * Math.sin(tempTheta)
                val y = origin.yCoord
                val z = origin.zCoord + radius * Math.cos(tempTheta)
                matrix.apply(Vec3d(x, y, z))
                points.add(Vec3d(x, y, z))
            }
            return points
        }
}
