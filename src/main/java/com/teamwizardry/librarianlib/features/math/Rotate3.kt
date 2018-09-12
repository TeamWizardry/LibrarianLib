@file:JvmName("Rotations3D")
package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.kotlin.angle
import com.teamwizardry.librarianlib.features.kotlin.cross
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import javax.vecmath.AxisAngle4d
import javax.vecmath.Matrix3f
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import kotlin.math.max
import kotlin.math.min

/**
 * @author WireSegal
 * Created at 4:43 PM on 9/11/18.
 */

typealias LinearTransformation = Matrix3f
typealias AffineTransformation = Matrix4f

private val I = Vec3d(1.0, 0.0, 0.0)
private val J = Vec3d(0.0, 1.0, 0.0)
private val K = Vec3d(0.0, 0.0, 1.0)

private val BLOCK_CENTER = Vec3d(0.5, 0.5, 0.5)

private val IDENTITY = LinearTransformation().apply { setIdentity() }

private val Vec3d.javax get() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

private fun identity() = LinearTransformation().apply { setIdentity() }

/**
 * Creates a [LinearTransformation] that rotates inputs:
 * * [xAngle] around the X axis
 * * [yAngle] around the Y axis
 * * [zAngle] around the Z axis
 */
fun rotationMatrix(xAngle: Double, yAngle: Double, zAngle: Double) =
        rotationMatrix(xAngle, I) * rotationMatrix(yAngle, J) * rotationMatrix(zAngle, K)

/**
 * Creates a [LinearTransformation] that rotates inputs [angle] around the axis given by ([axisX], [axisY], [axisZ]).
 */
fun rotationMatrix(angle: Double, axisX: Double, axisY: Double, axisZ: Double) =
        LinearTransformation().apply { set(AxisAngle4d(axisX, axisY, axisZ, angle)) }

/**
 * Creates a [LinearTransformation] that rotates inputs [angle] around the axis given by [axis].
 */
fun rotationMatrix(angle: Double, axis: Vec3i) = rotationMatrix(angle, axis.x * 1.0, axis.y * 1.0, axis.z * 1.0)

/**
 * Creates a [LinearTransformation] that rotates inputs [angle] around the axis given by [axis].
 */
fun rotationMatrix(angle: Double, axis: Vec3d) = rotationMatrix(angle, axis.x, axis.y, axis.z)

/**
 * Creates a [LinearTransformation] that rotates inputs from [from] to [to]. (i.e. north to east)
 */
fun rotationMatrix(from: Vec3d, to: Vec3d): LinearTransformation {
    if (from == -to) return identity().apply { negate() }
    return rotationMatrix(from angle to, from cross to)
}

/**
 * Creates a [LinearTransformation] that rotates inputs from [from] to [to]. (i.e. north to east)
 */
fun rotationMatrix(from: Vec3i, to: Vec3i): LinearTransformation {
    if (from == -to) return identity().apply { negate() }
    return rotationMatrix(from angle to, from cross to)
}

/**
 * Creates a [LinearTransformation] that rotates inputs from [from] to [to]. (i.e. north to east)
 */
fun rotationMatrix(from: EnumFacing, to: EnumFacing) = rotationMatrix(from.directionVec, to.directionVec)

/**
 * Creates an affine transformation matrix that translates inputs by the vector [translate].
 */
fun translation(translate: Vec3d) = AffineTransformation(IDENTITY, translate.javax, 1f)

/**
 * Converts a [LinearTransformation] to an [AffineTransformation] with no translation.
 */
fun LinearTransformation.affine() = withTranslation(Vec3d.ZERO)

/**
 * Converts a [LinearTransformation] to an [AffineTransformation] translated to the point [point].
 */
fun LinearTransformation.withTranslation(point: Vec3d) = AffineTransformation(this, point.javax, 1f)

/**
 * Converts a [LinearTransformation] to an [AffineTransformation] centered around the block center (0.5, 0.5, 0.5).
 */
fun LinearTransformation.aroundCenter() = affine().aroundCenter()

/**
 * Converts a [LinearTransformation] to an [AffineTransformation] centered around the point [point].
 */
fun LinearTransformation.aroundPoint(point: Vec3d) = affine().aroundPoint(point)

/**
 * Centers an [AffineTransformation] around the block center (0.5, 0.5, 0.5).
 */
fun AffineTransformation.aroundCenter() = aroundPoint(BLOCK_CENTER)

/**
 * Centers an [AffineTransformation] around the point [point].
 */
fun AffineTransformation.aroundPoint(point: Vec3d) = translation(point) * this * translation(-point)

/**
 * Applies a [LinearTransformation] to the vector ([x], [y], [z]).
 */
fun LinearTransformation.rotate(x: Double, y: Double, z: Double): Vec3d {
    val newX = m00 * x + m01 * y + m02 * z
    val newY = m10 * x + m11 * y + m12 * z
    val newZ = m20 * x + m21 * y + m22 * z
    return Vec3d(newX, newY, newZ)
}

/**
 * Applies a [LinearTransformation] to the vector [point].
 */
fun LinearTransformation.rotate(point: Vec3d) = rotate(point.x, point.y, point.z)

