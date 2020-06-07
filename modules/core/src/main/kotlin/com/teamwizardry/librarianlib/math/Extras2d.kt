package com.teamwizardry.librarianlib.math


/**
 * One of the 2D axes, [X] or [Y]
 */
enum class Axis2d(val direction: Vec2d) {
    X(vec(1, 0)),
    Y(vec(0, 1));

    val other: Axis2d
        get() = when(this) {
            X -> Y
            Y -> X
        }

    fun get(v: Vec2d): Double {
        return when(this) {
            X -> v.x
            Y -> v.y
        }
    }

    fun set(v: Vec2d, value: Double): Vec2d {
        return when(this) {
            X -> vec(value, v.y)
            Y -> vec(v.x, value)
        }
    }
}

/**
 * One of the four cardinal directions on a 2D plane
 */
enum class Cardinal2d(
    val direction: Vec2d,
    val axis: Axis2d,
    val sign: Int,
    /**
     * The number of clockwise 90° rotations from [UP] to this direction.
     * (UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3)
     */
    val rotation: Int
) {
    UP   (vec( 0, -1), Axis2d.Y, -1, 0),
    DOWN (vec( 0,  1), Axis2d.Y,  1, 2),
    LEFT (vec(-1,  0), Axis2d.X, -1, 3),
    RIGHT(vec( 1,  0), Axis2d.X,  1, 1);

    val opposite: Cardinal2d
        get() {
            return values()[(ordinal+2) % values().size]
        }
}

/**
 * A 2D alignment on any of the edges, corners, or the center of a space. The coordinate space is assumed to have X on
 * the horizontal and the origin in the top-left
 */
enum class Align2d(val x: X, val y: Y) {
    CENTER(X.CENTER, Y.CENTER),
    TOP_CENTER(X.CENTER, Y.TOP),
    TOP_RIGHT(X.RIGHT, Y.TOP),
    CENTER_RIGHT(X.RIGHT, Y.CENTER),
    BOTTOM_RIGHT(X.RIGHT, Y.BOTTOM),
    BOTTOM_CENTER(X.CENTER, Y.BOTTOM),
    BOTTOM_LEFT(X.LEFT, Y.BOTTOM),
    CENTER_LEFT(X.LEFT, Y.CENTER),
    TOP_LEFT(X.LEFT, Y.TOP);

    /**
     * An alignment along the X axis. The positive X axis is assumed to point right.
     */
    enum class X(val direction: Int) {
        LEFT(-1),
        CENTER(0),
        RIGHT(1)
    }

    /**
     * An alignment along the Y axis. The positive Y axis is assumed to point down.
     */
    enum class Y(val direction: Int) {
        TOP(-1),
        CENTER(0),
        BOTTOM(1)
    }

    val opposite: Align2d
        get() {
            if (this == CENTER) return CENTER
            return values()[(ordinal-1 + 4) % (values().size-1) + 1]
        }

    companion object {
        private val map = values().associateBy { it.x to it.y }
        @JvmStatic
        operator fun get(x: Align2d.X, y: Align2d.Y): Align2d {
            return map[x to y]!!
        }
    }
}

