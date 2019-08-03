package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

// adapted from flow/math: https://github.com/flow/math
class MutableMatrix4d: Matrix4d {
    constructor(m: Matrix4d): super(m)
    constructor(): super()
    constructor(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
        m30: Float, m31: Float, m32: Float, m33: Float
    ): super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)
    constructor(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double
    ): super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)

    fun set(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
        m30: Float, m31: Float, m32: Float, m33: Float
    ): MutableMatrix4d = set(
        m00.toDouble(), m01.toDouble(), m02.toDouble(), m03.toDouble(),
        m10.toDouble(), m11.toDouble(), m12.toDouble(), m13.toDouble(),
        m20.toDouble(), m21.toDouble(), m22.toDouble(), m23.toDouble(),
        m30.toDouble(), m31.toDouble(), m32.toDouble(), m33.toDouble()
    )

    fun set(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double
    ): MutableMatrix4d {
        this.m00 = m00
        this.m01 = m01
        this.m02 = m02
        this.m03 = m03
        this.m10 = m10
        this.m11 = m11
        this.m12 = m12
        this.m13 = m13
        this.m20 = m20
        this.m21 = m21
        this.m22 = m22
        this.m23 = m23
        this.m30 = m30
        this.m31 = m31
        this.m32 = m32
        this.m33 = m33
        return this
    }

    fun set(other: Matrix4d): MutableMatrix4d {
        this.m00 = other.m00
        this.m01 = other.m01
        this.m02 = other.m02
        this.m03 = other.m03
        this.m10 = other.m10
        this.m11 = other.m11
        this.m12 = other.m12
        this.m13 = other.m13
        this.m20 = other.m20
        this.m21 = other.m21
        this.m22 = other.m22
        this.m23 = other.m23
        this.m30 = other.m30
        this.m31 = other.m31
        this.m32 = other.m32
        this.m33 = other.m33
        return this
    }

    override fun add(m: Matrix4d): MutableMatrix4d {
        m00 += m.m00
        m01 += m.m01
        m02 += m.m02
        m03 += m.m03
        m10 += m.m10
        m11 += m.m11
        m12 += m.m12
        m13 += m.m13
        m20 += m.m20
        m21 += m.m21
        m22 += m.m22
        m23 += m.m23
        m30 += m.m30
        m31 += m.m31
        m32 += m.m32
        m33 += m.m33
        return this
    }

    override fun plus(m: Matrix4d): MutableMatrix4d {
        return add(m)
    }

    override fun sub(m: Matrix4d): MutableMatrix4d {
        m00 -= m.m00
        m01 -= m.m01
        m02 -= m.m02
        m03 -= m.m03
        m10 -= m.m10
        m11 -= m.m11
        m12 -= m.m12
        m13 -= m.m13
        m20 -= m.m20
        m21 -= m.m21
        m22 -= m.m22
        m23 -= m.m23
        m30 -= m.m30
        m31 -= m.m31
        m32 -= m.m32
        m33 -= m.m33
        return this
    }

    override fun minus(m: Matrix4d): MutableMatrix4d {
        return sub(m)
    }

    override fun mul(a: Float): MutableMatrix4d {
        return mul(a.toDouble())
    }

    override fun times(a: Float): MutableMatrix4d {
        return mul(a)
    }

    override fun mul(a: Double): MutableMatrix4d {
        m00 *= a
        m01 *= a
        m02 *= a
        m03 *= a
        m10 *= a
        m11 *= a
        m12 *= a
        m13 *= a
        m20 *= a
        m21 *= a
        m22 *= a
        m23 *= a
        m30 *= a
        m31 *= a
        m32 *= a
        m33 *= a
        return this
    }

    override fun times(a: Double): MutableMatrix4d {
        return mul(a)
    }

    override fun mul(m: Matrix4d): MutableMatrix4d {
        m00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30
        m01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31
        m02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32
        m03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33
        m10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30
        m11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31
        m12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32
        m13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33
        m20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30
        m21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31
        m22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32
        m23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33
        m30 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30
        m31 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31
        m32 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32
        m33 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33
        return this
    }

    override fun times(m: Matrix4d): MutableMatrix4d {
        return mul(m)
    }

    override fun div(a: Float): MutableMatrix4d {
        return div(a.toDouble())
    }

    override fun div(a: Double): MutableMatrix4d {
        m00 /= a
        m01 /= a
        m02 /= a
        m03 /= a
        m10 /= a
        m11 /= a
        m12 /= a
        m13 /= a
        m20 /= a
        m21 /= a
        m22 /= a
        m23 /= a
        m30 /= a
        m31 /= a
        m32 /= a
        m33 /= a
        return this
    }

    override fun div(m: Matrix4d): MutableMatrix4d {
        return mul(m.invert())
    }

    override fun pow(pow: Float): MutableMatrix4d {
        return pow(pow.toDouble())
    }

    override fun pow(pow: Double): MutableMatrix4d {
        m00 = m00.pow(pow)
        m01 = m01.pow(pow)
        m02 = m02.pow(pow)
        m03 = m03.pow(pow)
        m10 = m10.pow(pow)
        m11 = m11.pow(pow)
        m12 = m12.pow(pow)
        m13 = m13.pow(pow)
        m20 = m20.pow(pow)
        m21 = m21.pow(pow)
        m22 = m22.pow(pow)
        m23 = m23.pow(pow)
        m30 = m30.pow(pow)
        m31 = m31.pow(pow)
        m32 = m32.pow(pow)
        m33 = m33.pow(pow)
        return this
    }

    override fun translate(v: Vec3d): MutableMatrix4d {
        return translate(v.x, v.y, v.z)
    }

    override fun translate(x: Float, y: Float, z: Float): MutableMatrix4d {
        return translate(x.toDouble(), y.toDouble(), z.toDouble())
    }

    override fun translate(x: Double, y: Double, z: Double): MutableMatrix4d {
        return this.set(createTranslation(x, y, z).mul(this))
    }

    override fun scale(scale: Float): MutableMatrix4d {
        return scale(scale.toDouble())
    }

    override fun scale(scale: Double): MutableMatrix4d {
        return scale(scale, scale, scale, scale)
    }

    override fun scale(x: Float, y: Float, z: Float, w: Float): MutableMatrix4d {
        return scale(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
    }

    override fun scale(x: Double, y: Double, z: Double, w: Double): MutableMatrix4d {
        return this.set(createScaling(x, y, z, w).mul(this))
    }

    override fun rotate(rot: Quaternion): MutableMatrix4d {
        return this.set(createRotation(rot).mul(this))
    }

    override fun floor(): MutableMatrix4d {
        m00 = kotlin.math.floor(m00)
        m01 = kotlin.math.floor(m01)
        m02 = kotlin.math.floor(m02)
        m03 = kotlin.math.floor(m03)
        m10 = kotlin.math.floor(m10)
        m11 = kotlin.math.floor(m11)
        m12 = kotlin.math.floor(m12)
        m13 = kotlin.math.floor(m13)
        m20 = kotlin.math.floor(m20)
        m21 = kotlin.math.floor(m21)
        m22 = kotlin.math.floor(m22)
        m23 = kotlin.math.floor(m23)
        m30 = kotlin.math.floor(m30)
        m31 = kotlin.math.floor(m31)
        m32 = kotlin.math.floor(m32)
        m33 = kotlin.math.floor(m33)
        return this
    }

    override fun ceil(): MutableMatrix4d {
        m00 = kotlin.math.ceil(m00)
        m01 = kotlin.math.ceil(m01)
        m02 = kotlin.math.ceil(m02)
        m03 = kotlin.math.ceil(m03)
        m10 = kotlin.math.ceil(m10)
        m11 = kotlin.math.ceil(m11)
        m12 = kotlin.math.ceil(m12)
        m13 = kotlin.math.ceil(m13)
        m20 = kotlin.math.ceil(m20)
        m21 = kotlin.math.ceil(m21)
        m22 = kotlin.math.ceil(m22)
        m23 = kotlin.math.ceil(m23)
        m30 = kotlin.math.ceil(m30)
        m31 = kotlin.math.ceil(m31)
        m32 = kotlin.math.ceil(m32)
        m33 = kotlin.math.ceil(m33)
        return this
    }

    override fun round(): MutableMatrix4d {
        m00 = m00.roundToLong().toDouble()
        m01 = m01.roundToLong().toDouble()
        m02 = m02.roundToLong().toDouble()
        m03 = m03.roundToLong().toDouble()
        m10 = m10.roundToLong().toDouble()
        m11 = m11.roundToLong().toDouble()
        m12 = m12.roundToLong().toDouble()
        m13 = m13.roundToLong().toDouble()
        m20 = m20.roundToLong().toDouble()
        m21 = m21.roundToLong().toDouble()
        m22 = m22.roundToLong().toDouble()
        m23 = m23.roundToLong().toDouble()
        m30 = m30.roundToLong().toDouble()
        m31 = m31.roundToLong().toDouble()
        m32 = m32.roundToLong().toDouble()
        m33 = m33.roundToLong().toDouble()
        return this
    }

    override fun abs(): MutableMatrix4d {
        m00 = kotlin.math.abs(m00)
        m01 = kotlin.math.abs(m01)
        m02 = kotlin.math.abs(m02)
        m03 = kotlin.math.abs(m03)
        m10 = kotlin.math.abs(m10)
        m11 = kotlin.math.abs(m11)
        m12 = kotlin.math.abs(m12)
        m13 = kotlin.math.abs(m13)
        m20 = kotlin.math.abs(m20)
        m21 = kotlin.math.abs(m21)
        m22 = kotlin.math.abs(m22)
        m23 = kotlin.math.abs(m23)
        m30 = kotlin.math.abs(m30)
        m31 = kotlin.math.abs(m31)
        m32 = kotlin.math.abs(m32)
        m33 = kotlin.math.abs(m33)
        return this
    }

    override fun negate(): MutableMatrix4d {
        m00 = -m00
        m01 = -m01
        m02 = -m02
        m03 = -m03
        m10 = -m10
        m11 = -m11
        m12 = -m12
        m13 = -m13
        m20 = -m20
        m21 = -m21
        m22 = -m22
        m23 = -m23
        m30 = -m30
        m31 = -m31
        m32 = -m32
        m33 = -m33
        return this
    }

    override fun unaryMinus(): MutableMatrix4d {
        return negate()
    }

    override fun transpose(): MutableMatrix4d {
        m00 = m00
        m01 = m10
        m02 = m20
        m03 = m30
        m10 = m01
        m11 = m11
        m12 = m21
        m13 = m31
        m20 = m02
        m21 = m12
        m22 = m22
        m23 = m32
        m30 = m03
        m31 = m13
        m32 = m23
        m33 = m33
        return this
    }

    override fun invert(): MutableMatrix4d {
        val det = determinant()
        if (abs(det) < DBL_EPSILON) {
            throw ArithmeticException("Cannot inverse a matrix with a zero determinant")
        }
        m00 = det3(m11, m21, m31, m12, m22, m32, m13, m23, m33) / det
        m01 = -det3(m01, m21, m31, m02, m22, m32, m03, m23, m33) / det
        m02 = det3(m01, m11, m31, m02, m12, m32, m03, m13, m33) / det
        m03 = -det3(m01, m11, m21, m02, m12, m22, m03, m13, m23) / det
        m10 = -det3(m10, m20, m30, m12, m22, m32, m13, m23, m33) / det
        m11 = det3(m00, m20, m30, m02, m22, m32, m03, m23, m33) / det
        m12 = -det3(m00, m10, m30, m02, m12, m32, m03, m13, m33) / det
        m13 = det3(m00, m10, m20, m02, m12, m22, m03, m13, m23) / det
        m20 = det3(m10, m20, m30, m11, m21, m31, m13, m23, m33) / det
        m21 = -det3(m00, m20, m30, m01, m21, m31, m03, m23, m33) / det
        m22 = det3(m00, m10, m30, m01, m11, m31, m03, m13, m33) / det
        m23 = -det3(m00, m10, m20, m01, m11, m21, m03, m13, m23) / det
        m30 = -det3(m10, m20, m30, m11, m21, m31, m12, m22, m32) / det
        m31 = det3(m00, m20, m30, m01, m21, m31, m02, m22, m32) / det
        m32 = -det3(m00, m10, m30, m01, m11, m31, m02, m12, m32) / det
        m33 = det3(m00, m10, m20, m01, m11, m21, m02, m12, m22) / det
        return this
    }

    override fun clone(): MutableMatrix4d {
        return MutableMatrix4d(this)
    }

    fun toImmutable(): Matrix4d = Matrix4d(this)

    companion object {
        private val DBL_EPSILON = Double.fromBits(0x3cb0000000000000L)
    }
}