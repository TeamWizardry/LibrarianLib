package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.util.math.vector.Vector3d

@JvmSynthetic
public operator fun Vector3d.plus(other: Vector3d): Vector3d = vec(this.x + other.x, this.y + other.y, this.z + other.z)
@JvmSynthetic
public operator fun Vector3d.minus(other: Vector3d): Vector3d = vec(this.x - other.x, this.y - other.y, this.z - other.z)
@JvmSynthetic
public operator fun Vector3d.times(other: Vector3d): Vector3d = vec(this.x * other.x, this.y * other.y, this.z * other.z)
@JvmSynthetic
public operator fun Vector3d.div(other: Vector3d): Vector3d = vec(this.x / other.x, this.y / other.y, this.z / other.z)

@Suppress("NOTHING_TO_INLINE")
@JvmSynthetic
public inline operator fun Vector3d.div(other: Number): Vector3d = div(other.toDouble())
@JvmSynthetic
public operator fun Vector3d.div(other: Double): Vector3d = vec(this.x / other, this.y / other, this.z / other)

@Suppress("NOTHING_TO_INLINE")
@JvmSynthetic
public inline operator fun Vector3d.times(other: Number): Vector3d = times(other.toDouble())
@JvmSynthetic
public operator fun Vector3d.times(other: Double): Vector3d = vec(this.x * other, this.y * other, this.z * other)

@JvmSynthetic
public operator fun Vector3d.unaryMinus(): Vector3d = this * -1

@JvmSynthetic
public infix fun Vector3d.dot(other: Vector3d): Double = this.dotProduct(other)
@JvmSynthetic
public infix fun Vector3d.cross(other: Vector3d): Vector3d = this.crossProduct(other)

@JvmSynthetic
public operator fun Vector3d.component1(): Double = this.x

@JvmSynthetic
public operator fun Vector3d.component2(): Double = this.y

@JvmSynthetic
public operator fun Vector3d.component3(): Double = this.z
