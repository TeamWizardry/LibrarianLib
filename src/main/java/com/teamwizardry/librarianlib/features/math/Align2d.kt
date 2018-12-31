package com.teamwizardry.librarianlib.features.math

/**
 * A 2d alignment on any of the edges, corners, or the center of a space. The coordinate space is assumed to have X on
 * the horizontal and the origin in the top-left
 */
enum class Align2d(val x: X, val y: Y) {
    CENTER(X.CENTER, Y.CENTER),
    CENTER_TOP(X.CENTER, Y.TOP),
    RIGHT_TOP(X.RIGHT, Y.TOP),
    RIGHT_CENTER(X.RIGHT, Y.CENTER),
    RIGHT_BOTTOM(X.RIGHT, Y.BOTTOM),
    CENTER_BOTTOM(X.CENTER, Y.BOTTOM),
    LEFT_BOTTOM(X.LEFT, Y.BOTTOM),
    LEFT_CENTER(X.LEFT, Y.CENTER),
    LEFT_TOP(X.LEFT, Y.TOP);

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
}