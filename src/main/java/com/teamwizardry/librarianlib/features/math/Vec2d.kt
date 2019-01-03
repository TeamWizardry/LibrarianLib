package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.util.math.MathHelper

class Vec2d(val x: Double, val y: Double) {

    @Transient
    val xf: Float = x.toFloat()
    @Transient
    val yf: Float = y.toFloat()
    @Transient
    val xi: Int = Math.floor(x).toInt()
    @Transient
    val yi: Int = Math.floor(y).toInt()

    fun floor(): Vec2d {
        return Vec2d(Math.floor(x), Math.floor(y))
    }

    fun ceil(): Vec2d {
        return Vec2d(Math.ceil(x), Math.ceil(y))
    }

    fun setX(value: Double): Vec2d {
        return Vec2d(value, y)
    }

    fun setY(value: Double): Vec2d {
        return Vec2d(x, value)
    }

    fun add(other: Vec2d): Vec2d {
        return Vec2d(x + other.x, y + other.y)
    }

    fun add(otherX: Double, otherY: Double): Vec2d {
        return Vec2d(x + otherX, y + otherY)
    }

    fun sub(other: Vec2d): Vec2d {
        return Vec2d(x - other.x, y - other.y)
    }

    fun sub(otherX: Double, otherY: Double): Vec2d {
        return Vec2d(x - otherX, y - otherY)
    }

    fun mul(other: Vec2d): Vec2d {
        return Vec2d(x * other.x, y * other.y)
    }

    fun mul(otherX: Double, otherY: Double): Vec2d {
        return Vec2d(x * otherX, y * otherY)
    }

    fun mul(amount: Double): Vec2d {
        return Vec2d(x * amount, y * amount)
    }

    fun divide(other: Vec2d): Vec2d {
        return Vec2d(x / other.x, y / other.y)
    }

    fun divide(otherX: Double, otherY: Double): Vec2d {
        return Vec2d(x / otherX, y / otherY)
    }

    fun divide(amount: Double): Vec2d {
        return Vec2d(x / amount, y / amount)
    }

    infix fun dot(point: Vec2d): Double {
        return x * point.x + y * point.y
    }

    @delegate:Transient
    private val len by lazy { Math.sqrt(x * x + y * y) }

    fun length(): Double {
        return len
    }

    fun normalize(): Vec2d {
        val norm = length()
        return Vec2d(x / norm, y / norm)
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
        return Vec2d(
                this.x * cs - this.y * sn,
                this.x * sn + this.y * cs
        )
    }

    @Deprecated(message = "Deprecated for boxing reasons. Use the primitive version instead.",
            replaceWith = ReplaceWith("this.rotate(theta.toFloat())"))
    fun rotate(theta: Number) = rotate(theta.toFloat())

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
        val INFINITY = Vec2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        @JvmField
        val NEG_INFINITY = Vec2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

        @JvmField
        val ONE = Vec2d(1.0, 1.0)
        @JvmField
        val X = Vec2d(1.0, 0.0)
        @JvmField
        val Y = Vec2d(0.0, 1.0)
        @JvmField
        val X_INFINITY = Vec2d(Double.POSITIVE_INFINITY, 0.0)
        @JvmField
        val Y_INFINITY = Vec2d(0.0, Double.POSITIVE_INFINITY)

        @JvmField
        val NEG_ONE = Vec2d(-1.0, -1.0)
        @JvmField
        val NEG_X = Vec2d(-1.0, 0.0)
        @JvmField
        val NEG_Y = Vec2d(0.0, -1.0)
        @JvmField
        val NEG_X_INFINITY = Vec2d(Double.NEGATIVE_INFINITY, 0.0)
        @JvmField
        val NEG_Y_INFINITY = Vec2d(0.0, Double.NEGATIVE_INFINITY)

        /**
         * Takes the minimum of each component
         */
        @JvmStatic
        fun min(a: Vec2d, b: Vec2d): Vec2d {
            return Vec2d(Math.min(a.x, b.x), Math.min(a.y, b.y))
        }

        /**
         * Takes the maximum of each component
         */
        @JvmStatic
        fun max(a: Vec2d, b: Vec2d): Vec2d {
            return Vec2d(Math.max(a.x, b.x), Math.max(a.y, b.y))
        }
    }
}

enum class Axis2d(val direction: Vec2d) {
    X(vec(1, 0)),
    Y(vec(0, 1))
}

enum class Cardinal2d(val direction: Vec2d, val axis: Axis2d, val sign: Int) {
    POSITIVE_X(vec( 1,  0), Axis2d.X,  1),
    POSITIVE_Y(vec( 0,  1), Axis2d.Y,  1),
    NEGATIVE_X(vec(-1,  0), Axis2d.X, -1),
    NEGATIVE_Y(vec( 0, -1), Axis2d.Y, -1);

    object GUI {
        @JvmStatic val UP = NEGATIVE_Y
        @JvmStatic val DOWN = POSITIVE_Y
        @JvmStatic val LEFT = NEGATIVE_X
        @JvmStatic val RIGHT = POSITIVE_X
    }
}
