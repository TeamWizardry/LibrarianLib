package com.teamwizardry.librarianlib.features.kotlin

import net.minecraft.util.math.MathHelper
import kotlin.math.ceil
import kotlin.math.floor

// Clamping ============================================================================================================

fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
fun Short.clamp(min: Short, max: Short): Short = if (this < min) min else if (this > max) max else this
fun Long.clamp(min: Long, max: Long): Long = if (this < min) min else if (this > max) max else this
fun Byte.clamp(min: Byte, max: Byte): Byte = if (this < min) min else if (this > max) max else this
fun Char.clamp(min: Char, max: Char): Char = if (this < min) min else if (this > max) max else this
fun Float.clamp(min: Float, max: Float): Float = if (this < min) min else if (this > max) max else this
fun Double.clamp(min: Double, max: Double): Double = if (this < min) min else if (this > max) max else this

fun fastCos(angle: Float): Float = MathHelper.cos(angle)
fun fastCos(angle: Double): Double = MathHelper.cos(angle.toFloat()).toDouble()
fun fastSin(angle: Float): Float = MathHelper.sin(angle)
fun fastSin(angle: Double): Double = MathHelper.sin(angle.toFloat()).toDouble()

fun fastSqrt(value: Float): Float = MathHelper.sqrt(value)
fun fastSqrt(value: Double): Double = MathHelper.sqrt(value).toDouble()

fun fastInvSqrt(value: Float): Float = MathHelper.fastInvSqrt(value.toDouble()).toFloat()
fun fastInvSqrt(value: Double): Double = MathHelper.fastInvSqrt(value)

fun floorInt(value: Float): Int = floor(value).toInt()
fun floorInt(value: Double): Int = floor(value).toInt()

fun ceilInt(value: Float): Int = ceil(value).toInt()
fun ceilInt(value: Double): Int = ceil(value).toInt()

// https://gist.github.com/aslakhellesoy/1134482
// "Note that this code only works if all values are integer/long (using float/double gives wrong results in some cases)."

/** round n down to nearest multiple of m */
fun Int.roundDownBy(m: Int): Int {
    if(this >= 0)
        return (this / m) * m
    else
        return ((this - m + 1) / m) * m
}

/** round n up to nearest multiple of m */
fun Int.roundUpBy(m: Int): Int {
    if(this >= 0)
        return ((this + m - 1) / m) * m
    else
        return (this / m) * m
}

/** round n to nearest multiple of m */
fun Int.roundBy(m: Int): Int {
    if((this % m) > m/2)
        return this + m - this%m
    else
        return this - this%m
}

/** round n down to nearest multiple of m */
fun Long.roundDownBy(m: Long): Long {
    if(this >= 0)
        return (this / m) * m
    else
        return ((this - m + 1) / m) * m
}

/** round n up to nearest multiple of m */
fun Long.roundUpBy(m: Long): Long {
    if(this >= 0)
        return ((this + m - 1) / m) * m
    else
        return (this / m) * m
}

/** round n to nearest multiple of m */
fun Long.roundBy(m: Long): Long {
    if((this % m) > m/2)
        return this + m - this%m
    else
        return this - this%m
}
