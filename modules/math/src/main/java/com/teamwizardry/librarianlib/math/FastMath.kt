@file:JvmName("FastMath")
package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.MathHelper

fun fastCos(angle: Float): Float = MathHelper.cos(angle)
fun fastCos(angle: Double): Double = MathHelper.cos(angle.toFloat()).toDouble()
fun fastSin(angle: Float): Float = MathHelper.sin(angle)
fun fastSin(angle: Double): Double = MathHelper.sin(angle.toFloat()).toDouble()

fun fastInvSqrt(value: Float): Float = MathHelper.fastInvSqrt(value.toDouble()).toFloat()
fun fastInvSqrt(value: Double): Double = MathHelper.fastInvSqrt(value)
