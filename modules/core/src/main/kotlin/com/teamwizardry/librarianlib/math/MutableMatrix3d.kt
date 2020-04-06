package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.bridge.IMatrix3f
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.client.renderer.Matrix3f
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

// adapted from flow/math: https://github.com/flow/math
open class MutableMatrix3d: Matrix3d {
    constructor(m: Matrix3d): super(m)

    constructor(): super()

    constructor(
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
        m20: Float, m21: Float, m22: Float
    ): super(m00, m01, m02, m10, m11, m12, m20, m21, m22)

    constructor(
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
        m20: Double, m21: Double, m22: Double
    ): super(m00, m01, m02, m10, m11, m12, m20, m21, m22)

    constructor(m: Matrix3f): super(m)

    operator fun set(row: Int, col: Int, value: Double) {
        if(row !in 0 .. 2 || col !in 0 .. 2) {
            throw IllegalArgumentException(
                (if (row < 0 || row > 2) "row must be greater than zero and smaller than 2. " else "") + if (col < 0 || col > 2) "col must be greater than zero and smaller than 2." else "")
        }
        when (row) {
            0 -> when (col) {
                0 -> m00 = value
                1 -> m01 = value
                2 -> m02 = value
            }
            1 -> when (col) {
                0 -> m10 = value
                1 -> m11 = value
                2 -> m12 = value
            }
            2 -> when (col) {
                0 -> m20 = value
                1 -> m21 = value
                2 -> m22 = value
            }
        }
    }

    fun set(
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
        m20: Double, m21: Double, m22: Double
    ): MutableMatrix3d {
        this.m00 = m00
        this.m01 = m01
        this.m02 = m02
        this.m10 = m10
        this.m11 = m11
        this.m12 = m12
        this.m20 = m20
        this.m21 = m21
        this.m22 = m22
        return this
    }

    fun set(m: Matrix3d): MutableMatrix3d {
        this.m00 = m.m00
        this.m01 = m.m01
        this.m02 = m.m02
        this.m10 = m.m10
        this.m11 = m.m11
        this.m12 = m.m12
        this.m20 = m.m20
        this.m21 = m.m21
        this.m22 = m.m22
        return this
    }

    fun set(m: Matrix3f): MutableMatrix3d {
        @Suppress("CAST_NEVER_SUCCEEDS") val imatrix = m as IMatrix3f
        this.m00 = imatrix.m00.toDouble()
        this.m01 = imatrix.m01.toDouble()
        this.m02 = imatrix.m02.toDouble()
        this.m10 = imatrix.m10.toDouble()
        this.m11 = imatrix.m11.toDouble()
        this.m12 = imatrix.m12.toDouble()
        this.m20 = imatrix.m20.toDouble()
        this.m21 = imatrix.m21.toDouble()
        this.m22 = imatrix.m22.toDouble()
        return this
    }

    override fun add(m: Matrix3d): MutableMatrix3d {
        m00 += m.m00
        m01 += m.m01
        m02 += m.m02
        m10 += m.m10
        m11 += m.m11
        m12 += m.m12
        m20 += m.m20
        m21 += m.m21
        m22 += m.m22
        return this
    }

    override fun sub(m: Matrix3d): MutableMatrix3d {
        m00 -= m.m00
        m01 -= m.m01
        m02 -= m.m02
        m10 -= m.m10
        m11 -= m.m11
        m12 -= m.m12
        m20 -= m.m20
        m21 -= m.m21
        m22 -= m.m22
        return this
    }

    override fun mul(a: Double): MutableMatrix3d {
        m00 *= a
        m01 *= a
        m02 *= a
        m10 *= a
        m11 *= a
        m12 *= a
        m20 *= a
        m21 *= a
        m22 *= a
        return this
    }

    override fun mul(m: Matrix3d): MutableMatrix3d {
        m00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20
        m01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21
        m02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22
        m10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20
        m11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21
        m12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22
        m20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20
        m21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21
        m22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22
        return this
    }

    override fun div(a: Double): MutableMatrix3d {
        m00 /= a
        m01 /= a
        m02 /= a
        m10 /= a
        m11 /= a
        m12 /= a
        m20 /= a
        m21 /= a
        m22 /= a
        return this
    }

    override fun pow(pow: Double): MutableMatrix3d {
        m00 = m00.pow(pow)
        m01 = m01.pow(pow)
        m02 = m02.pow(pow)
        m10 = m10.pow(pow)
        m11 = m11.pow(pow)
        m12 = m12.pow(pow)
        m20 = m20.pow(pow)
        m21 = m21.pow(pow)
        m22 = m22.pow(pow)
        return this
    }

    override fun floor(): MutableMatrix3d {
        m00 = floor(m00)
        m01 = floor(m01)
        m02 = floor(m02)
        m10 = floor(m10)
        m11 = floor(m11)
        m12 = floor(m12)
        m20 = floor(m20)
        m21 = floor(m21)
        m22 = floor(m22)
        return this
    }

    override fun ceil(): MutableMatrix3d {
        m00 = ceil(m00)
        m01 = ceil(m01)
        m02 = ceil(m02)
        m10 = ceil(m10)
        m11 = ceil(m11)
        m12 = ceil(m12)
        m20 = ceil(m20)
        m21 = ceil(m21)
        m22 = ceil(m22)
        return this
    }

