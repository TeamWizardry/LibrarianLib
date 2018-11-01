package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.kotlin.minus

class Rect2d(val x: Double, val y: Double, val width: Double, val height: Double) {
    constructor(pos: Vec2d, size: Vec2d) : this(
        pos.x, pos.y,
        size.x, size.y
    )

    val min: Vec2d
        get() = Vec2d(x, y)
    val max: Vec2d
        get() = Vec2d(x+width, y+height)

    val pos: Vec2d
        get() = Vec2d(x, y)
    val size: Vec2d
        get() = Vec2d(width, height)

    @Transient
    val xf: Float = x.toFloat()
    @Transient
    val yf: Float = y.toFloat()
    @Transient
    val widthf: Float = width.toFloat()
    @Transient
    val heightf: Float = height.toFloat()

    @Transient
    val xi: Int = Math.floor(x).toInt()
    @Transient
    val yi: Int = Math.floor(y).toInt()
    @Transient
    val widthi: Int = Math.floor(width).toInt()
    @Transient
    val heighti: Int = Math.floor(height).toInt()

    fun setX(value: Double): Rect2d {
        return Rect2d(value, y, width, height)
    }

    fun setY(value: Double): Rect2d {
        return Rect2d(x, value, width, height)
    }

    fun setWidth(value: Double): Rect2d {
        return Rect2d(x, y, value, height)
    }

    fun setHeight(value: Double): Rect2d {
        return Rect2d(x, y, width, value)
    }

    fun expandToFit(point: Vec2d): Rect2d {
        val newX = Math.min(x, point.x)
        val newY = Math.min(y, point.y)
        return Rect2d(newX, newY, Math.max(x+width, point.x)-newX, Math.max(y+width, point.y)-newY)
    }

    fun expandToFit(rect: Rect2d): Rect2d {
        return Rect2d(Vec2d.min(rect.min, this.min), Vec2d.max(rect.max, this.max) - Vec2d.min(rect.min, this.min))
    }

    /**
     * Test if the provided position is inside this rect, using the half-open range [min, max) on each axis
     */
    operator fun contains(point: Vec2d): Boolean {
        return point.x >= x && point.y >= y && point.x < x + width && point.y < y + height
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
        temp = java.lang.Double.doubleToLongBits(width)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(height)
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
        return x == (other as Rect2d).x && y == other.y && width == other.width && height == other.height
    }

    override fun toString(): String {
        return "($x,$y,$width,$height)"
    }

    companion object {
        @JvmField
        val ZERO = Vec2d(0.0, 0.0)
    }
}
