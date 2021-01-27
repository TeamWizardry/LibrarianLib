package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.math.vector.Vector3d
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong

// adapted from flow/math: https://github.com/flow/math
public open class MutableMatrix4d(
    override var m00: Double,
    override var m01: Double,
    override var m02: Double,
    override var m03: Double,
    override var m10: Double,
    override var m11: Double,
    override var m12: Double,
    override var m13: Double,
    override var m20: Double,
    override var m21: Double,
    override var m22: Double,
    override var m23: Double,
    override var m30: Double,
    override var m31: Double,
    override var m32: Double,
    override var m33: Double
): Matrix4d() {

    public constructor(m: Matrix4d): this(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33)

    public constructor(): this(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    public constructor(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
        m30: Float, m31: Float, m32: Float, m33: Float): this(
        m00.toDouble(), m01.toDouble(), m02.toDouble(), m03.toDouble(),
        m10.toDouble(), m11.toDouble(), m12.toDouble(), m13.toDouble(),
        m20.toDouble(), m21.toDouble(), m22.toDouble(), m23.toDouble(),
        m30.toDouble(), m31.toDouble(), m32.toDouble(), m33.toDouble())

    @Suppress("CAST_NEVER_SUCCEEDS")
    public constructor(m: Matrix4f): this(
        (m as IMatrix4f).m00,
        (m as IMatrix4f).m01,
        (m as IMatrix4f).m02,
        (m as IMatrix4f).m03,
        (m as IMatrix4f).m10,
        (m as IMatrix4f).m11,
        (m as IMatrix4f).m12,
        (m as IMatrix4f).m13,
        (m as IMatrix4f).m20,
        (m as IMatrix4f).m21,
        (m as IMatrix4f).m22,
        (m as IMatrix4f).m23,
        (m as IMatrix4f).m30,
        (m as IMatrix4f).m31,
        (m as IMatrix4f).m32,
        (m as IMatrix4f).m33
    )

    public operator fun set(row: Int, col: Int, value: Double) {
        if (row !in 0..3 || col !in 0..3) {
            throw IllegalArgumentException(
                (if (row < 0 || row > 3) "row must be greater than zero and smaller than 3. " else "") + if (col < 0 || col > 3) "col must be greater than zero and smaller than 3." else "")
        }
        when (row) {
            0 -> when (col) {
                0 -> m00 = value
                1 -> m01 = value
                2 -> m02 = value
                3 -> m03 = value
            }
            1 -> when (col) {
                0 -> m10 = value
                1 -> m11 = value
                2 -> m12 = value
                3 -> m13 = value
            }
            2 -> when (col) {
                0 -> m20 = value
                1 -> m21 = value
                2 -> m22 = value
                3 -> m23 = value
            }
            3 -> when (col) {
                0 -> m30 = value
                1 -> m31 = value
                2 -> m32 = value
                3 -> m33 = value
            }
        }
    }

    public fun set(
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

    public fun set(
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

    /**
     * Set the contents of this matrix to the contents of the other matrix
     */
    public fun set(other: Matrix4d): MutableMatrix4d {
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

    /**
     * Set the contents of this matrix to the contents of the other matrix
     */
    public fun set(m: Matrix4f): MutableMatrix4d {
        @Suppress("CAST_NEVER_SUCCEEDS") val imatrix = m as IMatrix4f
        this.m00 = imatrix.m00.toDouble()
        this.m01 = imatrix.m01.toDouble()
        this.m02 = imatrix.m02.toDouble()
        this.m03 = imatrix.m03.toDouble()
        this.m10 = imatrix.m10.toDouble()
        this.m11 = imatrix.m11.toDouble()
        this.m12 = imatrix.m12.toDouble()
        this.m13 = imatrix.m13.toDouble()
        this.m20 = imatrix.m20.toDouble()
        this.m21 = imatrix.m21.toDouble()
        this.m22 = imatrix.m22.toDouble()
        this.m23 = imatrix.m23.toDouble()
        this.m30 = imatrix.m30.toDouble()
        this.m31 = imatrix.m31.toDouble()
        this.m32 = imatrix.m32.toDouble()
        this.m33 = imatrix.m33.toDouble()
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

    override fun mul(
        m: Matrix4d
    ): MutableMatrix4d {
        return mul(
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33
        )
    }

    public fun mul(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double
    ): MutableMatrix4d {
        val _m00 = this.m00
        val _m01 = this.m01
        val _m02 = this.m02
        val _m03 = this.m03
        val _m10 = this.m10
        val _m11 = this.m11
        val _m12 = this.m12
        val _m13 = this.m13
        val _m20 = this.m20
        val _m21 = this.m21
        val _m22 = this.m22
        val _m23 = this.m23
        val _m30 = this.m30
        val _m31 = this.m31
        val _m32 = this.m32
        val _m33 = this.m33

        this.m00 = _m00 * m00 + _m01 * m10 + _m02 * m20 + _m03 * m30
        this.m01 = _m00 * m01 + _m01 * m11 + _m02 * m21 + _m03 * m31
        this.m02 = _m00 * m02 + _m01 * m12 + _m02 * m22 + _m03 * m32
        this.m03 = _m00 * m03 + _m01 * m13 + _m02 * m23 + _m03 * m33
        this.m10 = _m10 * m00 + _m11 * m10 + _m12 * m20 + _m13 * m30
        this.m11 = _m10 * m01 + _m11 * m11 + _m12 * m21 + _m13 * m31
        this.m12 = _m10 * m02 + _m11 * m12 + _m12 * m22 + _m13 * m32
        this.m13 = _m10 * m03 + _m11 * m13 + _m12 * m23 + _m13 * m33
        this.m20 = _m20 * m00 + _m21 * m10 + _m22 * m20 + _m23 * m30
        this.m21 = _m20 * m01 + _m21 * m11 + _m22 * m21 + _m23 * m31
        this.m22 = _m20 * m02 + _m21 * m12 + _m22 * m22 + _m23 * m32
        this.m23 = _m20 * m03 + _m21 * m13 + _m22 * m23 + _m23 * m33
        this.m30 = _m30 * m00 + _m31 * m10 + _m32 * m20 + _m33 * m30
        this.m31 = _m30 * m01 + _m31 * m11 + _m32 * m21 + _m33 * m31
        this.m32 = _m30 * m02 + _m31 * m12 + _m32 * m22 + _m33 * m32
        this.m33 = _m30 * m03 + _m31 * m13 + _m32 * m23 + _m33 * m33
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

    override fun translate(v: Vector3d): MutableMatrix4d {
        return translate(v.x, v.y, v.z)
    }

    override fun translate(x: Float, y: Float, z: Float): MutableMatrix4d {
        return translate(x.toDouble(), y.toDouble(), z.toDouble())
    }

    override fun translate(x: Double, y: Double, z: Double): MutableMatrix4d {
        return this.mul(createTranslation(x, y, z))
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
        return this.mul(createScaling(x, y, z, w))
    }

    override fun rotate(rot: Quaternion): MutableMatrix4d {
        return this.mul(createRotation(rot))
    }

    override fun rotate(axis: Vector3d, angle: Double): Matrix4d {
        return this.mul(createRotation(axis, angle))
    }

    override fun floor(): MutableMatrix4d {
        m00 = floor(m00)
        m01 = floor(m01)
        m02 = floor(m02)
        m03 = floor(m03)
        m10 = floor(m10)
        m11 = floor(m11)
        m12 = floor(m12)
        m13 = floor(m13)
        m20 = floor(m20)
        m21 = floor(m21)
        m22 = floor(m22)
        m23 = floor(m23)
        m30 = floor(m30)
        m31 = floor(m31)
        m32 = floor(m32)
        m33 = floor(m33)
        return this
    }

    override fun ceil(): MutableMatrix4d {
        m00 = ceil(m00)
        m01 = ceil(m01)
        m02 = ceil(m02)
        m03 = ceil(m03)
        m10 = ceil(m10)
        m11 = ceil(m11)
        m12 = ceil(m12)
        m13 = ceil(m13)
        m20 = ceil(m20)
        m21 = ceil(m21)
        m22 = ceil(m22)
        m23 = ceil(m23)
        m30 = ceil(m30)
        m31 = ceil(m31)
        m32 = ceil(m32)
        m33 = ceil(m33)
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
        m00 = abs(m00)
        m01 = abs(m01)
        m02 = abs(m02)
        m03 = abs(m03)
        m10 = abs(m10)
        m11 = abs(m11)
        m12 = abs(m12)
        m13 = abs(m13)
        m20 = abs(m20)
        m21 = abs(m21)
        m22 = abs(m22)
        m23 = abs(m23)
        m30 = abs(m30)
        m31 = abs(m31)
        m32 = abs(m32)
        m33 = abs(m33)
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
        val _m00 = m00;
        val _m01 = m01;
        val _m02 = m02;
        val _m03 = m03
        val _m10 = m10;
        val _m11 = m11;
        val _m12 = m12;
        val _m13 = m13
        val _m20 = m20;
        val _m21 = m21;
        val _m22 = m22;
        val _m23 = m23
        val _m30 = m30;
        val _m31 = m31;
        val _m32 = m32;
        val _m33 = m33

        m00 = _m00
        m01 = _m10
        m02 = _m20
        m03 = _m30
        m10 = _m01
        m11 = _m11
        m12 = _m21
        m13 = _m31
        m20 = _m02
        m21 = _m12
        m22 = _m22
        m23 = _m32
        m30 = _m03
        m31 = _m13
        m32 = _m23
        m33 = _m33
        return this
    }

    override fun invert(): MutableMatrix4d {
        val det = determinant()
        if (abs(det) < DBL_EPSILON) {
            throw ArithmeticException("Cannot inverse a matrix with a zero determinant")
        }
        val _m00 = m00;
        val _m01 = m01;
        val _m02 = m02;
        val _m03 = m03
        val _m10 = m10;
        val _m11 = m11;
        val _m12 = m12;
        val _m13 = m13
        val _m20 = m20;
        val _m21 = m21;
        val _m22 = m22;
        val _m23 = m23
        val _m30 = m30;
        val _m31 = m31;
        val _m32 = m32;
        val _m33 = m33

        m00 = det3(_m11, _m21, _m31, _m12, _m22, _m32, _m13, _m23, _m33) / det
        m01 = -det3(_m01, _m21, _m31, _m02, _m22, _m32, _m03, _m23, _m33) / det
        m02 = det3(_m01, _m11, _m31, _m02, _m12, _m32, _m03, _m13, _m33) / det
        m03 = -det3(_m01, _m11, _m21, _m02, _m12, _m22, _m03, _m13, _m23) / det
        m10 = -det3(_m10, _m20, _m30, _m12, _m22, _m32, _m13, _m23, _m33) / det
        m11 = det3(_m00, _m20, _m30, _m02, _m22, _m32, _m03, _m23, _m33) / det
        m12 = -det3(_m00, _m10, _m30, _m02, _m12, _m32, _m03, _m13, _m33) / det
        m13 = det3(_m00, _m10, _m20, _m02, _m12, _m22, _m03, _m13, _m23) / det
        m20 = det3(_m10, _m20, _m30, _m11, _m21, _m31, _m13, _m23, _m33) / det
        m21 = -det3(_m00, _m20, _m30, _m01, _m21, _m31, _m03, _m23, _m33) / det
        m22 = det3(_m00, _m10, _m30, _m01, _m11, _m31, _m03, _m13, _m33) / det
        m23 = -det3(_m00, _m10, _m20, _m01, _m11, _m21, _m03, _m13, _m23) / det
        m30 = -det3(_m10, _m20, _m30, _m11, _m21, _m31, _m12, _m22, _m32) / det
        m31 = det3(_m00, _m20, _m30, _m01, _m21, _m31, _m02, _m22, _m32) / det
        m32 = -det3(_m00, _m10, _m30, _m01, _m11, _m31, _m02, _m12, _m32) / det
        m33 = det3(_m00, _m10, _m20, _m01, _m11, _m21, _m02, _m12, _m22) / det
        return this
    }

    override fun clone(): MutableMatrix4d {
        return MutableMatrix4d(this)
    }

    override fun toImmutable(): Matrix4d = Matrix4d(this)

    private companion object {
        private val DBL_EPSILON = Double.fromBits(0x3cb0000000000000L)
    }
}