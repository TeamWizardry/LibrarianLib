@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import javax.vecmath.Matrix3d
import javax.vecmath.Matrix3f
import javax.vecmath.Matrix4f
import kotlin.math.acos

// Vec3d ===============================================================================================================

operator fun Vec3d.times(other: Vec3d): Vec3d = Vec3d(x * other.x, y * other.y, z * other.z)
operator fun Vec3d.times(other: Double): Vec3d = scale(other)
operator fun Vec3d.times(other: Float): Vec3d = this * other.toDouble()
operator fun Vec3d.times(other: Int): Vec3d = this * other.toDouble()

operator fun Vec3d.div(other: Vec3d) = Vec3d(x / other.x, y / other.y, z / other.z)
operator fun Vec3d.div(other: Double): Vec3d = this * (1 / other)
operator fun Vec3d.div(other: Float): Vec3d = this / other.toDouble()
operator fun Vec3d.div(other: Int): Vec3d = this / other.toDouble()

operator fun Vec3d.plus(other: Vec3d): Vec3d = add(other)
operator fun Vec3d.minus(other: Vec3d): Vec3d = subtract(other)
operator fun Vec3d.unaryMinus(): Vec3d = this * -1.0

infix fun Vec3d.dot(other: Vec3d) = dotProduct(other)

infix fun Vec3d.cross(other: Vec3d): Vec3d = crossProduct(other)

infix fun Vec3d.angle(other: Vec3d) = acos((this dot other) / (length() * other.length()))

fun Vec3d.withX(other: Double) = Vec3d(other, y, z)
fun Vec3d.withY(other: Double) = Vec3d(x, other, z)
fun Vec3d.withZ(other: Double) = Vec3d(x, y, other)

fun Vec3d.withX(other: Float) = withX(other.toDouble())
fun Vec3d.withY(other: Float) = withY(other.toDouble())
fun Vec3d.withZ(other: Float) = withZ(other.toDouble())

fun Vec3d.withX(other: Int) = withX(other.toDouble())
fun Vec3d.withY(other: Int) = withY(other.toDouble())
fun Vec3d.withZ(other: Int) = withZ(other.toDouble())

val Vec3d.crossMatrix get() = Matrix3d(
        0.0, -z, y,
        z, 0.0, -x,
        -y, x, 0.0)
val Vec3d.tensorMatrix get() = Matrix3d(
        x * x, x * y, x * z,
        y * x, y * y, y * z,
        z * x, z * y, z * z)

operator fun Vec3d.component1() = x
operator fun Vec3d.component2() = y
operator fun Vec3d.component3() = z

// Vec3i ===============================================================================================================


fun Vec3i.lengthSquared() = x * x + y * y + z * z

fun Vec3i.length() = Math.sqrt(lengthSquared().toDouble())

operator fun Vec3i.times(other: Vec3i): Vec3i = Vec3i(x * other.x, y * other.y, z * other.z)
operator fun Vec3i.times(other: Double): Vec3i = Vec3i(x * other, y * other, z * other)
operator fun Vec3i.times(other: Float): Vec3i = this * other.toDouble()
operator fun Vec3i.times(other: Int): Vec3i = this * other.toDouble()

operator fun Vec3i.div(other: Vec3i) = Vec3i(x / other.x, y / other.y, z / other.z)
operator fun Vec3i.div(other: Double): Vec3i = this * (1 / other)
operator fun Vec3i.div(other: Float): Vec3i = this / other.toDouble()
operator fun Vec3i.div(other: Int): Vec3i = this / other.toDouble()

operator fun Vec3i.plus(other: Vec3i): Vec3i = Vec3i(x + other.x, y + other.y, z + other.z)
operator fun Vec3i.minus(other: Vec3i): Vec3i = Vec3i(x - other.x, y - other.y, z - other.z)
operator fun Vec3i.unaryMinus(): Vec3i = this * -1

infix fun Vec3i.dot(other: Vec3i) = x * other.x + y * other.y + z * other.z

infix fun Vec3i.cross(other: Vec3i): Vec3i = crossProduct(other)

infix fun Vec3i.angle(other: Vec3i) = acos((this dot other) / (length() * other.length()))

