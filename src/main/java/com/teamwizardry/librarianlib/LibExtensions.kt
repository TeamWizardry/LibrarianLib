package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting

/**
 * Created by TheCodeWarrior
 */

operator fun TextFormatting.plus(str: String) = "$this$str"

// Vec3d ===============================================================================================================

operator fun Vec3d.times(other: Vec3d) = Vec3d(this.xCoord * other.xCoord, this.yCoord * other.yCoord, this.zCoord * other.zCoord)
operator fun Vec3d.times(other: Double) = this.scale(other)
operator fun Vec3d.times(other: Float) = this * other.toDouble()
operator fun Vec3d.times(other: Int) = this * other.toDouble()

operator fun Vec3d.div(other: Vec3d) = Vec3d(this.xCoord / other.xCoord, this.yCoord / other.yCoord, this.zCoord / other.zCoord)
operator fun Vec3d.div(other: Double) = this * (1/other)
operator fun Vec3d.div(other: Float) = this / other.toDouble()
operator fun Vec3d.div(other: Int) = this / other.toDouble()

operator fun Vec3d.plus(other: Vec3d) = this.add(other)
operator fun Vec3d.minus(other: Vec3d) = this.subtract(other)
operator fun Vec3d.unaryMinus() = this * -1.0

infix fun Vec3d.dot(other: Vec3d) = this.dotProduct(other)

infix fun Vec3d.cross(other: Vec3d) = this.crossProduct(other)

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

// AxisAlignedBB =======================================================================================================

operator fun AxisAlignedBB.contains(other: Vec3d) =
        this.minX <= other.xCoord && this.maxX >= other.xCoord &&
                this.minY <= other.yCoord && this.maxY >= other.yCoord &&
                this.minZ <= other.zCoord && this.maxZ >= other.zCoord
