@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by TheCodeWarrior
 */

operator fun TextFormatting.plus(str: String) = "$this$str"
operator fun String.plus(form: TextFormatting) = "$this$form"
operator fun TextFormatting.plus(other: TextFormatting) = "$this$other"

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

infix fun Vec3d.cross(other: Vec3d) = this.crossProduct(other)

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

// AxisAlignedBB =======================================================================================================

operator fun AxisAlignedBB.contains(other: Vec3d) =
        this.minX <= other.xCoord && this.maxX >= other.xCoord &&
                this.minY <= other.yCoord && this.maxY >= other.yCoord &&
                this.minZ <= other.zCoord && this.maxZ >= other.zCoord

// Class ===============================================================================================================

fun <T> Class<T>.genericType(index: Int): Type? {
    val genericSuper = genericSuperclass
    return if (genericSuper is ParameterizedType) {
        val args = genericSuper.actualTypeArguments
        if (0 <= index && index < args.size)
            genericSuper.actualTypeArguments[index]
        else null
    } else null
}

fun <T> Class<T>.genericClass(index: Int): Class<*>? {
    val generic = genericType(index) ?: return null
    return if (generic is Class<*>) generic else null
}

@Suppress("UNCHECKED_CAST")
fun <T, O> Class<T>.genericClassTyped(index: Int) = genericClass(index) as Class<O>?
