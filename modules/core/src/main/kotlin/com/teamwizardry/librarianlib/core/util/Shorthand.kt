/**
 * Shorthands for creating common objects like vectors and resource locations. These functions are available in Java
 * as static members of the `Shorthand` class. e.g.
 *
 * ```java
 * import com.teamwizardry.librarianlib.core.util.Shorthand.vec
 * ```
 */
@file:JvmName("Shorthand")
package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.Vec2i
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
* Shorthand for `new Identifier(location)`
*/
public fun loc(location: String): Identifier = Identifier(location)

/**
* Shorthand for `new Identifier(namespace, path)`
*/
public fun loc(namespace: String, path: String): Identifier = Identifier(namespace, path)

// Vec2d:

/**
* Get [Vec2d] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
* number of Vec2d allocations when they are used as intermediates, e.g. when adding one Vec2d to another to offset
* it, this allocates no objects: `Shorthand.vec(1, 0)`
*/
public fun vec(x: Double, y: Double): Vec2d = Vec2d.getPooled(x, y)

/**
* Get [Vec2d] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
* number of Vec2d allocations when they are used as intermediates, e.g. when adding one Vec2d to another to offset it,
* this allocates no objects: `vec(1, 0)`
*
* This method exists for ease of use in kotlin, where numbers aren't implicitly coerced.
*/
@JvmSynthetic
@Suppress("NOTHING_TO_INLINE")
public inline fun vec(x: Number, y: Number): Vec2d = vec(x.toDouble(), y.toDouble())

// Vec2i:

/**
* Get [Vec2i] instances, selecting from a pool of small value instances when possible. This can vastly reduce the
* number of Vec2i allocations when they are used as intermediates, e.g. when adding one Vec2i to another to offset
* it, this allocates no objects: `Shorthand.ivec(1, 0)`
*/
public fun ivec(x: Int, y: Int): Vec2i = Vec2i.getPooled(x, y)

// Vec3d:

/**
* Get [Vec3d] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
* number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset
* it, this allocates no objects: `Shorthand.vec(1, 0, 0)`
*/
public fun vec(x: Double, y: Double, z: Double): Vec3d = Vec3dPool.getPooled(x, y, z)

