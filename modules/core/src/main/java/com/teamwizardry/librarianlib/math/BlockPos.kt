package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.BlockPos

operator fun BlockPos.plus(other: BlockPos): BlockPos = block(this.x + other.x, this.y + other.y, this.z + other.z)
operator fun BlockPos.minus(other: BlockPos): BlockPos = block(this.x - other.x, this.y - other.y, this.z - other.z)
operator fun BlockPos.times(other: BlockPos): BlockPos = block(this.x * other.x, this.y * other.y, this.z * other.z)
operator fun BlockPos.div(other: BlockPos): BlockPos = block(this.x / other.x, this.y / other.y, this.z / other.z)

operator fun BlockPos.div(other: Int): BlockPos = block(this.x / other, this.y / other, this.z / other)

operator fun BlockPos.times(other: Int): BlockPos = block(this.x * other, this.y * other, this.z * other)

operator fun BlockPos.unaryMinus(): BlockPos = this * -1

infix fun BlockPos.cross(other: BlockPos): BlockPos = this.crossProduct(other)

@JvmSynthetic
operator fun BlockPos.component1(): Int = this.x
@JvmSynthetic
operator fun BlockPos.component2(): Int = this.y
@JvmSynthetic
operator fun BlockPos.component3(): Int = this.z
