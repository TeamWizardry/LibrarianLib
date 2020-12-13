package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.math.*
import com.teamwizardry.librarianlib.math.BlockPosPool
import com.teamwizardry.librarianlib.math.Vec3dPool
import com.teamwizardry.librarianlib.math.Vec3iPool
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
 * Shorthands for Java consumption. For kotlin use the top-level functions in `util/kotlin/Misc.kt`
 */
public object Shorthand {
    /**
     * Shorthand for `new ResourceLocation(location)`
     */
    @JvmStatic
    public fun loc(location: String): ResourceLocation = ResourceLocation(location)

    /**
     * Shorthand for `new ResourceLocation(namespace, path)`
     */
    @JvmStatic
    public fun loc(namespace: String, path: String): ResourceLocation = ResourceLocation(namespace, path)

    /**
     * Get [Vec2d] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
     * number of Vec2d allocations when they are used as intermediates, e.g. when adding one Vec2d to another to offset
     * it, this allocates no objects: `Shorthand.vec(1, 0)`
     */
    public fun vec(x: Double, y: Double): Vec2d = Vec2d.getPooled(x, y)

    /**
     * Get [Vec2i] instances, selecting from a pool of small value instances when possible. This can vastly reduce the
     * number of Vec2i allocations when they are used as intermediates, e.g. when adding one Vec2i to another to offset
     * it, this allocates no objects: `Shorthand.ivec(1, 0)`
     */
    public fun ivec(x: Int, y: Int): Vec2i = Vec2i.getPooled(x, y)

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
     * Get [Vec3i] instances, selecting from a pool of small integer instances when possible. This can vastly reduce the
     * number of Vec3i allocations when they are used as intermediates, e.g. when adding one Vec3i to another to offset
     * it, this allocates no objects: `Shorthand.ivec(1, 0, 0)`
     */
    public fun ivec(x: Int, y: Int, z: Int): Vec3i = Vec3iPool.getPooled(x, y, z)

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
}