package com.teamwizardry.librarianlib.math

import kotlin.math.sqrt

public class Vec2i(public val x: Int, public val y: Int) {
    public val xd: Double get() = x.toDouble()
    public val yd: Double get() = y.toDouble()
    public val xf: Float get() = x.toFloat()
    public val yf: Float get() = y.toFloat()

    @JvmName("add")
    public operator fun plus(other: Vec2i): Vec2i {
        return getPooled(x + other.x, y + other.y)
    }

    public fun add(otherX: Int, otherY: Int): Vec2i {
        return getPooled(x + otherX, y + otherY)
    }

    @JvmName("sub")
    public operator fun minus(other: Vec2i): Vec2i {
        return getPooled(x - other.x, y - other.y)
    }

    public fun sub(otherX: Int, otherY: Int): Vec2i {
        return getPooled(x - otherX, y - otherY)
    }

    @JvmName("mul")
    public operator fun times(other: Vec2i): Vec2i {
        return getPooled(x * other.x, y * other.y)
    }

    public fun mul(otherX: Int, otherY: Int): Vec2i {
        return getPooled(x * otherX, y * otherY)
    }

    @JvmName("mul")
    public operator fun times(amount: Int): Vec2i {
        return getPooled(x * amount, y * amount)
    }

    @JvmName("divide")
    public operator fun div(other: Vec2i): Vec2i {
        return getPooled(x / other.x, y / other.y)
    }

    public fun divide(otherX: Int, otherY: Int): Vec2i {
        return getPooled(x / otherX, y / otherY)
    }

    public fun divide(amount: Int): Vec2i {
        return getPooled(x / amount, y / amount)
    }

    public infix fun dot(point: Vec2i): Int {
        return x * point.x + y * point.y
    }

    // ===========================================================================

    @JvmName("add")
    public operator fun plus(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x + other.x, y + other.y)
    }

    public fun add(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x + otherX, y + otherY)
    }

    @JvmName("sub")
    public operator fun minus(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x - other.x, y - other.y)
    }

    public fun sub(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x - otherX, y - otherY)
    }

    @JvmName("mul")
    public operator fun times(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x * other.x, y * other.y)
    }

    public fun mul(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x * otherX, y * otherY)
    }

    @JvmName("mul")
    public operator fun times(amount: Double): Vec2d {
        return Vec2d.getPooled(x * amount, y * amount)
    }

    @JvmName("divide")
    public operator fun div(other: Vec2d): Vec2d {
        return Vec2d.getPooled(x / other.x, y / other.y)
    }

    public fun divide(otherX: Double, otherY: Double): Vec2d {
        return Vec2d.getPooled(x / otherX, y / otherY)
    }

    public fun divide(amount: Double): Vec2d {
        return Vec2d.getPooled(x / amount, y / amount)
    }

    public infix fun dot(point: Vec2d): Double {
        return x * point.x + y * point.y
    }

    @JvmSynthetic
    public operator fun unaryMinus(): Vec2d = this * -1.0

    // ===========================================================================

    @get:JvmName("lengthSquared")
    public val lengthSquared: Int get() = x * x + y * y

    public fun length(): Double {
        return sqrt(lengthSquared.toDouble())
    }

    public fun fastInvLength(): Double {
        return fastInvSqrt(lengthSquared.toDouble())
    }

    public fun distanceSquared(vec: Vec2i): Int {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return d0 * d0 + d1 * d1
    }

    public fun distance(vec: Vec2i): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return sqrt((d0 * d0 + d1 * d1).toDouble())
    }

    public fun fastInvDistance(vec: Vec2i): Double {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return fastInvSqrt((d0 * d0 + d1 * d1).toDouble())
    }

    /**
     * Run the passed function on the axes of this vector, then return a vector of the results.
     */
    @JvmSynthetic
    public inline fun map(fn: (Int) -> Int): Vec2i {
        return getPooled(fn(this.x), fn(this.y))
    }

    /**
     * Run the passed function on the axes of this vector and the [other] vector, then return a vector of the results.
     */
    @JvmSynthetic
    public inline fun zip(other: Vec2i, fn: (Int, Int) -> Int): Vec2i {
        return getPooled(fn(this.x, other.x), fn(this.y, other.y))
    }

    /**
     * Run the passed function on the axes of this vector and the other vectors, then return a vector of the results.
     */
    @JvmSynthetic
    public inline fun zip(a: Vec2i, b: Vec2i, fn: (Int, Int, Int) -> Int): Vec2i {
        return getPooled(fn(this.x, a.x, b.x), fn(this.y, a.y, b.y))
    }

    @JvmSynthetic
    public operator fun component1(): Int = this.x
    @JvmSynthetic
    public operator fun component2(): Int = this.y

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

    public companion object {

        @JvmField
        public val ZERO: Vec2i = Vec2i(0, 0)

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
        public fun getPooled(x: Int, y: Int): Vec2i {
            if (x in poolMin..poolMax &&
                y in poolMin..poolMax) {
                return pool[
                    (x-poolMin) shl poolBits or (y-poolMin)
                ]
            }
            return Vec2i(x, y)
        }
    }
}
