package com.teamwizardry.librarianlib.math

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sqrt

class Vec2d(val x: Double, val y: Double) {
    val xf: Float get() = x.toFloat()
    val yf: Float get() = y.toFloat()
    val xi: Int get() = x.toInt()
    val yi: Int get() = y.toInt()

    init {
//        AllocationTracker.vec2dAllocations++
//        AllocationTracker.vec2dAllocationStats?.also { stats ->
//            stats[this] = stats.getInt(this) + 1
//        }
    }

// Operations ==========================================================================================================

    fun floor(): Vec2d {
        return Vec2d.getPooled(floor(x), floor(y))
    }

    fun ceil(): Vec2d {
        return Vec2d.getPooled(ceil(x), ceil(y))
    }

    fun round(): Vec2d {
        return Vec2d.getPooled(round(x), round(y))
    }

    fun floorInt(): Vec2i {
        return Vec2i.getPooled(floorInt(x), floorInt(y))
    }

    fun ceilInt(): Vec2i {
        return Vec2i.getPooled(ceilInt(x), ceilInt(y))
    }

    fun roundInt(): Vec2i {
        return Vec2i.getPooled(roundInt(x), roundInt(y))
    }

    @JvmSynthetic
    operator fun plus(other: Vec2d): Vec2d = add(other)
    fun add(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x + other.x, y + other.y)
    }

    fun add(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x + otherX, y + otherY)
    }

    @JvmSynthetic
    operator fun minus(other: Vec2d): Vec2d = sub(other)
    fun sub(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x - other.x, y - other.y)
    }

    fun sub(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x - otherX, y - otherY)
    }

    @JvmSynthetic
    operator fun times(other: Vec2d): Vec2d = mul(other)
    fun mul(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x * other.x, y * other.y)
    }

    fun mul(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x * otherX, y * otherY)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun times(amount: Number): Vec2d = mul(amount.toDouble())
    fun mul(amount: Double): Vec2d {
        return Vec2d.getPooled(x * amount, y * amount)
    }

    @JvmSynthetic
    operator fun div(other: Vec2d): Vec2d = divide(other)
    fun divide(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x / other.x, y / other.y)
    }

    fun divide(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x / otherX, y / otherY)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun div(amount: Number): Vec2d = divide(amount.toDouble())
    fun divide(amount: Double): Vec2d {
        return Vec2d.getPooled(x / amount, y / amount)
    }

    infix fun dot(point: Vec2d): Double {
        return x * point.x + y * point.y
    }

    @JvmSynthetic
    operator fun unaryMinus(): Vec2d = this * -1

// Misc utils ==========================================================================================================

    @get:JvmName("lengthSquared")
    val lengthSquared: Double get() = x * x + y * y

    fun length(): Double {
        return sqrt(x * x + y * y)
    }

    fun normalize(): Vec2d {
        val norm = fastInvSqrt(x * x + y * y)
        return Vec2d.getPooled(x / norm, y / norm)
    }

    fun squareDist(vec: Vec2d): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return d0 * d0 + d1 * d1
    }

    fun projectOnTo(other: Vec2d): Vec2d {
        val norm = other.normalize()
        return norm.mul(this.dot(norm))
    }

    fun rotate(theta: Float): Vec2d {
        if (theta == 0f) return this

        val cs = fastCos(theta)
        val sn = fastSin(theta)
        return Vec2d.getPooled(
            this.x * cs - this.y * sn,
            this.x * sn + this.y * cs
        )
    }

    /**
     * Run the passed function on the axes of this vector, then return a vector of the results.
     */
    inline fun map(fn: (Double) -> Double): Vec2d {
        return Vec2d.getPooled(fn(this.x), fn(this.y))
    }

    /**
     * Run the passed function on the axes of this vector and the [other] vector, then return a vector of the results.
     */
    inline fun zip(other: Vec2d, fn: (Double, Double) -> Double): Vec2d {
        return Vec2d.getPooled(fn(this.x, other.x), fn(this.y, other.y))
    }

    /**
     * Run the passed function on the axes of this vector and the other vectors, then return a vector of the results.
     */
    inline fun zip(a: Vec2d, b: Vec2d, fn: (Double, Double, Double) -> Double): Vec2d {
        return Vec2d.getPooled(fn(this.x, a.x, b.x), fn(this.y, a.y, b.y))
    }

    @JvmSynthetic
    operator fun component1(): Double = this.x
    @JvmSynthetic
    operator fun component2(): Double = this.y

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

    companion object {

        @JvmField
        val ZERO = Vec2d(0.0, 0.0)

        private val poolBits = 7
        private val poolMask = (1 shl poolBits)-1
        private val poolMax = (1 shl poolBits-1)-1
        private val poolMin = -(1 shl poolBits-1)
        @JvmStatic
        private val pool = Array(1 shl poolBits*2) {
            val x = (it shr poolBits) + poolMin
            val y = (it and poolMask) + poolMin
            Vec2d(x.toDouble(), y.toDouble())
        }

        @JvmStatic
        fun getPooled(x: Double, y: Double): Vec2d {
            val xi = x.toInt()
            val yi = y.toInt()
            if (xi.toDouble() == x && xi in poolMin..poolMax &&
                yi.toDouble() == y && yi in poolMin..poolMax) {
//                AllocationTracker.vec2dPooledAllocations++
                return pool[
                    (xi-poolMin) shl poolBits or (yi-poolMin)
                ]
            }
            return Vec2d(x, y)
        }
    }
}
