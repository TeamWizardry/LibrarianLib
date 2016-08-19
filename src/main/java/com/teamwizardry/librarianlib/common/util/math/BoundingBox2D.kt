package com.teamwizardry.librarianlib.common.util.math

import com.teamwizardry.librarianlib.common.util.math.Vec2d

class BoundingBox2D(val min: Vec2d, val max: Vec2d) {

    constructor(minX: Double, minY: Double, maxX: Double, maxY: Double) : this(Vec2d(minX, minY), Vec2d(maxX, maxY)) {
    }

    fun union(other: BoundingBox2D): BoundingBox2D {
        return BoundingBox2D(
                Math.min(min.x, other.min.x),
                Math.min(min.y, other.min.y),
                Math.max(max.x, other.max.x),
                Math.max(max.y, other.max.y))
    }

    operator fun contains(other: Vec2d): Boolean{
        return other.x <= max.x && other.x >= min.x && other.y <= max.y && other.y >= min.y
    }

    fun height(): Double {
        return max.y - min.y
    }

    fun width(): Double {
        return max.x - min.x
    }

    fun heightF(): Float {
        return max.yf - min.yf
    }

    fun widthF(): Float {
        return max.xf - min.xf
    }

    fun heightI(): Int {
        return max.yi - min.yi
    }

    fun widthI(): Int {
        return max.xi - min.xi
    }
}
