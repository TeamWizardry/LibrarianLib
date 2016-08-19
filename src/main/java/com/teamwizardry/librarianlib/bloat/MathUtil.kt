package com.teamwizardry.librarianlib.bloat

object MathUtil {

    fun round(value: Double, increment: Double): Double {
        return Math.round(value / increment) * increment
    }

    fun round(value: Float, increment: Float): Float {
        return Math.round(value / increment) * increment
    }

    fun round(value: Int, increment: Int): Int {
        return Math.round(value.toFloat() / increment.toFloat()) * increment
    }

    // ========

    fun clamp(`val`: Double, min: Double, max: Double): Double {
        return Math.max(min, Math.min(max, `val`))
    }

    fun clamp(`val`: Float, min: Float, max: Float): Float {
        return Math.max(min, Math.min(max, `val`))
    }

    fun clamp(`val`: Int, min: Int, max: Int): Int {
        return Math.max(min, Math.min(max, `val`))
    }

    // ========

    fun isLessThanOthers(check: Double, vararg others: Double): Boolean {
        for (other in others) {
            if (check >= other)
                return false
        }
        return true
    }

    fun isLequalToOthers(check: Double, vararg others: Double): Boolean {
        for (other in others) {
            if (check > other)
                return false
        }
        return true
    }

    fun isGreaterThanOthers(check: Double, vararg others: Double): Boolean {
        for (other in others) {
            if (check <= other)
                return false
        }
        return true
    }

    fun isGequalToOthers(check: Double, vararg others: Double): Boolean {
        for (other in others) {
            if (check < other)
                return false
        }
        return true
    }
}
