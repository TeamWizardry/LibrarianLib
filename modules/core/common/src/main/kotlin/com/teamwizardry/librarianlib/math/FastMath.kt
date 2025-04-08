@file:JvmName("FastMath")

package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.MathHelper

public fun fastCos(angle: Float): Float = MathHelper.cos(angle)
public fun fastCos(angle: Double): Double = MathHelper.cos(angle.toFloat()).toDouble()
public fun fastSin(angle: Float): Float = MathHelper.sin(angle)
public fun fastSin(angle: Double): Double = MathHelper.sin(angle.toFloat()).toDouble()

public fun fastInvSqrt(value: Float): Float = MathHelper.fastInverseSqrt(value.toDouble()).toFloat()
public fun fastInvSqrt(value: Double): Double = MathHelper.fastInverseSqrt(value)
