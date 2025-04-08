package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.vec
import kotlin.math.max
import kotlin.math.min

public class Rect2d(public val x: Double, public val y: Double, public val width: Double, public val height: Double) {
    public constructor(pos: Vec2d, size: Vec2d): this(
        pos.x, pos.y,
        size.x, size.y
    )

    public val min: Vec2d
        get() = vec(x, y)
    public val max: Vec2d
        get() = vec(x + width, y + height)

    public val minX: Double
        get() = x
    public val minY: Double
        get() = y
    public val maxX: Double
        get() = x + width
    public val maxY: Double
        get() = y + height

    public val pos: Vec2d
        get() = vec(x, y)
    public val size: Vec2d
        get() = vec(width, height)

    public val xf: Float get() = x.toFloat()
    public val yf: Float get() = y.toFloat()
    public val widthf: Float get() = width.toFloat()
    public val heightf: Float get() = height.toFloat()

    public val xi: Int get() = x.toInt()
    public val yi: Int get() = y.toInt()
    public val widthi: Int get() = width.toInt()
    public val heighti: Int get() = height.toInt()

    public fun setX(value: Double): Rect2d {
        return Rect2d(value, y, width, height)
    }

    public fun setY(value: Double): Rect2d {
        return Rect2d(x, value, width, height)
    }

    public fun setWidth(value: Double): Rect2d {
        return Rect2d(x, y, value, height)
    }

    public fun setHeight(value: Double): Rect2d {
        return Rect2d(x, y, width, value)
    }

    public fun expandToFit(point: Vec2d): Rect2d {
        val newX = min(x, point.x)
        val newY = min(y, point.y)
        return Rect2d(newX, newY, max(x + width, point.x) - newX, max(y + width, point.y) - newY)
    }

    public fun expandToFit(rect: Rect2d): Rect2d {
        val min = Vec2d.zip(this.min, rect.min) { a, b -> min(a, b) }
        return Rect2d(
            min,
            Vec2d.zip(this.max, rect.max, min) { a, b, m -> max(a, b) - m }
        )
    }

    /**
     * Test if the provided position is inside this rect, using the half-open range `[min, max)` on each axis
     */
    public operator fun contains(point: Vec2d): Boolean {
        return point.x >= minX && point.y >= minY && point.x < maxX && point.y < maxY
    }

    /**
     * Test if the provided rect is inside this rect, using the open range `[min, max]` on each axis. Passing this
     * rect to itself will return `true`
     */
    public operator fun contains(other: Rect2d): Boolean {
        return other.minX >= this.minX && other.minY >= this.minY && other.maxX <= this.maxX && other.maxY <= this.maxY
    }

    /**
     * Clamps the passed point's X and Y coordinates to within this rect
     */
    public fun clamp(point: Vec2d): Vec2d {
        if (point in this) return point
        return vec(
            point.x.clamp(pos.x, pos.x + size.x),
            point.y.clamp(pos.y, pos.y + size.y)
        )
    }

    /**
     * Offset the rect by adding the given offset to its position
     */
    public fun offset(offset: Vec2d): Rect2d {
        return Rect2d(this.pos + offset, this.size)
    }

    /**
     * Expand the rect by adding the given offset to its size
     */
    public fun expand(offset: Vec2d): Rect2d {
        return Rect2d(this.pos, this.size + offset)
    }

    /**
     * Grow the rect by offsetting all the sides by the given amount
     */
    public fun grow(offset: Double): Rect2d {
        return Rect2d(this.pos - vec(offset, offset), this.size + vec(offset * 2, offset * 2))
    }

    /**
     * Grow the rect by offsetting all the sides by the given amount. Equivalent to `grow(-offset)`.
     */
    public fun shrink(offset: Double): Rect2d {
        return Rect2d(this.pos + vec(offset, offset), this.size - vec(offset * 2, offset * 2))
    }

    /**
     * Shift each side by the given amount
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline fun offset(minX: Number, minY: Number, maxX: Number, maxY: Number): Rect2d =
        offset(minX.toDouble(), minY.toDouble(), maxX.toDouble(), maxY.toDouble())

    /**
     * Shift each side by the given amount
     */
    public fun offset(minX: Double, minY: Double, maxX: Double, maxY: Double): Rect2d {
        return Rect2d(this.pos + vec(minX, minY), this.size + vec(maxX - minX, maxY - minY))
    }

    /**
     * Add the specified position and size to this rect's position and size
     */
    public fun add(pos: Vec2d, size: Vec2d): Rect2d {
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

    public companion object {
        @JvmField
        public val ZERO: Rect2d = Rect2d(0.0, 0.0, 0.0, 0.0)

        @JvmField
        public val INFINITE: Rect2d = Rect2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    }
}