fun Vec3i.withX(other: Int) = Vec3i(other, y, z)
fun Vec3i.withY(other: Int) = Vec3i(x, other, z)
fun Vec3i.withZ(other: Int) = Vec3i(x, y, other)

val Vec3i.crossMatrix get() = Matrix3d(
        0.0, -z * 1.0, y * 1.0,
        z * 1.0, 0.0, -x * 1.0,
        -y * 1.0, x * 1.0, 0.0)
val Vec3i.tensorMatrix get() = Matrix3d(
        x * x * 1.0, x * y * 1.0, x * z * 1.0,
        y * x * 1.0, y * y * 1.0, y * z * 1.0,
        z * x * 1.0, z * y * 1.0, z * z * 1.0)

operator fun Vec3i.component1() = x
operator fun Vec3i.component2() = y
operator fun Vec3i.component3() = z

fun randomNormal(): Vec3d {
    val yaw = Math.random()*2*Math.PI
    val pitch = Math.random()*Math.PI - Math.PI/2

    var x = Math.sin(yaw)
    var z = Math.cos(yaw)

    val y = Math.sin(pitch)
    val multiplier = Math.cos(pitch)
    x *= multiplier
    z *= multiplier

    return Vec3d(x, y, z)
}



// Vec2d ===============================================================================================================

operator fun Vec2d.times(other: Vec2d) = mul(other)
operator fun Vec2d.times(other: Double) = mul(other)
operator fun Vec2d.times(other: Float) = this * other.toDouble()
operator fun Vec2d.times(other: Int) = this * other.toDouble()

operator fun Vec2d.div(other: Vec2d) = divide(other)
operator fun Vec2d.div(other: Double) = divide(other)
operator fun Vec2d.div(other: Float) = this / other.toDouble()
operator fun Vec2d.div(other: Int) = this / other.toDouble()

operator fun Vec2d.plus(other: Vec2d) = add(other)
operator fun Vec2d.minus(other: Vec2d) = sub(other)
operator fun Vec2d.unaryMinus() = this * -1

fun Vec2d.withX(other: Double) = Vec2d(other, y)
fun Vec2d.withY(other: Double) = Vec2d(x, other)

fun Vec2d.withX(other: Float) = withX(other.toDouble())
fun Vec2d.withY(other: Float) = withY(other.toDouble())

fun Vec2d.withX(other: Int) = withX(other.toDouble())
fun Vec2d.withY(other: Int) = withY(other.toDouble())


// BlockPos ============================================================================================================

operator fun BlockPos.times(other: BlockPos) = BlockPos(x * other.x, y * other.y, z * other.z)
operator fun BlockPos.times(other: Vec3d) = BlockPos((x * other.x).toInt(), (y * other.y).toInt(), (z * other.z).toInt())
operator fun BlockPos.times(other: Number) = BlockPos((x * other.toDouble()).toInt(), (y * other.toDouble()).toInt(), (z * other.toDouble()).toInt())

operator fun BlockPos.div(other: BlockPos) = BlockPos(x / other.x, y / other.y, z / other.z)
operator fun BlockPos.div(other: Vec3d) = BlockPos((x / other.x).toInt(), (y / other.y).toInt(), (z / other.z).toInt())
operator fun BlockPos.div(other: Number) = BlockPos((x / other.toDouble()).toInt(), (y / other.toDouble()).toInt(), (z / other.toDouble()).toInt())

operator fun BlockPos.plus(other: BlockPos): BlockPos = add(other)
operator fun BlockPos.minus(other: BlockPos): BlockPos = subtract(other)


// Matrices ============================================================================================================

operator fun Matrix3f.times(scalar: Float) = apply { mul(scalar) }
operator fun Matrix3f.times(other: Matrix3f) = apply { mul(other) }
operator fun Matrix3f.plus(other: Matrix3f) = apply { add(other) }
operator fun Matrix3f.minus(other: Matrix3f) = apply { sub(other) }

operator fun Matrix4f.times(scalar: Float) = apply { mul(scalar) }
operator fun Matrix4f.times(other: Matrix4f) = apply { mul(other) }
operator fun Matrix4f.plus(other: Matrix4f) = apply { add(other) }
operator fun Matrix4f.minus(other: Matrix4f) = apply { sub(other) }
