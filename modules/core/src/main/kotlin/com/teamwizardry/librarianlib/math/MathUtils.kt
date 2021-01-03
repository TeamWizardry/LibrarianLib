@file:JvmName("MathUtils")

package com.teamwizardry.librarianlib.math

import kotlin.math.ceil
import kotlin.math.floor

public fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
public fun Short.clamp(min: Short, max: Short): Short = if (this < min) min else if (this > max) max else this
public fun Long.clamp(min: Long, max: Long): Long = if (this < min) min else if (this > max) max else this
public fun Byte.clamp(min: Byte, max: Byte): Byte = if (this < min) min else if (this > max) max else this
public fun Char.clamp(min: Char, max: Char): Char = if (this < min) min else if (this > max) max else this
public fun Float.clamp(min: Float, max: Float): Float = if (this < min) min else if (this > max) max else this
public fun Double.clamp(min: Double, max: Double): Double = if (this < min) min else if (this > max) max else this

// kotlin-only =========================================================================================================

@Suppress("NOTHING_TO_INLINE")
public inline fun <T: Comparable<T>> T.clamp(min: T, max: T): T = if (this < min) min else if (this > max) max else this

@JvmSynthetic
public fun floorInt(value: Float): Int = floor(value).toInt()

@JvmSynthetic
public fun floorInt(value: Double): Int = floor(value).toInt()

@JvmSynthetic
public fun ceilInt(value: Float): Int = ceil(value).toInt()

@JvmSynthetic
public fun ceilInt(value: Double): Int = ceil(value).toInt()
