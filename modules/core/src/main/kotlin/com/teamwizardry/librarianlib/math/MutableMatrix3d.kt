package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.bridge.IMatrix3f
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

// adapted from flow/math: https://github.com/flow/math
public open class MutableMatrix3d(
    m00: Double,
    m01: Double,
    m02: Double,
    m10: Double,
    m11: Double,
    m12: Double,
    m20: Double,
    m21: Double,
    m22: Double
): Matrix3d() {

    override var m00: Double = m00
        set(value) = recordMutation { field = value }
    override var m01: Double = m01
        set(value) = recordMutation { field = value }
    override var m02: Double = m02
        set(value) = recordMutation { field = value }
    override var m10: Double = m10
        set(value) = recordMutation { field = value }
    override var m11: Double = m11
        set(value) = recordMutation { field = value }
    override var m12: Double = m12
        set(value) = recordMutation { field = value }
    override var m20: Double = m20
        set(value) = recordMutation { field = value }
    override var m21: Double = m21
        set(value) = recordMutation { field = value }
    override var m22: Double = m22
        set(value) = recordMutation { field = value }

    /**
     * This value is incremented every time the matrix is mutated, making it easy to detect changes for the purpose of
     * caching.
     */
    public var mutationCount: Int = 0
        private set

    public constructor(m: Matrix3d): this(
        m.m00, m.m01, m.m02,
        m.m10, m.m11, m.m12,
        m.m20, m.m21, m.m22
    )

    public constructor(): this(
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0,
        0.0, 0.0, 1.0
    )

    public constructor(
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
        m20: Float, m21: Float, m22: Float
    ): this(
        m00.toDouble(), m01.toDouble(), m02.toDouble(),
        m10.toDouble(), m11.toDouble(), m12.toDouble(),
        m20.toDouble(), m21.toDouble(), m22.toDouble()
    )

    @Suppress("CAST_NEVER_SUCCEEDS")
    public constructor(m: Matrix3f): this(
        (m as IMatrix3f).m00,
        (m as IMatrix3f).m01,
        (m as IMatrix3f).m02,
        (m as IMatrix3f).m10,
        (m as IMatrix3f).m11,
        (m as IMatrix3f).m12,
        (m as IMatrix3f).m20,
        (m as IMatrix3f).m21,
        (m as IMatrix3f).m22
    )

    public operator fun set(row: Int, col: Int, value: Double) {
        if (row !in 0..2 || col !in 0..2) {
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

    public fun set(
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

    public fun set(m: Matrix3d): MutableMatrix3d {
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

    public fun set(m: Matrix3f): MutableMatrix3d {
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
        val _m00 = m00;
        val _m01 = m01;
        val _m02 = m02
        val _m10 = m10;
        val _m11 = m11;
        val _m12 = m12
        val _m20 = m20;
        val _m21 = m21;
        val _m22 = m22
        m00 = _m00 * m.m00 + _m01 * m.m10 + _m02 * m.m20
        m01 = _m00 * m.m01 + _m01 * m.m11 + _m02 * m.m21
        m02 = _m00 * m.m02 + _m01 * m.m12 + _m02 * m.m22
        m10 = _m10 * m.m00 + _m11 * m.m10 + _m12 * m.m20
        m11 = _m10 * m.m01 + _m11 * m.m11 + _m12 * m.m21
        m12 = _m10 * m.m02 + _m11 * m.m12 + _m12 * m.m22
        m20 = _m20 * m.m00 + _m21 * m.m10 + _m22 * m.m20
        m21 = _m20 * m.m01 + _m21 * m.m11 + _m22 * m.m21
        m22 = _m20 * m.m02 + _m21 * m.m12 + _m22 * m.m22
        return this
    }

    public fun reverseMul(m: Matrix3d): MutableMatrix3d {
        val _m00 = m00;
        val _m01 = m01;
        val _m02 = m02
        val _m10 = m10;
        val _m11 = m11;
        val _m12 = m12
        val _m20 = m20;
        val _m21 = m21;
        val _m22 = m22
        m00 = m.m00 * _m00 + m.m01 * _m10 + m.m02 * _m20
        m01 = m.m00 * _m01 + m.m01 * _m11 + m.m02 * _m21
        m02 = m.m00 * _m02 + m.m01 * _m12 + m.m02 * _m22
        m10 = m.m10 * _m00 + m.m11 * _m10 + m.m12 * _m20
        m11 = m.m10 * _m01 + m.m11 * _m11 + m.m12 * _m21
        m12 = m.m10 * _m02 + m.m11 * _m12 + m.m12 * _m22
        m20 = m.m20 * _m00 + m.m21 * _m10 + m.m22 * _m20
        m21 = m.m20 * _m01 + m.m21 * _m11 + m.m22 * _m21
        m22 = m.m20 * _m02 + m.m21 * _m12 + m.m22 * _m22
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
        val _m00 = m00;
        val _m01 = m01;
        val _m02 = m02
        val _m10 = m10;
        val _m11 = m11;
        val _m12 = m12
        val _m20 = m20;
        val _m21 = m21;
        val _m22 = m22
        m00 = _m00
        m01 = _m10
        m02 = _m20
        m10 = _m01
        m11 = _m11
        m12 = _m21
        m20 = _m02
        m21 = _m12
        m22 = _m22
        return this
    }

    override fun invert(): MutableMatrix3d {
        val det = determinant()
        if (abs(det) < DBL_EPSILON) {
            throw ArithmeticException("Cannot inverse a matrix with a zero determinant")
        }
        val _m00 = m00;
        val _m01 = m01;
        val _m02 = m02
        val _m10 = m10;
        val _m11 = m11;
        val _m12 = m12
        val _m20 = m20;
        val _m21 = m21;
        val _m22 = m22

        m00 = (_m11 * _m22 - _m21 * _m12) / det
        m01 = -(_m01 * _m22 - _m21 * _m02) / det
        m02 = (_m01 * _m12 - _m02 * _m11) / det

        m10 = -(_m10 * _m22 - _m20 * _m12) / det
        m11 = (_m00 * _m22 - _m20 * _m02) / det
        m12 = -(_m00 * _m12 - _m10 * _m02) / det

        m20 = (_m10 * _m21 - _m20 * _m11) / det
        m21 = -(_m00 * _m21 - _m20 * _m01) / det
        m22 = (_m00 * _m11 - _m01 * _m10) / det
        return this
    }

    override fun plus(m: Matrix3d): MutableMatrix3d {
        return add(m)
    }

    @JvmSynthetic
    public operator fun plusAssign(m: Matrix3d) {
        add(m)
    }

    override fun minus(m: Matrix3d): MutableMatrix3d {
        return sub(m)
    }

    @JvmSynthetic
    public operator fun minusAssign(m: Matrix3d) {
        sub(m)
    }

    override fun mul(a: Float): MutableMatrix3d {
        return mul(a.toDouble())
    }

    override fun times(a: Float): MutableMatrix3d {
        return mul(a)
    }

    @JvmSynthetic
    public operator fun timesAssign(a: Float) {
        mul(a)
    }

    override fun times(a: Double): MutableMatrix3d {
        return mul(a)
    }

    @JvmSynthetic
    public operator fun timesAssign(a: Double) {
        mul(a)
    }

    override fun times(m: Matrix3d): MutableMatrix3d {
        return mul(m)
    }

    @JvmSynthetic
    public operator fun timesAssign(m: Matrix3d) {
        mul(m)
    }

    @JvmSynthetic
    public operator fun divAssign(a: Double) {
        div(a)
    }

    override fun div(a: Float): MutableMatrix3d {
        return div(a.toDouble())
    }

    @JvmSynthetic
    public operator fun divAssign(a: Float) {
        div(a)
    }

    override fun div(m: Matrix3d): MutableMatrix3d {
        temporaryMatrix.set(m)
        return mul(temporaryMatrix.invert())
    }

    @JvmSynthetic
    public operator fun divAssign(m: Matrix3d) {
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
        return this.mul(createTranslation(x, y))
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
        return this.mul(createScaling(x, y, z))
    }

    override fun rotate(rot: Quaternion): MutableMatrix3d {
        return this.mul(createRotation(rot))
    }

    override fun rotate(axis: Vec3d, angle: Double): Matrix3d {
        return this.mul(createRotation(axis, angle))
    }

    override fun rotate2d(angle: Double): Matrix3d {
        return this.mul(createRotation2d(angle))
    }

    override fun unaryMinus(): MutableMatrix3d {
        return negate()
    }

    override fun clone(): MutableMatrix3d {
        return MutableMatrix3d(this)
    }

    override fun toImmutable(): Matrix3d {
        return Matrix3d(this)
    }

    protected fun recordMutation() {
        mutationCount++
    }

    protected inline fun recordMutation(block: () -> Unit) {
        block()
        recordMutation()
    }

    private companion object {
        private val DBL_EPSILON = Double.fromBits(0x3cb0000000000000L)

        private val temporaryMatrix: MutableMatrix3d by threadLocal { MutableMatrix3d() }
    }
}