package com.teamwizardry.librarianlib

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting

/**
 * Created by TheCodeWarrior
 */

operator fun TextFormatting.plus(str: String) : String {
    return this.toString() + str
}

// Vec3d ===============================================================================================================

operator fun Vec3d.plus(other: Vec3d) : Vec3d {
    return this.add(other)
}

operator fun Vec3d.minus(other: Vec3d) : Vec3d {
    return this.subtract(other)
}

operator fun Vec3d.times(other: Vec3d) : Vec3d {
    return Vec3d(this.xCoord*other.xCoord, this.yCoord*other.yCoord, this.zCoord*other.zCoord)
}

operator fun Vec3d.times(other: Double) : Vec3d {
    return Vec3d(this.xCoord*other, this.yCoord*other, this.zCoord*other)
}
operator fun Vec3d.times(other: Float) = this * other as Double
operator fun Vec3d.times(other: Int) = this * other as Double

operator fun Vec3d.div(other: Vec3d) : Vec3d {
    return Vec3d(this.xCoord/other.xCoord, this.yCoord/other.yCoord, this.zCoord/other.zCoord)
}

operator fun Vec3d.div(other: Double) : Vec3d {
    return Vec3d(this.xCoord/other, this.yCoord/other, this.zCoord/other)
}
operator fun Vec3d.div(other: Float) = this / other as Double
operator fun Vec3d.div(other: Int) = this / other as Double

operator fun Vec3d.unaryMinus() : Vec3d {
    return this * -1.0;
}

infix fun Vec3d.dot(other: Vec3d) : Double {
    return this.dotProduct(other)
}

infix fun Vec3d.cross(other: Vec3d) : Vec3d {
    return this.crossProduct(other)
}

// AxisAlignedBB =======================================================================================================

operator fun AxisAlignedBB.contains(other: Vec3d) : Boolean {
    return (
        this.minX <= other.xCoord && this.maxX >= other.xCoord &&
        this.minY <= other.yCoord && this.maxY >= other.yCoord &&
        this.minZ <= other.zCoord && this.maxZ >= other.zCoord
    )
}