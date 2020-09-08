package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.Vec3i

@JvmSynthetic
public operator fun Vec3i.plus(other: Vec3i): Vec3i = ivec(this.x + other.x, this.y + other.y, this.z + other.z)
@JvmSynthetic
public operator fun Vec3i.minus(other: Vec3i): Vec3i = ivec(this.x - other.x, this.y - other.y, this.z - other.z)
@JvmSynthetic
public operator fun Vec3i.times(other: Vec3i): Vec3i = ivec(this.x * other.x, this.y * other.y, this.z * other.z)
@JvmSynthetic
public operator fun Vec3i.div(other: Vec3i): Vec3i = ivec(this.x / other.x, this.y / other.y, this.z / other.z)

@JvmSynthetic
public operator fun Vec3i.div(other: Int): Vec3i = ivec(this.x / other, this.y / other, this.z / other)

@JvmSynthetic
public operator fun Vec3i.times(other: Int): Vec3i = ivec(this.x * other, this.y * other, this.z * other)

@JvmSynthetic
public operator fun Vec3i.unaryMinus(): Vec3i = this * -1

@JvmSynthetic
public infix fun Vec3i.cross(other: Vec3i): Vec3i = this.crossProduct(other)

@JvmSynthetic
public operator fun Vec3i.component1(): Int = this.x

@JvmSynthetic
public operator fun Vec3i.component2(): Int = this.y

@JvmSynthetic
public operator fun Vec3i.component3(): Int = this.z
