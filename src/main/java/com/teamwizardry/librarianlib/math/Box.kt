package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

import com.google.common.annotations.VisibleForTesting
import com.teamwizardry.librarianlib.ragdoll.cloth.PointMass3D

class Box(var matrix: Matrix4, var inverse: Matrix4, var minX: Double, var minY: Double, var minZ: Double, var maxX: Double, var maxY: Double, var maxZ: Double) {
    var aabb: AxisAlignedBB

    init {
        this.aabb = AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun fix(vec: Vec3d): Vec3d {
        var vecT = matrix.apply(vec)

        vecT = AABBUtils.closestOutsidePoint(aabb, vecT)

        val vecWorldSpace = inverse.apply(vecT)
        return vecWorldSpace
    }

    fun trace(start: Vec3d, end: Vec3d): Vec3d? {
        var start = start
        var end = end
        start = matrix.apply(start)
        end = matrix.apply(end)
        val result = calculateIntercept(aabb, start, end, false) ?: return null
        return inverse.apply(result)
    }

    fun calculateIntercept(aabb: AxisAlignedBB, vecA: Vec3d, vecB: Vec3d, yOnly: Boolean): Vec3d? {
        var vecX: Vec3d? = null
        var vecY: Vec3d? = null
        var vecZ: Vec3d? = null

        if (vecA.yCoord > vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.maxY, vecA, vecB)
        }

        if (vecA.yCoord < vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.minY, vecA, vecB)
        }

        if (vecY != null) {
            return Vec3d(vecB.xCoord, vecY.yCoord, vecB.zCoord)
        }

        if (yOnly)
            return null

        if (vecA.xCoord > vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.maxX, vecA, vecB)
        }

        if (vecA.xCoord < vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.minX, vecA, vecB)
        }

        if (vecX != null) {
            return Vec3d(vecX.xCoord, vecB.yCoord, vecB.zCoord)
        }

        if (vecA.zCoord > vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.maxZ, vecA, vecB)
        }

        if (vecA.zCoord < vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.minZ, vecA, vecB)
        }

        if (vecZ != null) {
            return Vec3d(vecB.xCoord, vecB.yCoord, vecZ.zCoord)
        }

        return null
    }

    internal fun min(a: Vec3d?, b: Vec3d?): Vec3d? {
        if (a == null && b == null)
            return null
        if (a != null && b == null)
            return a
        if (a == null && b != null)
            return b

        if (b!!.squareDistanceTo(Vec3d.ZERO) < a!!.squareDistanceTo(Vec3d.ZERO))
            return b
        return a
    }

    @VisibleForTesting
    internal fun collideWithXPlane(aabb: AxisAlignedBB, p_186671_1_: Double, p_186671_3_: Vec3d, p_186671_4_: Vec3d): Vec3d? {
        val vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_)
        return if (vec3d != null && this.intersectsWithYZ(aabb, vec3d)) vec3d else null
    }

    @VisibleForTesting
    internal fun collideWithYPlane(aabb: AxisAlignedBB, p_186663_1_: Double, p_186663_3_: Vec3d, p_186663_4_: Vec3d): Vec3d? {
        val vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_)
        return if (vec3d != null && this.intersectsWithXZ(aabb, vec3d)) vec3d else null
    }

    @VisibleForTesting
    internal fun collideWithZPlane(aabb: AxisAlignedBB, p_186665_1_: Double, p_186665_3_: Vec3d, p_186665_4_: Vec3d): Vec3d? {
        val vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_)
        return if (vec3d != null && this.intersectsWithXY(aabb, vec3d)) vec3d else null
    }

    @VisibleForTesting
    fun intersectsWithYZ(aabb: AxisAlignedBB, vec: Vec3d): Boolean {
        val m = -0.0
        return vec.yCoord > aabb.minY + m && vec.yCoord < aabb.maxY - m && vec.zCoord > aabb.minZ + m && vec.zCoord < aabb.maxZ - m
    }

    @VisibleForTesting
    fun intersectsWithXZ(aabb: AxisAlignedBB, vec: Vec3d): Boolean {
        val m = -0.0
        return vec.xCoord > aabb.minX + m && vec.xCoord < aabb.maxX - m && vec.zCoord > aabb.minZ + m && vec.zCoord < aabb.maxZ - m
    }

    @VisibleForTesting
    fun intersectsWithXY(aabb: AxisAlignedBB, vec: Vec3d): Boolean {
        return vec.xCoord > aabb.minX && vec.xCoord < aabb.maxX && vec.yCoord > aabb.minY && vec.yCoord < aabb.maxY
    }
}