    override fun round(): MutableMatrix3d {
        m00 = round(m00)
        m01 = round(m01)
        m02 = round(m02)
        m10 = round(m10)
        m11 = round(m11)
        m12 = round(m12)
        m20 = round(m20)
        m21 = round(m21)
        m22 = round(m22)
        return this
    }

    override fun abs(): MutableMatrix3d {
        m00 = abs(m00)
        m01 = abs(m01)
        m02 = abs(m02)
        m10 = abs(m10)
        m11 = abs(m11)
        m12 = abs(m12)
        m20 = abs(m20)
        m21 = abs(m21)
        m22 = abs(m22)
        return this
    }

    override fun negate(): MutableMatrix3d {
        m00 = -m00
        m01 = -m01
        m02 = -m02
        m10 = -m10
        m11 = -m11
        m12 = -m12
        m20 = -m20
        m21 = -m21
        m22 = -m22
        return this
    }

    override fun transpose(): MutableMatrix3d {
        m00 = m00
        m01 = m10
        m02 = m20
        m10 = m01
        m11 = m11
        m12 = m21
        m20 = m02
        m21 = m12
        m22 = m22
        return this
    }

    override fun invert(): MutableMatrix3d {
        val det = determinant()
        if (abs(det) < DBL_EPSILON) {
            throw ArithmeticException("Cannot inverse a matrix with a zero determinant")
        }
        m00 =  (m11 * m22 - m21 * m12) / det
        m01 = -(m01 * m22 - m21 * m02) / det
        m02 =  (m01 * m12 - m02 * m11) / det

        m10 = -(m10 * m22 - m20 * m12) / det
        m11 =  (m00 * m22 - m20 * m02) / det
        m12 = -(m00 * m12 - m10 * m02) / det

        m20 =  (m10 * m21 - m20 * m11) / det
        m21 = -(m00 * m21 - m20 * m01) / det
        m22 =  (m00 * m11 - m01 * m10) / det
        return this
    }

    override fun plus(m: Matrix3d): MutableMatrix3d {
        return add(m)
    }

    @JvmSynthetic
    operator fun plusAssign(m: Matrix3d) {
        add(m)
    }

    override fun minus(m: Matrix3d): MutableMatrix3d {
        return sub(m)
    }

    @JvmSynthetic
    operator fun minusAssign(m: Matrix3d) {
        sub(m)
    }

    override fun mul(a: Float): MutableMatrix3d {
        return mul(a.toDouble())
    }

    override fun times(a: Float): MutableMatrix3d {
        return mul(a)
    }

    @JvmSynthetic
    operator fun timesAssign(a: Float) {
        mul(a)
    }

    override fun times(a: Double): MutableMatrix3d {
        return mul(a)
    }

    @JvmSynthetic
    operator fun timesAssign(a: Double) {
        mul(a)
    }

    override fun times(m: Matrix3d): MutableMatrix3d {
        return mul(m)
    }

    @JvmSynthetic
    operator fun timesAssign(m: Matrix3d) {
        mul(m)
    }

    @JvmSynthetic
    operator fun divAssign(a: Double) {
        div(a)
    }

    override fun div(a: Float): MutableMatrix3d {
        return div(a.toDouble())
    }

    @JvmSynthetic
    operator fun divAssign(a: Float) {
        div(a)
    }

    override fun div(m: Matrix3d): MutableMatrix3d {
        return mul(temporaryMatrix.invert())
    }

    @JvmSynthetic
    operator fun divAssign(m: Matrix3d) {
        div(m)
    }

    override fun pow(pow: Float): MutableMatrix3d {
        return pow(pow.toDouble())
    }

    override fun translate(v: Vec2d): MutableMatrix3d {
        return translate(v.x, v.y)
    }

    override fun translate(x: Float, y: Float): MutableMatrix3d {
        return translate(x.toDouble(), y.toDouble())
    }

    override fun translate(x: Double, y: Double): MutableMatrix3d {
        return this.set(createTranslation(x, y).mul(this))
    }

    override fun scale(scale: Float): MutableMatrix3d {
        return scale(scale.toDouble())
    }

    override fun scale(scale: Double): MutableMatrix3d {
        return scale(scale, scale, scale)
    }

    override fun scale(v: Vec3d): MutableMatrix3d {
        return scale(v.getX(), v.getY(), v.getZ())
    }

    override fun scale(x: Float, y: Float, z: Float): MutableMatrix3d {
        return scale(x.toDouble(), y.toDouble(), z.toDouble())
    }

    override fun scale(x: Double, y: Double, z: Double): MutableMatrix3d {
        return this.set(createScaling(x, y, z).mul(this))
    }

    override fun rotate(rot: Quaternion): MutableMatrix3d {
        return this.set(createRotation(rot).mul(this))
    }

    override fun rotate(axis: Vec3d, angle: Double): Matrix3d {
        return this.set(createRotation(axis, angle).mul(this))
    }

    override fun unaryMinus(): MutableMatrix3d {
        return negate()
    }

    override fun clone(): MutableMatrix3d {
        return MutableMatrix3d(this)
    }

    fun toImmutable(): Matrix3d {
        return Matrix3d(this)
    }

    companion object {
        private val DBL_EPSILON = Double.fromBits(0x3cb0000000000000L)

        private val temporaryMatrix: MutableMatrix3d by threadLocal { MutableMatrix3d() }
    }
}