/**
 * Applies a [LinearTransformation] to the vector [point].
 */
fun LinearTransformation.rotate(point: Vec3i) = rotate(point.x * 1.0, point.y * 1.0, point.z * 1.0)

/**
 * Applies a [LinearTransformation] to the axis-aligned bounding box [boundingBox].
 */
fun LinearTransformation.rotate(boundingBox: AxisAlignedBB): AxisAlignedBB {
    val bbMinX = boundingBox.minX
    val bbMinY = boundingBox.minY
    val bbMinZ = boundingBox.minZ
    val bbMaxX = boundingBox.maxX
    val bbMaxY = boundingBox.maxY
    val bbMaxZ = boundingBox.maxZ

    val p1 = rotate(bbMinX, bbMinY, bbMinZ)
    val p2 = rotate(bbMinX, bbMinY, bbMaxZ)
    val p3 = rotate(bbMinX, bbMaxY, bbMinZ)
    val p4 = rotate(bbMinX, bbMaxY, bbMaxZ)
    val p5 = rotate(bbMaxX, bbMinY, bbMinZ)
    val p6 = rotate(bbMaxX, bbMinY, bbMaxZ)
    val p7 = rotate(bbMaxX, bbMaxY, bbMinZ)
    val p8 = rotate(bbMaxX, bbMaxY, bbMaxZ)

    val minX = min(p1.x, min(p2.x, min(p3.x, min(p4.x, min(p5.x, min(p6.x, min(p7.x, p8.x)))))))
    val minY = min(p1.y, min(p2.y, min(p3.y, min(p4.y, min(p5.y, min(p6.y, min(p7.y, p8.y)))))))
    val minZ = min(p1.z, min(p2.z, min(p3.z, min(p4.z, min(p5.z, min(p6.z, min(p7.z, p8.z)))))))
    val maxX = max(p1.x, max(p2.x, max(p3.x, max(p4.x, max(p5.x, max(p6.x, max(p7.x, p8.x)))))))
    val maxY = max(p1.y, max(p2.y, max(p3.y, max(p4.y, max(p5.y, max(p6.y, max(p7.y, p8.y)))))))
    val maxZ = max(p1.z, max(p2.z, max(p3.z, max(p4.z, max(p5.z, max(p6.z, max(p7.z, p8.z)))))))

    return AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
}

/**
 * Applies an [AffineTransformation] to the vector ([x], [y], [z]).
 */
fun AffineTransformation.rotate(x: Double, y: Double, z: Double): Vec3d {
    val newX = m00 * x + m01 * y + m02 * z + m03
    val newY = m10 * x + m11 * y + m12 * z + m13
    val newZ = m20 * x + m21 * y + m22 * z + m23
    return Vec3d(newX, newY, newZ)
}

/**
 * Applies an [AffineTransformation] to the vector [point].
 */
fun AffineTransformation.rotate(point: Vec3d) = rotate(point.x, point.y, point.z)

/**
 * Applies an [AffineTransformation] to the vector [point].
 */
fun AffineTransformation.rotate(point: Vec3i) = rotate(point.x * 1.0, point.y * 1.0, point.z * 1.0)

/**
 * Applies an [AffineTransformation] to the axis-aligned bounding box [boundingBox].
 */
fun AffineTransformation.rotate(boundingBox: AxisAlignedBB): AxisAlignedBB {
    val bbMinX = boundingBox.minX
    val bbMinY = boundingBox.minY
    val bbMinZ = boundingBox.minZ
    val bbMaxX = boundingBox.maxX
    val bbMaxY = boundingBox.maxY
    val bbMaxZ = boundingBox.maxZ

    val p1 = rotate(bbMinX, bbMinY, bbMinZ)
    val p2 = rotate(bbMinX, bbMinY, bbMaxZ)
    val p3 = rotate(bbMinX, bbMaxY, bbMinZ)
    val p4 = rotate(bbMinX, bbMaxY, bbMaxZ)
    val p5 = rotate(bbMaxX, bbMinY, bbMinZ)
    val p6 = rotate(bbMaxX, bbMinY, bbMaxZ)
    val p7 = rotate(bbMaxX, bbMaxY, bbMinZ)
    val p8 = rotate(bbMaxX, bbMaxY, bbMaxZ)

    val minX = min(p1.x, min(p2.x, min(p3.x, min(p4.x, min(p5.x, min(p6.x, min(p7.x, p8.x)))))))
    val minY = min(p1.y, min(p2.y, min(p3.y, min(p4.y, min(p5.y, min(p6.y, min(p7.y, p8.y)))))))
    val minZ = min(p1.z, min(p2.z, min(p3.z, min(p4.z, min(p5.z, min(p6.z, min(p7.z, p8.z)))))))
    val maxX = max(p1.x, max(p2.x, max(p3.x, max(p4.x, max(p5.x, max(p6.x, max(p7.x, p8.x)))))))
    val maxY = max(p1.y, max(p2.y, max(p3.y, max(p4.y, max(p5.y, max(p6.y, max(p7.y, p8.y)))))))
    val maxZ = max(p1.z, max(p2.z, max(p3.z, max(p4.z, max(p5.z, max(p6.z, max(p7.z, p8.z)))))))

    return AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
}