/**
* Get [Vec3d] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
* number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset
* it, this allocates no objects: `Shorthand.vec(1, 0, 0)`
*/
public fun vec(pos: BlockPos): Vec3d = vec(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

/**
* Get [Vec3d] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
* number of Vec3d allocations when they are used as intermediates, e.g. when adding one Vec3d to another to offset it,
* this allocates no objects: `vec(1, 0, 0)`
*
* This method exists for ease of use in kotlin, where numbers aren't implicitly coerced.
*/
@JvmSynthetic
@Suppress("NOTHING_TO_INLINE")
public inline fun vec(x: Number, y: Number, z: Number): Vec3d = vec(x.toDouble(), y.toDouble(), z.toDouble())

// Vec3i:

/**
* Get [Vec3i] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
* number of Vec3i allocations when they are used as intermediates, e.g. when adding one Vec3i to another to offset
* it, this allocates no objects: `Shorthand.ivec(1, 0, 0)`
*/
public fun ivec(x: Int, y: Int, z: Int): Vec3i = Vec3iPool.getPooled(x, y, z)

// BlockPos:

/**
* Get [BlockPos] instances, selecting from a pool of small integer instances when possible. This can vastly reduce
* the number of BlockPos allocations when they are used as intermediates, e.g. when adding one BlockPos to another
* to offset it, this allocates no objects: `Shorthand.block(1, 0, 0)`
*/
public fun block(x: Int, y: Int, z: Int): BlockPos = BlockPosPool.getPooled(x, y, z)

/**
* Get [BlockPos] instances, selecting from a pool of small integer instances when possible. This can vastly reduce
* the number of BlockPos allocations when they are used as intermediates, e.g. when adding one BlockPos to another
* to offset it, this allocates no objects: `Shorthand.block(1, 0, 0)`
*/
public fun block(vec: Vec3d): BlockPos = BlockPosPool.getPooled(vec.x.toInt(), vec.y.toInt(), vec.z.toInt())

/**
* Get [BlockPos] instances, selecting from a pool of small integer instances when possible. This can vastly reduce
* the number of BlockPos allocations when they are used as intermediates, e.g. when adding one BlockPos to another
* to offset it, this allocates no objects: `Shorthand.block(1, 0, 0)`
*/
public fun block(vec: Vec3i): BlockPos = BlockPosPool.getPooled(vec.x, vec.y, vec.z)

// Rect2d:

/**
* Convenience method for creating a [Rect2d] instance. This does not do any pooling like the vectors do, but is
* more convenient than `new Rect2d(...)`
*/
public fun rect(x: Double, y: Double, width: Double, height: Double): Rect2d = Rect2d(x, y, width, height)

/**
 * Convenience method for creating a [Rect2d] instance. This does not do any pooling like the vectors do, but is
 * more convenient than `new Rect2d(...)`
 */
public fun rect(pos: Vec2d, size: Vec2d): Rect2d = Rect2d(pos, size)

/**
* Convenience function for creating [Rect2d]s in Kotlin without needing to explicitly convert parameters to doubles.
*/
@JvmSynthetic
@Suppress("NOTHING_TO_INLINE")
public inline fun rect(x: Number, y: Number, width: Number, height: Number): Rect2d = rect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

// Internal:

private object Vec3dPool {
    private val poolBits = 5
    private val poolMask = (1 shl poolBits) - 1
    private val poolMax = (1 shl poolBits - 1) - 1
    private val poolMin = -(1 shl poolBits - 1)
    private val pool = Array(1 shl poolBits * 3) {
        val x = (it shr poolBits * 2) + poolMin
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
            return pool[
                    ((xi - poolMin) shl poolBits * 2) or ((yi - poolMin) shl poolBits) or (zi - poolMin)
            ]
        }
        return Vec3d(x, y, z)
    }
}

private object Vec3iPool {
    private val poolBits = 5
    private val poolMask = (1 shl poolBits) - 1
    private val poolMax = (1 shl poolBits - 1) - 1
    private val poolMin = -(1 shl poolBits - 1)
    private val pool = Array(1 shl poolBits * 3) {
        val x = (it shr poolBits * 2) + poolMin
        val y = (it shr poolBits and poolMask) + poolMin
        val z = (it and poolMask) + poolMin
        Vec3i(x, y, z)
    }

    @JvmStatic
    fun getPooled(x: Int, y: Int, z: Int): Vec3i {
        if (x in poolMin..poolMax &&
            y in poolMin..poolMax &&
            z in poolMin..poolMax) {
            return pool[
                    ((x - poolMin) shl poolBits * 2) or ((y - poolMin) shl poolBits) or (z - poolMin)
            ]
        }
        return Vec3i(x, y, z)
    }
}

private object BlockPosPool {
    private val poolBits = 5
    private val poolMask = (1 shl poolBits) - 1
    private val poolMax = (1 shl poolBits - 1) - 1
    private val poolMin = -(1 shl poolBits - 1)
    private val pool = Array(1 shl poolBits * 3) {
        val x = (it shr poolBits * 2) + poolMin
        val y = (it shr poolBits and poolMask) + poolMin
        val z = (it and poolMask) + poolMin
        BlockPos(x, y, z)
    }

    @JvmStatic
    fun getPooled(x: Int, y: Int, z: Int): BlockPos {
        if (x in poolMin..poolMax &&
            y in poolMin..poolMax &&
            z in poolMin..poolMax) {
            return pool[
                    ((x - poolMin) shl poolBits * 2) or ((y - poolMin) shl poolBits) or (z - poolMin)
            ]
        }
        return BlockPos(x, y, z)
    }
}