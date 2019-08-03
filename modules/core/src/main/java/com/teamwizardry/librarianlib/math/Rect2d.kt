package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.math.clamp
import kotlin.math.max
import kotlin.math.min

class Rect2d(val x: Double, val y: Double, val width: Double, val height: Double) {
    constructor(pos: Vec2d, size: Vec2d) : this(
        pos.x, pos.y,
        size.x, size.y
    )
    init {
//        AllocationTracker.rect2dAllocations++
//        AllocationTracker.rect2dAllocationStats?.also { stats ->
//            stats[this] = stats.getInt(this) + 1
//        }
    }

    val min: Vec2d
        get() = vec(x, y)
    val max: Vec2d
        get() = vec(x+width, y+height)

    val minX: Double
        get() = x
    val minY: Double
        get() = y
    val maxX: Double
        get() = x+width
    val maxY: Double
        get() = y+height

    val pos: Vec2d
        get() = vec(x, y)
    val size: Vec2d
        get() = vec(width, height)

    val xf: Float get() = x.toFloat()
    val yf: Float get() = y.toFloat()
    val widthf: Float get() = width.toFloat()
    val heightf: Float get() = height.toFloat()

    val xi: Int get() = x.toInt()
    val yi: Int get() = y.toInt()
    val widthi: Int get() = width.toInt()
    val heighti: Int get() = height.toInt()

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
        val newX = min(x, point.x)
        val newY = min(y, point.y)
        return Rect2d(newX, newY, max(x+width, point.x) -newX, max(y+width, point.y) -newY)
    }

    fun expandToFit(rect: Rect2d): Rect2d {
        val min = Vec2d.zip(this.min, rect.min) { a, b -> min(a, b) }
        return Rect2d(
            min,
            Vec2d.zip(this.max, rect.max, min) { a, b, m -> max(a, b) - m }
        )
    }

    /**
     * Test if the provided position is inside this rect, using the half-open range `[min, max)` on each axis
     */
    operator fun contains(point: Vec2d): Boolean {
        return point.x >= minX && point.y >= minY && point.x < maxX && point.y < maxY
    }

    /**
     * Test if the provided rect is inside this rect, using the open range `[min, max]` on each axis. Passing this
     * rect to itself will return `true`
     */
    operator fun contains(other: Rect2d): Boolean {
        return other.minX >= this.minX && other.minY >= this.minY && other.maxX <= this.maxX && other.maxY <= this.maxY
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

    @Suppress("NOTHING_TO_INLINE")
    inline fun offset(minX: Number, minY: Number, maxX: Number, maxY: Number): Rect2d =
        offset(minX.toDouble(), minY.toDouble(), maxX.toDouble(), maxY.toDouble())
    fun offset(minX: Double, minY: Double, maxX: Double, maxY: Double): Rect2d {
        return Rect2d(this.pos + vec(minX, minY), this.size + vec(maxX - minX, maxY - minY))
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
