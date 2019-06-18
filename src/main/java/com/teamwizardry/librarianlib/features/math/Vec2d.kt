package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.fastInvSqrt
import com.teamwizardry.librarianlib.features.kotlin.fastSqrt
import net.minecraft.util.math.MathHelper
import kotlin.math.sqrt

class Vec2d(val x: Double, val y: Double) {

    val xf: Float get() = x.toFloat()
    val yf: Float get() = y.toFloat()
    val xi: Int get() = x.toInt()
    val yi: Int get() = y.toInt()

    init {
        AllocationTracker.vec2dAllocations++
        AllocationTracker.vec2dAllocationStats?.also { stats ->
            stats[this] = stats.getInt(this) + 1
        }
    }

    fun floor(): Vec2d {
        return Vec2d.getPooled(Math.floor(x), Math.floor(y))
    }

    fun ceil(): Vec2d {
        return Vec2d.getPooled(Math.ceil(x), Math.ceil(y))
    }

    fun round(): Vec2d {
        return Vec2d(Math.round(x).toDouble(), Math.round(y).toDouble())
    }

    fun setX(value: Double): Vec2d {
        return Vec2d.getPooled(value, y)
    }

    fun setY(value: Double): Vec2d {
        return Vec2d.getPooled(x, value)
    }

    fun setAxis(axis: Axis2d, value: Double): Vec2d {
        return when(axis) {
            Axis2d.X -> Vec2d.getPooled(value, y)
            Axis2d.Y -> Vec2d.getPooled(x, value)
        }
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

    @get:JvmName("lengthSquared")
    val lengthSquared: Double get() = x * x + y * y

    fun length(): Double {
        return sqrt(x * x + y * y)
    }

    fun fastLength(): Double {
        return fastSqrt(x * x + y * y)
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

        val cs = MathHelper.cos(theta)
        val sn = MathHelper.sin(theta)
        return Vec2d.getPooled(
                this.x * cs - this.y * sn,
                this.x * sn + this.y * cs
        )
    }

    @Deprecated(message = "Deprecated for boxing reasons. Use the primitive version instead.",
            replaceWith = ReplaceWith("this.rotate(theta.toFloat())"))
    fun rotate(theta: Number) = rotate(theta.toFloat())

    //=============================================================================

    operator fun get(axis: Axis2d): Double {
        return when(axis) {
            Axis2d.X -> this.x
            Axis2d.Y -> this.y
        }
    }

    //=============================================================================

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        var temp: Long
        temp = java.lang.Double.doubleToLongBits(x)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(y)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        return x == (other as Vec2d).x && y == other.y
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    companion object {

        @JvmField
        val ZERO = Vec2d(0.0, 0.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val INFINITY = Vec2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val NEG_INFINITY = Vec2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val ONE = Vec2d(1.0, 1.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val X = Vec2d(1.0, 0.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val Y = Vec2d(0.0, 1.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val X_INFINITY = Vec2d(Double.POSITIVE_INFINITY, 0.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val Y_INFINITY = Vec2d(0.0, Double.POSITIVE_INFINITY)

        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val NEG_ONE = Vec2d(-1.0, -1.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val NEG_X = Vec2d(-1.0, 0.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val NEG_Y = Vec2d(0.0, -1.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val NEG_X_INFINITY = Vec2d(Double.NEGATIVE_INFINITY, 0.0)
        @JvmField
        @Deprecated("These are stupid. Past me was stupid.")
        val NEG_Y_INFINITY = Vec2d(0.0, Double.NEGATIVE_INFINITY)

        /**
         * Takes the minimum of each component
         */
        @JvmStatic
        fun min(a: Vec2d, b: Vec2d): Vec2d {
            return Vec2d.getPooled(Math.min(a.x, b.x), Math.min(a.y, b.y))
        }

        /**
         * Takes the maximum of each component
         */
        @JvmStatic
        fun max(a: Vec2d, b: Vec2d): Vec2d {
            return Vec2d.getPooled(Math.max(a.x, b.x), Math.max(a.y, b.y))
        }

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
                AllocationTracker.vec2dPooledAllocations++
                return pool[
                    (xi-poolMin) shl poolBits or (yi-poolMin)
                ]
            }
            return Vec2d(x, y)
        }
    }
}
