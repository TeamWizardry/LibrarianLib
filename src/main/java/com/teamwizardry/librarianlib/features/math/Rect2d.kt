package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus

class Rect2d(val x: Double, val y: Double, val width: Double, val height: Double) {
    constructor(pos: Vec2d, size: Vec2d) : this(
        pos.x, pos.y,
        size.x, size.y
    )
    init {
        AllocationTracker.rect2dAllocations++
        AllocationTracker.rect2dAllocationStats?.also { stats ->
            stats[this] = stats.getInt(this) + 1
        }
    }

    val min: Vec2d
        get() = vec(x, y)
    val max: Vec2d
        get() = vec(x+width, y+height)

    val pos: Vec2d
        get() = vec(x, y)
    val size: Vec2d
        get() = vec(width, height)

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

    /**
     * Clamps the passed point's X and Y coordinates to within this rect
     */
    fun clamp(point: Vec2d): Vec2d {
        if(point in this) return point
        return vec(
            point.x.clamp(pos.x, pos.x + size.x),
            point.y.clamp(pos.y, pos.y + size.y)
        )
    }

    fun offset(offset: Vec2d): Rect2d {
        return Rect2d(this.pos + offset, this.size)
    }

    fun expand(offset: Vec2d): Rect2d {
        return Rect2d(this.pos, this.size + offset)
    }

    fun grow(offset: Double): Rect2d {
        return Rect2d(this.pos - vec(offset, offset), this.size + vec(offset*2, offset*2))
    }

    fun shrink(offset: Double): Rect2d {
        return Rect2d(this.pos + vec(offset, offset), this.size - vec(offset*2, offset*2))
    }

    fun add(pos: Vec2d, size: Vec2d): Rect2d {
        return Rect2d(this.pos + pos, this.size + size)
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
        val ZERO = Rect2d(0.0, 0.0, 0.0, 0.0)
        @JvmField
        val INFINITE = Rect2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    }
}