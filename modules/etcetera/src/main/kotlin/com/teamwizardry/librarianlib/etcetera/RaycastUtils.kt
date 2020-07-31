package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.math.floorInt
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Loops through the blocks a line segment passes through without making any allocations. This object is an iterator
 * that repeatedly returns itself after modifying the [x], [y], and [z] properties. Call [reset] to begin a new segment.
 *
 * The algorithm is based on this page: https://playtechs.blogspot.com/2007/03/raytracing-on-grid.html, integrating some
 * fixes from this comment: https://playtechs.blogspot.com/2007/03/raytracing-on-grid.html#c8721763227361666805
 */
@Suppress("PrivatePropertyName")
class IntersectingBlocksIterator: Iterator<IntersectingBlocksIterator> {
    val x: Int get() = _xOut
    val y: Int get() = _yOut
    val z: Int get() = _zOut

    private var _xOut: Int = 0
    private var _yOut: Int = 0
    private var _zOut: Int = 0

    private var complete: Boolean = true

    // the current position in the algorithm. This is actually one step ahead of what's returned to the user, just
    // because of how the algorithm works out
    private var _x: Int = 0
    private var _y: Int = 0
    private var _z: Int = 0

    private var dx: Double = 0.0
    private var dy: Double = 0.0
    private var dz: Double = 0.0

    private var dt_dx: Double = 0.0
    private var dt_dy: Double = 0.0
    private var dt_dz: Double = 0.0

    private var t: Double = 0.0

    private var n: Int = 0

    private var x_inc: Int = 0
    private var y_inc: Int = 0
    private var z_inc: Int = 0

    private var t_next_x: Double = 0.0
    private var t_next_y: Double = 0.0
    private var t_next_z: Double = 0.0

    /**
     * Resets this iterator to iterate a new line segment
     */
    fun reset(
        x0: Double, y0: Double, z0: Double,
        x1: Double, y1: Double, z1: Double
    ) {
        complete = false

        dx = abs(x1 - x0)
        dy = abs(y1 - y0)
        dz = abs(z1 - z0)

        _x = floorInt(x0)
        _y = floorInt(y0)
        _z = floorInt(z0)

        t = 0.0
        n = 1

        if (dx == 0.0) {
            x_inc = 0
            dt_dx = 0.0
            t_next_x = Double.POSITIVE_INFINITY
        }
        else if (x1 > x0) {
            x_inc = 1
            dt_dx = 1.0 / dx
            n += floorInt(x1) - _x
            t_next_x = (floor(x0) + 1 - x0) * dt_dx
        }
        else {
            x_inc = -1
            dt_dx = 1.0 / dx
            n += _x - floorInt(x1)
            t_next_x = (x0 - floor(x0)) * dt_dx
        }

        if (dy == 0.0) {
            y_inc = 0
            dt_dy = 0.0
            t_next_y = Double.POSITIVE_INFINITY
        }
        else if (y1 > y0) {
            y_inc = 1
            dt_dy = 1.0 / dy
            n += floorInt(y1) - _y
            t_next_y = (floor(y0) + 1 - y0) * dt_dy
        }
        else {
            y_inc = -1
            dt_dy = 1.0 / dy
            n += _y - floorInt(y1)
            t_next_y = (y0 - floor(y0)) * dt_dy
        }

        if (dz == 0.0) {
            z_inc = 0
            dt_dz = 0.0
            t_next_z = Double.POSITIVE_INFINITY
        }
        else if (z1 > z0) {
            z_inc = 1
            dt_dz = 1.0 / dz
            n += floorInt(z1) - _z
            t_next_z = (floor(z0) + 1 - z0) * dt_dz
        }
        else {
            z_inc = -1
            dt_dz = 1.0 / dz
            n += _z - floorInt(z1)
            t_next_z = (z0 - floor(z0)) * dt_dz
        }
    }

    override fun hasNext(): Boolean {
        return !complete
    }

