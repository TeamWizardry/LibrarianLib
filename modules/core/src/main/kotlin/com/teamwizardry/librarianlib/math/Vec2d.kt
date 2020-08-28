package com.teamwizardry.librarianlib.math

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sqrt

public class Vec2d(public val x: Double, public val y: Double) {
    public val xf: Float get() = x.toFloat()
    public val yf: Float get() = y.toFloat()
    public val xi: Int get() = x.toInt()
    public val yi: Int get() = y.toInt()

    init {
//        AllocationTracker.vec2dAllocations++
//        AllocationTracker.vec2dAllocationStats?.also { stats ->
//            stats[this] = stats.getInt(this) + 1
//        }
    }

// Operations ==========================================================================================================

    public fun floor(): Vec2d {
        return getPooled(floor(x), floor(y))
    }

    public fun ceil(): Vec2d {
        return getPooled(ceil(x), ceil(y))
    }

    public fun round(): Vec2d {
        return getPooled(round(x), round(y))
    }

    public fun floorInt(): Vec2i {
        return Vec2i.getPooled(floorInt(x), floorInt(y))
    }

    public fun ceilInt(): Vec2i {
        return Vec2i.getPooled(ceilInt(x), ceilInt(y))
    }

    public fun roundInt(): Vec2i {
        return Vec2i.getPooled(x.roundToInt(), y.roundToInt())
    }

    @JvmName("add")
    public operator fun plus(other: Vec2d): Vec2d {
        return getPooled(x + other.x, y + other.y)
    }

    public fun add(otherX: Double, otherY: Double): Vec2d {
        return getPooled(x + otherX, y + otherY)
    }

    @JvmName("sub")
    public operator fun minus(other: Vec2d): Vec2d {
        return getPooled(x - other.x, y - other.y)
    }

    public fun sub(otherX: Double, otherY: Double): Vec2d {
        return getPooled(x - otherX, y - otherY)
    }

    @JvmName("mul")
    public operator fun times(other: Vec2d): Vec2d {
        return getPooled(x * other.x, y * other.y)
    }

    public fun mul(otherX: Double, otherY: Double): Vec2d {
        return getPooled(x * otherX, y * otherY)
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline operator fun times(amount: Number): Vec2d = mul(amount.toDouble())
    public fun mul(amount: Double): Vec2d {
        return getPooled(x * amount, y * amount)
    }

    @JvmName("divide")
    public operator fun div(other: Vec2d): Vec2d {
        return getPooled(x / other.x, y / other.y)
    }

    public fun divide(otherX: Double, otherY: Double): Vec2d {
        return getPooled(x / otherX, y / otherY)
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline operator fun div(amount: Number): Vec2d = divide(amount.toDouble())
    public fun divide(amount: Double): Vec2d {
        return getPooled(x / amount, y / amount)
    }

    public infix fun dot(point: Vec2d): Double {
        return x * point.x + y * point.y
    }

    @JvmSynthetic
    public operator fun unaryMinus(): Vec2d = this * -1

// Misc utils ==========================================================================================================

    @get:JvmName("lengthSquared")
    public val lengthSquared: Double
        get() = x * x + y * y

    public fun length(): Double {
        return sqrt(x * x + y * y)
    }

    public fun normalize(): Vec2d {
        val norm = fastInvSqrt(x * x + y * y)
        return getPooled(x / norm, y / norm)
    }

    public fun squareDist(vec: Vec2d): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return d0 * d0 + d1 * d1
    }

    public fun projectOnTo(other: Vec2d): Vec2d {
        val norm = other.normalize()
        return norm.mul(this.dot(norm))
    }

    public fun rotate(theta: Float): Vec2d {
        if (theta == 0f) return this

        val cs = fastCos(theta)
        val sn = fastSin(theta)
        return getPooled(
            this.x * cs - this.y * sn,
            this.x * sn + this.y * cs
        )
    }

    /**
     * Run the passed function on the axes of this vector, then return a vector of the results.
     */
    @JvmSynthetic
    public inline fun map(fn: (Double) -> Double): Vec2d {
        return getPooled(fn(this.x), fn(this.y))
    }

    @JvmSynthetic
    public operator fun component1(): Double = this.x

    @JvmSynthetic
    public operator fun component2(): Double = this.y

//=============================================================================

    override fun toString(): String {
        return "($x,$y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2d) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    public companion object {

        @JvmField
        public val ZERO: Vec2d = Vec2d(0.0, 0.0)

        private val poolBits = 7
        private val poolMask = (1 shl poolBits) - 1
        private val poolMax = (1 shl poolBits - 1) - 1
        private val poolMin = -(1 shl poolBits - 1)

        @JvmStatic
        private val pool = Array(1 shl poolBits * 2) {
            val x = (it shr poolBits) + poolMin
            val y = (it and poolMask) + poolMin
            Vec2d(x.toDouble(), y.toDouble())
        }

        @JvmStatic
        public fun getPooled(x: Double, y: Double): Vec2d {
            val xi = x.toInt()
            val yi = y.toInt()
            if (xi.toDouble() == x && xi in poolMin..poolMax &&
                yi.toDouble() == y && yi in poolMin..poolMax) {
                return pool[
                    (xi - poolMin) shl poolBits or (yi - poolMin)
                ]
            }
            return Vec2d(x, y)
        }

        /**
         * Run the passed function on the axes of the passed vectors, then return a vector of the results.
         */
        @JvmSynthetic
        public inline fun zip(a: Vec2d, b: Vec2d, fn: (a: Double, b: Double) -> Double): Vec2d {
            return getPooled(fn(a.x, b.x), fn(a.y, b.y))
        }

        /**
         * Run the passed function on the axes of the passed vectors, then return a vector of the results.
         */
        @JvmSynthetic
        public inline fun zip(a: Vec2d, b: Vec2d, c: Vec2d, fn: (a: Double, b: Double, c: Double) -> Double): Vec2d {
            return getPooled(fn(a.x, b.x, c.x), fn(a.y, b.y, c.y))
        }

        /**
         * Run the passed function on the axes of the passed vectors, then return a vector of the results.
         */
        @JvmSynthetic
        public inline fun zip(a: Vec2d, b: Vec2d, c: Vec2d, d: Vec2d, fn: (a: Double, b: Double, c: Double, d: Double) -> Double): Vec2d {
            return getPooled(fn(a.x, b.x, c.x, d.x), fn(a.y, b.y, c.y, d.y))
        }
    }
}
