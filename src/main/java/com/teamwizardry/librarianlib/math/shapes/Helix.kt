package com.teamwizardry.librarianlib.math.shapes

import net.minecraft.util.math.Vec3d
import java.util.*

/**
 * Created by Saad on 2/7/2016.
 */
class Helix : IShape3D {

    /**
     * Amount of strands
     */
    private var strands = 1

    /**
     * Points per strand
     */
    private var pointCount = 30

    /**
     * Radius of helix
     */
    private var radius = 5f

    /**
     * Max height a strand will reach
     */
    private var height = 5f

    /**
     * Factor for the curves. Negative values reverse rotation.
     */
    private var curve = 10f

    /**
     * Rotation of the helix (Fraction of PI)
     */
    private var rotation = Math.PI / 4

    /**
     * Will reverse the y axis of the helix
     */
    private var reverse = false

    /**
     * Will reduce the radius incrementally
     */
    private var shrink = true

    /**
     * Center location of the helix
     */
    private var center: Vec3d? = null

    /**
     * Use this for a helix that shrinks/expands

     * @param center  Center point of the helix
     * *
     * @param points  Amount of points to use per strand
     * *
     * @param radius  The maximum radius of the helix. starts from 0 because it shrinks/expands
     * *
     * @param height  Defines the maximum height for the helix to reach
     * *
     * @param strands The number of strands that the helix will have
     * *
     * @param curve   The stiffness of the curvature per strand
     * *
     * @param reverse Will draw the helix upside down starting from a fat bottom to a peak and vice versa
     */
    constructor(center: Vec3d, points: Int, radius: Float, height: Float, strands: Int, curve: Float, reverse: Boolean) {
        this.center = center
        this.pointCount = points
        this.radius = radius
        this.strands = strands
        this.height = height
        this.curve = curve
        this.reverse = reverse
        this.shrink = true
    }

    /**
     * Use this for a helix that does not shrink nor expand. More like a candy cane pipe
     * @param center Center point of the helix
     * *
     * @param points Amount of points to use per strand
     * *
     * @param radius The maximum radius of the helix. starts from 0 because it shrinks/expands
     * *
     * @param height Defines the maximum height for the helix to reach
     * *
     * @param strands The number of strands that the helix will have
     * *
     * @param rotation Will define the amount of rotations the helix will assume. Higher values will increase the amount of spins
     */
    constructor(center: Vec3d, points: Int, radius: Float, height: Float, strands: Int, rotation: Double) {
        this.center = center
        this.pointCount = points
        this.radius = radius
        this.strands = strands
        this.height = height
        this.rotation = rotation
        this.shrink = false
    }

    override val points: ArrayList<Vec3d>
        get() {
            val locs = ArrayList<Vec3d>()
            for (strand in 1..strands) {
                if (shrink) {
                    var y = 0.0
                    if (reverse) y = height.toDouble()
                    for (point in 1..pointCount) {
                        val ratio = point.toFloat() / pointCount
                        val angle = curve.toDouble() * ratio.toDouble() * 2.0 * Math.PI / strands + 2.0 * Math.PI * strand.toDouble() / strands + rotation
                        val x = Math.cos(angle) * ratio.toDouble() * radius.toDouble()
                        val z = Math.sin(angle) * ratio.toDouble() * radius.toDouble()
                        if (reverse)
                            y -= (center!!.yCoord - center!!.yCoord + height) / pointCount
                        else
                            y += (center!!.yCoord - center!!.yCoord + height) / pointCount
                        locs.add(center!!.add(Vec3d(x, y, z)))
                        //locs.add(center.subtract(new Vec3d(x, y, z)));
                    }
                } else {
                    var y = 0.0
                    while (y < height) {
                        val x = radius * Math.cos(y * rotation)
                        val z = radius * Math.sin(y * rotation)
                        locs.add(center!!.add(Vec3d(x, y, z)))
                        y += (center!!.yCoord - center!!.yCoord + height) / pointCount
                        //locs.add(center.subtract(new Vec3d(x, y, z)));
                    }
                }
            }
            if (reverse) Collections.reverse(locs)
            return locs
        }

    val multipleStrandPoints: ArrayList<ArrayList<Vec3d>>
        get() {
            val locs = ArrayList<ArrayList<Vec3d>>()
            for (strand in 1..strands) {
                val strandPoints = ArrayList<Vec3d>()
                if (shrink) {
                    var y = 0.0
                    if (reverse) y = height.toDouble()
                    for (point in 1..pointCount) {
                        val ratio = point.toFloat() / pointCount
                        val angle = curve.toDouble() * ratio.toDouble() * 2.0 * Math.PI / strands + 2.0 * Math.PI * strand.toDouble() / strands + rotation
                        val x = Math.cos(angle) * ratio.toDouble() * radius.toDouble()
                        val z = Math.sin(angle) * ratio.toDouble() * radius.toDouble()
                        if (reverse)
                            y -= (center!!.yCoord - center!!.yCoord + height) / pointCount
                        else
                            y += (center!!.yCoord - center!!.yCoord + height) / pointCount
                        strandPoints.add(center!!.add(Vec3d(x, y, z)))
                    }
                } else {
                    var y = 0.0
                    while (y < height) {
                        val x = radius * Math.cos(y * rotation)
                        val z = radius * Math.sin(y * rotation)
                        strandPoints.add(center!!.add(Vec3d(x, y, z)))
                        y += (center!!.yCoord - center!!.yCoord + height) / pointCount
                    }
                }
                locs.add(strandPoints)
            }
            if (reverse) Collections.reverse(locs)
            return locs
        }
}