    // the while loop from the source
    override fun next(): IntersectingBlocksIterator {
        if (complete)
            throw NoSuchElementException()
        visit(_x, _y, _z)

        // the break is replaced with `complete = true`, halting before the next iteration.
        if (--n == 0)
            complete = true

        // move forward one step
        if (t_next_x <= t_next_y && t_next_x <= t_next_z) // t_next_x is smallest
        {
            _x += x_inc
            t = t_next_x
            t_next_x += dt_dx
        }
        else if (t_next_y <= t_next_x && t_next_y <= t_next_z) // t_next_y is smallest
        {
            _y += y_inc
            t = t_next_y
            t_next_y += dt_dy
        }
        else // t_next_z is smallest
        {
            _z += z_inc
            t = t_next_z
            t_next_z += dt_dz
        }

        return this
    }

    // the "visitor" function described in the article takes the values and exposes them to the user
    private fun visit(x: Int, y: Int, z: Int) {
        _xOut = x
        _yOut = y
        _zOut = z
    }
}

/**
 * A zero-allocation AABB raycaster. Try to avoid creating a new raycaster for each request, as that negates the
 * "zero-allocation" aspect of it
 */
class DirectRaycaster {
    /**
     * The distance along the raycast that the hit occurred, expressed as a multiple of the ray's direction
     * vector, or positive infinity if no impact occurred. This value may be greater than 1.0.
     */
    var distance: Double = 0.0

    /**
     * The depth of the hit. This is the distance from the entrance to the exit point, expressed as a multiple of the
     * ray's direction vector, or zero if no impact occurred.
     */
    var depth: Double = 0.0

    /**
     * The X component of the impacted face's normal, or 0.0 if no impact occurred
     */
    var normalX: Double = 0.0

    /**
     * The Y component of the impacted face's normal, or 0.0 if no impact occurred
     */
    var normalY: Double = 0.0

    /**
     * The Z component of the impacted face's normal, or 0.0 if no impact occurred
     */
    var normalZ: Double = 0.0

    fun reset() {
        distance = Double.POSITIVE_INFINITY
        depth = 0.0
        normalX = 0.0
        normalY = 0.0
        normalZ = 0.0
    }

    /**
     * Performs a raycast between a ray and an AABB, and stores the result in this object. The ray direction does not
     * need to be normalized, its length will become the unit distance in the result. To find the closest ray hit
     * against multiple AABBs, [reset] this raycaster, then pass true to [cumulative] for each AABB.
     *
     * Algorithm used is from this page: https://tavianator.com/fast-branchless-raybounding-box-intersections/
     *
     * @return true if this cast resulted in a new hit
     */
    fun cast(
        cumulative: Boolean,
        minX: Double, minY: Double, minZ: Double,
        maxX: Double, maxY: Double, maxZ: Double,
        startX: Double, startY: Double, startZ: Double,
        invLengthX: Double, invLengthY: Double, invLengthZ: Double
    ): Boolean {
        val tx1 = (minX - startX) * invLengthX
        val tx2 = (maxX - startX) * invLengthX

        var tmin = min(tx1, tx2)
        var tmax = max(tx1, tx2)

        val ty1 = (minY - startY) * invLengthY
        val ty2 = (maxY - startY) * invLengthY

        tmin = max(tmin, min(ty1, ty2))
        tmax = min(tmax, max(ty1, ty2))

        val tz1 = (minZ - startZ) * invLengthZ
        val tz2 = (maxZ - startZ) * invLengthZ

        tmin = max(tmin, min(tz1, tz2))
        tmax = min(tmax, max(tz1, tz2))

        if (tmax >= tmin && tmax >= 0 && tmin >= 0) {
            // there was a collision of some sort
            if (!cumulative || tmin < distance) {
                normalX = if (tmin == tx1) -1.0 else if (tmin == tx2) 1.0 else 0.0
                normalY = if (tmin == ty1) -1.0 else if (tmin == ty2) 1.0 else 0.0
                normalZ = if (tmin == tz1) -1.0 else if (tmin == tz2) 1.0 else 0.0
                distance = tmin
                depth = tmax - tmin
                return true
            }
        } else if (!cumulative) {
            reset()
        }
        return false
    }
}
