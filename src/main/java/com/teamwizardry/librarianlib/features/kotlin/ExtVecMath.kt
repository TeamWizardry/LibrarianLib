@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

// Vec3d ===============================================================================================================

operator fun Vec3d.times(other: Vec3d): Vec3d = Vec3d(this.xCoord * other.xCoord, this.yCoord * other.yCoord, this.zCoord * other.zCoord)
operator fun Vec3d.times(other: Double): Vec3d = this.scale(other)
operator fun Vec3d.times(other: Float): Vec3d = this * other.toDouble()
operator fun Vec3d.times(other: Int): Vec3d = this * other.toDouble()

operator fun Vec3d.div(other: Vec3d) = Vec3d(this.xCoord / other.xCoord, this.yCoord / other.yCoord, this.zCoord / other.zCoord)
operator fun Vec3d.div(other: Double): Vec3d = this * (1 / other)
operator fun Vec3d.div(other: Float): Vec3d = this / other.toDouble()
operator fun Vec3d.div(other: Int): Vec3d = this / other.toDouble()

operator fun Vec3d.plus(other: Vec3d): Vec3d = this.add(other)
operator fun Vec3d.minus(other: Vec3d): Vec3d = this.subtract(other)
operator fun Vec3d.unaryMinus(): Vec3d = this * -1.0

infix fun Vec3d.dot(other: Vec3d) = this.dotProduct(other)

infix fun Vec3d.cross(other: Vec3d): Vec3d = this.crossProduct(other)

fun Vec3d.withX(other: Double) = Vec3d(other, this.yCoord, this.zCoord)
fun Vec3d.withY(other: Double) = Vec3d(this.xCoord, other, this.zCoord)
fun Vec3d.withZ(other: Double) = Vec3d(this.xCoord, this.yCoord, other)

fun Vec3d.withX(other: Float) = this.withX(other.toDouble())
fun Vec3d.withY(other: Float) = this.withY(other.toDouble())
fun Vec3d.withZ(other: Float) = this.withZ(other.toDouble())

fun Vec3d.withX(other: Int) = this.withX(other.toDouble())
fun Vec3d.withY(other: Int) = this.withY(other.toDouble())
fun Vec3d.withZ(other: Int) = this.withZ(other.toDouble())


// Vec2d ===============================================================================================================

operator fun Vec2d.times(other: Vec2d) = this.mul(other)
operator fun Vec2d.times(other: Double) = this.mul(other)
operator fun Vec2d.times(other: Float) = this * other.toDouble()
operator fun Vec2d.times(other: Int) = this * other.toDouble()

operator fun Vec2d.div(other: Vec2d) = this.divide(other)
operator fun Vec2d.div(other: Double) = this.divide(other)
operator fun Vec2d.div(other: Float) = this / other.toDouble()
operator fun Vec2d.div(other: Int) = this / other.toDouble()

operator fun Vec2d.plus(other: Vec2d) = this.add(other)
operator fun Vec2d.minus(other: Vec2d) = this.add(other)
operator fun Vec2d.unaryMinus() = this * -1

fun Vec2d.withX(other: Double) = Vec2d(other, this.y)
fun Vec2d.withY(other: Double) = Vec2d(this.x, other)

fun Vec2d.withX(other: Float) = this.withX(other.toDouble())
fun Vec2d.withY(other: Float) = this.withY(other.toDouble())

fun Vec2d.withX(other: Int) = this.withX(other.toDouble())
fun Vec2d.withY(other: Int) = this.withY(other.toDouble())

// BlockPos ============================================================================================================

operator fun BlockPos.times(other: BlockPos) = BlockPos(this.x * other.x, this.y * other.y, this.z * other.z)
operator fun BlockPos.times(other: Vec3d) = BlockPos((this.x * other.xCoord).toInt(), (this.y * other.yCoord).toInt(), (this.z * other.zCoord).toInt())
operator fun BlockPos.times(other: Number) = BlockPos((this.x * other.toDouble()).toInt(), (this.y * other.toDouble()).toInt(), (this.z * other.toDouble()).toInt())

operator fun BlockPos.div(other: BlockPos) = BlockPos(this.x / other.x, this.y / other.y, this.z / other.z)
operator fun BlockPos.div(other: Vec3d) = BlockPos((this.x / other.xCoord).toInt(), (this.y / other.yCoord).toInt(), (this.z / other.zCoord).toInt())
operator fun BlockPos.div(other: Number) = BlockPos((this.x / other.toDouble()).toInt(), (this.y / other.toDouble()).toInt(), (this.z / other.toDouble()).toInt())

operator fun BlockPos.plus(other: BlockPos) = this.add(other)
operator fun BlockPos.minus(other: BlockPos) = this.subtract(other)

operator fun BlockPos.unaryMinus() = this * -1
