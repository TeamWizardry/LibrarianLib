@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.features.math.AllocationTracker
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.util.math.Vec3d

private val poolBits = 5
private val poolMask = (1 shl poolBits)-1
private val poolMax = (1 shl poolBits-1)-1
private val poolMin = -(1 shl poolBits-1)
private val vec3dPool = Array(1 shl poolBits*3) {
    val x = (it shr poolBits*2) + poolMin
    val y = (it shr poolBits and poolMask) + poolMin
    val z = (it and poolMask) + poolMin
    Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}

fun getPooledVec3d(x: Double, y: Double, z: Double): Vec3d {
    val xi = x.toInt()
    val yi = y.toInt()
    val zi = z.toInt()
    if (xi.toDouble() == x && xi in poolMin..poolMax &&
        yi.toDouble() == y && yi in poolMin..poolMax &&
        zi.toDouble() == z && zi in poolMin..poolMax) {
        AllocationTracker.vec3dPooledAllocations++
        return vec3dPool[
            ((xi-poolMin) shl poolBits*2) or ((yi-poolMin) shl poolBits) or (zi-poolMin)
        ]
    }
    val newVec = Vec3d(x, y, z)
    AllocationTracker.vec3dAllocations++
    AllocationTracker.vec3dAllocationStats?.also { stats ->
        stats[newVec] = stats.getInt(newVec) + 1
    }
    return newVec
}

inline fun vec(x: Number, y: Number) = Vec2d.getPooled(x.toDouble(), y.toDouble())
inline fun vec(x: Number, y: Number, z: Number) = getPooledVec3d(x.toDouble(), y.toDouble(), z.toDouble())

inline fun BufferBuilder.pos(x: Number, y: Number, z: Number): BufferBuilder = this.pos(x.toDouble(), y.toDouble(), z.toDouble())
inline fun BufferBuilder.pos(x: Number, y: Number): BufferBuilder = this.pos(x.toDouble(), y.toDouble(), 0.0)

/**
 * Get `Vec3d` instances, selecting from a pool of small integer instances
 */
object Vec3dPool {
    @JvmStatic
    fun create(x: Double, y: Double, z: Double) {
        return vec(x, y, z)
    }
}
