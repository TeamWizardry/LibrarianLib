package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.ivec
import net.minecraft.util.math.vector.Vector3i

@JvmSynthetic
public operator fun Vector3i.plus(other: Vector3i): Vector3i = ivec(this.x + other.x, this.y + other.y, this.z + other.z)
@JvmSynthetic
public operator fun Vector3i.minus(other: Vector3i): Vector3i = ivec(this.x - other.x, this.y - other.y, this.z - other.z)
@JvmSynthetic
public operator fun Vector3i.times(other: Vector3i): Vector3i = ivec(this.x * other.x, this.y * other.y, this.z * other.z)
@JvmSynthetic
public operator fun Vector3i.div(other: Vector3i): Vector3i = ivec(this.x / other.x, this.y / other.y, this.z / other.z)

@JvmSynthetic
public operator fun Vector3i.div(other: Int): Vector3i = ivec(this.x / other, this.y / other, this.z / other)

@JvmSynthetic
public operator fun Vector3i.times(other: Int): Vector3i = ivec(this.x * other, this.y * other, this.z * other)

@JvmSynthetic
public operator fun Vector3i.unaryMinus(): Vector3i = this * -1

@JvmSynthetic
public infix fun Vector3i.cross(other: Vector3i): Vector3i = this.crossProduct(other)

@JvmSynthetic
public operator fun Vector3i.component1(): Int = this.x

@JvmSynthetic
public operator fun Vector3i.component2(): Int = this.y

@JvmSynthetic
public operator fun Vector3i.component3(): Int = this.z
