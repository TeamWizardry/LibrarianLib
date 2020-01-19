package com.teamwizardry.librarianlib.math

import kotlin.math.sqrt

class Vec2i(val x: Int, val y: Int) {
    val xd: Double get() = x.toDouble()
    val yd: Double get() = y.toDouble()
    val xf: Float get() = x.toFloat()
    val yf: Float get() = y.toFloat()

    init {
//        AllocationTracker.vec2dAllocations++
//        AllocationTracker.vec2dAllocationStats?.also { stats ->
//            stats[this] = stats.getInt(this) + 1
//        }
    }

    @JvmSynthetic
    operator fun plus(other: Vec2i): Vec2i = add(other)
    fun add(other: Vec2i): Vec2i {
        return Vec2i.getPooled(x + other.x, y + other.y)
    }

    fun add(otherX: Int, otherY: Int): Vec2i {
        return Vec2i.getPooled(x + otherX, y + otherY)
    }

    @JvmSynthetic
    operator fun minus(other: Vec2i): Vec2i = sub(other)
    fun sub(other: Vec2i): Vec2i {
        return Vec2i.getPooled(x - other.x, y - other.y)
    }

    fun sub(otherX: Int, otherY: Int): Vec2i {
        return Vec2i.getPooled(x - otherX, y - otherY)
    }

    @JvmSynthetic
    operator fun times(other: Vec2i): Vec2i = mul(other)
    fun mul(other: Vec2i): Vec2i {
        return Vec2i.getPooled(x * other.x, y * other.y)
    }

    fun mul(otherX: Int, otherY: Int): Vec2i {
        return Vec2i.getPooled(x * otherX, y * otherY)
    }

    @JvmSynthetic
    operator fun times(other: Int): Vec2i = mul(other)
    fun mul(amount: Int): Vec2i {
        return Vec2i.getPooled(x * amount, y * amount)
    }

    @JvmSynthetic
    operator fun div(other: Vec2i): Vec2i = divide(other)
    fun divide(other: Vec2i): Vec2i {
        return Vec2i.getPooled(x / other.x, y / other.y)
    }

    fun divide(otherX: Int, otherY: Int): Vec2i {
        return Vec2i.getPooled(x / otherX, y / otherY)
    }

    fun divide(amount: Int): Vec2i {
        return Vec2i.getPooled(x / amount, y / amount)
    }

    infix fun dot(point: Vec2i): Int {
        return x * point.x + y * point.y
    }

    // ===========================================================================

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

    @JvmSynthetic
    operator fun times(other: Double): Vec2d = mul(other)
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

    fun divide(amount: Double): Vec2d {
        return Vec2d.getPooled(x / amount, y / amount)
    }

    infix fun dot(point: Vec2d): Double {
        return x * point.x + y * point.y
    }

    @JvmSynthetic
    operator fun unaryMinus(): Vec2d = this * -1.0

    // ===========================================================================

    @get:JvmName("lengthSquared")
    val lengthSquared: Int get() = x * x + y * y

    fun length(): Double {
        return sqrt(lengthSquared.toDouble())
    }

    fun fastInvLength(): Double {
        return fastInvSqrt(lengthSquared.toDouble())
    }

    fun distanceSquared(vec: Vec2i): Int {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return d0 * d0 + d1 * d1
    }

    fun distance(vec: Vec2i): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return sqrt((d0 * d0 + d1 * d1).toDouble())
    }

    fun fastInvDistance(vec: Vec2i): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return fastInvSqrt((d0 * d0 + d1 * d1).toDouble())
    }

    /**
     * Run the passed function on the axes of this vector, then return a vector of the results.
     */
    inline fun map(fn: (Int) -> Int): Vec2i {
        return Vec2i.getPooled(fn(this.x), fn(this.y))
    }

    /**
     * Run the passed function on the axes of this vector and the [other] vector, then return a vector of the results.
     */
    inline fun zip(other: Vec2i, fn: (Int, Int) -> Int): Vec2i {
        return Vec2i.getPooled(fn(this.x, other.x), fn(this.y, other.y))
    }

    /**
     * Run the passed function on the axes of this vector and the other vectors, then return a vector of the results.
     */
    inline fun zip(a: Vec2i, b: Vec2i, fn: (Int, Int, Int) -> Int): Vec2i {
        return Vec2i.getPooled(fn(this.x, a.x, b.x), fn(this.y, a.y, b.y))
    }

    @JvmSynthetic
    operator fun component1(): Int = this.x
    @JvmSynthetic
    operator fun component2(): Int = this.y

    //=============================================================================

    override fun toString(): String {
        return "($x,$y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2i) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    companion object {

        @JvmField
        val ZERO = Vec2i(0, 0)

        private val poolBits = 7
        private val poolMask = (1 shl poolBits)-1
        private val poolMax = (1 shl poolBits-1)-1
        private val poolMin = -(1 shl poolBits-1)
        @JvmStatic
        private val pool = Array(1 shl poolBits*2) {
            val x = (it shr poolBits) + poolMin
            val y = (it and poolMask) + poolMin
            Vec2i(x, y)
        }

        @JvmStatic
        fun getPooled(x: Int, y: Int): Vec2i {
            if (x in poolMin..poolMax &&
                y in poolMin..poolMax) {
//                AllocationTracker.vec2dPooledAllocations++
                return pool[
                    (x-poolMin) shl poolBits or (y-poolMin)
                ]
            }
            return Vec2i(x, y)
        }
    }
}
