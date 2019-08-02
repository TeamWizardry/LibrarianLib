@file:JvmName("MathUtils")
package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.Vec3d
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
fun Short.clamp(min: Short, max: Short): Short = if (this < min) min else if (this > max) max else this
fun Long.clamp(min: Long, max: Long): Long = if (this < min) min else if (this > max) max else this
fun Byte.clamp(min: Byte, max: Byte): Byte = if (this < min) min else if (this > max) max else this
fun Char.clamp(min: Char, max: Char): Char = if (this < min) min else if (this > max) max else this
fun Float.clamp(min: Float, max: Float): Float = if (this < min) min else if (this > max) max else this
fun Double.clamp(min: Double, max: Double): Double = if (this < min) min else if (this > max) max else this

// kotlin-only =========================================================================================================

@Suppress("NOTHING_TO_INLINE")
inline fun <T: Comparable<T>> T.clamp(min: T, max: T): T = if (this < min) min else if (this > max) max else this

@JvmSynthetic
fun floorInt(value: Float): Int = floor(value).toInt()
@JvmSynthetic
fun floorInt(value: Double): Int = floor(value).toInt()

@JvmSynthetic
fun ceilInt(value: Float): Int = ceil(value).toInt()
@JvmSynthetic
fun ceilInt(value: Double): Int = ceil(value).toInt()

@JvmSynthetic
fun roundInt(value: Float): Int = round(value).toInt()
@JvmSynthetic
fun roundInt(value: Double): Int = round(value).toInt()

/**
 * Get `Vec2d` instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
 * number of Vec2d allocations when they are used as intermediates, e.g. when adding one Vec2d to another to offset it,
 * this allocates no objects: `vec(1, 0)`
 */
@Suppress("NOTHING_TO_INLINE")
inline fun vec(x: Number, y: Number): Vec2d = Vec2d.getPooled(x.toDouble(), y.toDouble())

/**
 * Get `Vec2d` instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
 * number of Vec2d allocations when they are used as intermediates, e.g. when adding one Vec2d to another to offset it,
 * this allocates no objects: `MathUtils.vec(1, 0)`
 */
fun vec(x: Double, y: Double): Vec2d = Vec2d.getPooled(x, y)

/**
 * Get `Vec2i` instances, selecting from a pool of small value instances when possible. This can vastly reduce the
 * number of Vec2i allocations when they are used as intermediates, e.g. when adding one Vec2i to another to offset it,
 * this allocates no objects: `MathUtils.veci(1, 0)`
 */
fun veci(x: Int, y: Int): Vec2i = Vec2i.getPooled(x, y)

/**
 * Get `Vec3d` instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
 * number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset it,
 * this allocates no objects: `vec(1, 0, 0)`
 */
@Suppress("NOTHING_TO_INLINE")
inline fun vec(x: Number, y: Number, z: Number): Vec3d = vec(x.toDouble(), y.toDouble(), z.toDouble())

/**
 * Get `Vec3d` instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
 * number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset it,
 * this allocates no objects: `MathUtils.vec(1, 0, 0)`
 */
fun vec(x: Double, y: Double, z: Double): Vec3d = Vec3dPool.getPooled(x, y, z)
//fun veci(x: Int, y: Int, z: Int): Vec2i = Vec3i.getPooled(x, y, z)


private object Vec3dPool {
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

    @JvmStatic
    fun getPooled(x: Double, y: Double, z: Double): Vec3d {
        val xi = x.toInt()
        val yi = y.toInt()
        val zi = z.toInt()
        if (xi.toDouble() == x && xi in poolMin..poolMax &&
            yi.toDouble() == y && yi in poolMin..poolMax &&
            zi.toDouble() == z && zi in poolMin..poolMax) {
//            AllocationTracker.vec3dPooledAllocations++
            return vec3dPool[
                ((xi-poolMin) shl poolBits*2) or ((yi-poolMin) shl poolBits) or (zi-poolMin)
            ]
        }
        val newVec = Vec3d(x, y, z)
//        AllocationTracker.vec3dAllocations++
//        AllocationTracker.vec3dAllocationStats?.also { stats ->
//            stats[newVec] = stats.getInt(newVec) + 1
//        }
        return newVec
    }
}
