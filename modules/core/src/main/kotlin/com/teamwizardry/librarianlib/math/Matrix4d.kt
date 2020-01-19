package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.util.math.Vec3d
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.math.roundToLong
import kotlin.math.tan

// adapted from flow/math: https://github.com/flow/math
open class Matrix4d: Cloneable {
    internal var m00: Double
    internal var m01: Double
    internal var m02: Double
    internal var m03: Double
    internal var m10: Double
    internal var m11: Double
    internal var m12: Double
    internal var m13: Double
    internal var m20: Double
    internal var m21: Double
    internal var m22: Double
    internal var m23: Double
    internal var m30: Double
    internal var m31: Double
    internal var m32: Double
    internal var m33: Double
    @Volatile
    @Transient
    private var hashCode = 0

    constructor(m: Matrix4d): this(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33)

    constructor(): this(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    constructor(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
        m30: Float, m31: Float, m32: Float, m33: Float): this(
        m00.toDouble(), m01.toDouble(), m02.toDouble(), m03.toDouble(),
        m10.toDouble(), m11.toDouble(), m12.toDouble(), m13.toDouble(),
        m20.toDouble(), m21.toDouble(), m22.toDouble(), m23.toDouble(),
        m30.toDouble(), m31.toDouble(), m32.toDouble(), m33.toDouble())

    constructor(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double) {
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
    }

    operator fun get(row: Int, col: Int): Double {
        when (row) {
            0 -> {
                when (col) {
                    0 -> return m00
                    1 -> return m01
                    2 -> return m02
                    3 -> return m03
                }
                when (col) {
                    0 -> return m10
                    1 -> return m11
                    2 -> return m12
                    3 -> return m13
                }
                when (col) {
                    0 -> return m20
                    1 -> return m21
                    2 -> return m22
                    3 -> return m23
                }
                when (col) {
                    0 -> return m30
                    1 -> return m31
                    2 -> return m32
                    3 -> return m33
                }
            }
            1 -> {
                when (col) {
                    0 -> return m10
                    1 -> return m11
                    2 -> return m12
                    3 -> return m13
                }
                when (col) {
                    0 -> return m20
                    1 -> return m21
                    2 -> return m22
                    3 -> return m23
                }
                when (col) {
                    0 -> return m30
                    1 -> return m31
                    2 -> return m32
                    3 -> return m33
                }
            }
            2 -> {
                when (col) {
                    0 -> return m20
                    1 -> return m21
                    2 -> return m22
                    3 -> return m23
                }
                when (col) {
                    0 -> return m30
                    1 -> return m31
                    2 -> return m32
                    3 -> return m33
                }
            }
            3 -> when (col) {
                0 -> return m30
                1 -> return m31
                2 -> return m32
                3 -> return m33
            }
        }
        throw IllegalArgumentException(
            (if (row < 0 || row > 2) "row must be greater than zero and smaller than 3. " else "") + if (col < 0 || col > 2) "col must be greater than zero and smaller than 3." else "")
    }

    open fun add(m: Matrix4d): Matrix4d {
        return Matrix4d(
            m00 + m.m00, m01 + m.m01, m02 + m.m02, m03 + m.m03,
            m10 + m.m10, m11 + m.m11, m12 + m.m12, m13 + m.m13,
            m20 + m.m20, m21 + m.m21, m22 + m.m22, m23 + m.m23,
            m30 + m.m30, m31 + m.m31, m32 + m.m32, m33 + m.m33)
    }

    /** Operator function for Kotlin  */
    open operator fun plus(m: Matrix4d): Matrix4d {
        return add(m)
    }

    open fun sub(m: Matrix4d): Matrix4d {
        return Matrix4d(
            m00 - m.m00, m01 - m.m01, m02 - m.m02, m03 - m.m03,
            m10 - m.m10, m11 - m.m11, m12 - m.m12, m13 - m.m13,
            m20 - m.m20, m21 - m.m21, m22 - m.m22, m23 - m.m23,
            m30 - m.m30, m31 - m.m31, m32 - m.m32, m33 - m.m33)
    }

    /** Operator function for Kotlin  */
    open operator fun minus(m: Matrix4d): Matrix4d {
        return sub(m)
    }

    open fun mul(a: Float): Matrix4d {
        return mul(a.toDouble())
    }

    /** Operator function for Kotlin  */
    open operator fun times(a: Float): Matrix4d {
        return mul(a)
    }

    open fun mul(a: Double): Matrix4d {
        return Matrix4d(
            m00 * a, m01 * a, m02 * a, m03 * a,
            m10 * a, m11 * a, m12 * a, m13 * a,
            m20 * a, m21 * a, m22 * a, m23 * a,
            m30 * a, m31 * a, m32 * a, m33 * a)
    }

    /** Operator function for Kotlin  */
    open operator fun times(a: Double): Matrix4d {
        return mul(a)
    }

    open fun mul(m: Matrix4d): Matrix4d {
        return Matrix4d(
            m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30,
            m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31,
            m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32,
            m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33,
            m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30,
            m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31,
            m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32,
            m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33,
            m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30,
            m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31,
            m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32,
            m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33,
            m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30,
            m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31,
            m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32,
            m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33)
    }

    /** Operator function for Kotlin  */
    open operator fun times(m: Matrix4d): Matrix4d {
        return mul(m)
    }

    open operator fun div(a: Float): Matrix4d {
        return div(a.toDouble())
    }

    open operator fun div(a: Double): Matrix4d {
        return Matrix4d(
            m00 / a, m01 / a, m02 / a, m03 / a,
            m10 / a, m11 / a, m12 / a, m13 / a,
            m20 / a, m21 / a, m22 / a, m23 / a,
            m30 / a, m31 / a, m32 / a, m33 / a)
    }

    open operator fun div(m: Matrix4d): Matrix4d {
        return mul(m.invert())
    }

    open fun pow(pow: Float): Matrix4d {
        return pow(pow.toDouble())
    }

    open fun pow(pow: Double): Matrix4d {
        return Matrix4d(
            m00.pow(pow), m01.pow(pow), m02.pow(pow), m03.pow(pow),
            m10.pow(pow), m11.pow(pow), m12.pow(pow), m13.pow(pow),
            m20.pow(pow), m21.pow(pow), m22.pow(pow), m23.pow(pow),
            m30.pow(pow), m31.pow(pow), m32.pow(pow), m33.pow(pow))
    }

    open fun translate(v: Vec3d): Matrix4d {
        return translate(v.x, v.y, v.z)
    }

    open fun translate(x: Float, y: Float, z: Float): Matrix4d {
        return translate(x.toDouble(), y.toDouble(), z.toDouble())
    }

    open fun translate(x: Double, y: Double, z: Double): Matrix4d {
        return createTranslation(x, y, z).mul(this).toImmutable()
    }

    open fun scale(scale: Float): Matrix4d {
        return scale(scale.toDouble())
    }

    open fun scale(scale: Double): Matrix4d {
        return scale(scale, scale, scale, scale)
    }

    open fun scale(x: Float, y: Float, z: Float, w: Float): Matrix4d {
        return scale(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
    }

    open fun scale(x: Double, y: Double, z: Double, w: Double): Matrix4d {
        return createScaling(x, y, z, w).mul(this).toImmutable()
    }

    open fun rotate(rot: Quaternion): Matrix4d {
        return createRotation(rot).mul(this).toImmutable()
    }

    open fun floor(): Matrix4d {
        return Matrix4d(
            floor(m00), floor(m01), floor(m02), floor(m03),
            floor(m10), floor(m11), floor(m12), floor(m13),
            floor(m20), floor(m21), floor(m22), floor(m23),
            floor(m30), floor(m31), floor(m32), floor(m33))
    }

    open fun ceil(): Matrix4d {
        return Matrix4d(
            ceil(m00), ceil(m01), ceil(m02), ceil(m03),
            ceil(m10), ceil(m11), ceil(m12), ceil(m13),
            ceil(m20), ceil(m21), ceil(m22), ceil(m23),
            ceil(m30), ceil(m31), ceil(m32), ceil(m33))
    }

    open fun round(): Matrix4d {
        return Matrix4d(
            m00.roundToLong().toFloat(), m01.roundToLong().toFloat(), m02.roundToLong().toFloat(), m03.roundToLong().toFloat(),
            m10.roundToLong().toFloat(), m11.roundToLong().toFloat(), m12.roundToLong().toFloat(), m13.roundToLong().toFloat(),
            m20.roundToLong().toFloat(), m21.roundToLong().toFloat(), m22.roundToLong().toFloat(), m23.roundToLong().toFloat(),
            m30.roundToLong().toFloat(), m31.roundToLong().toFloat(), m32.roundToLong().toFloat(), m33.roundToLong().toFloat())
    }

    open fun abs(): Matrix4d {
        return Matrix4d(
            abs(m00), abs(m01), abs(m02), abs(m03),
            abs(m10), abs(m11), abs(m12), abs(m13),
            abs(m20), abs(m21), abs(m22), abs(m23),
            abs(m30), abs(m31), abs(m32), abs(m33))
    }

    open fun negate(): Matrix4d {
        return Matrix4d(
            -m00, -m01, -m02, -m03,
            -m10, -m11, -m12, -m13,
            -m20, -m21, -m22, -m23,
            -m30, -m31, -m32, -m33)
    }

    /** Operator function for Kotlin  */
    open operator fun unaryMinus(): Matrix4d {
        return negate()
    }

    open fun transpose(): Matrix4d {
        return Matrix4d(
            m00, m10, m20, m30,
            m01, m11, m21, m31,
            m02, m12, m22, m32,
            m03, m13, m23, m33)
    }

    fun trace(): Double {
        return m00 + m11 + m22 + m33
    }

    fun determinant(): Double {
        return m00 * (m11 * m22 * m33 + m21 * m32 * m13 + m31 * m12 * m23 - m31 * m22 * m13 - m11 * m32 * m23 - m21 * m12 * m33) - m10 * (m01 * m22 * m33 + m21 * m32 * m03 + m31 * m02 * m23 - m31 * m22 * m03 - m01 * m32 * m23 - m21 * m02 * m33) + m20 * (m01 * m12 * m33 + m11 * m32 * m03 + m31 * m02 * m13 - m31 * m12 * m03 - m01 * m32 * m13 - m11 * m02 * m33) - m30 * (m01 * m12 * m23 + m11 * m22 * m03 + m21 * m02 * m13 - m21 * m12 * m03 - m01 * m22 * m13 - m11 * m02 * m23)
    }

    open fun invert(): Matrix4d {
        val det = determinant()
        if (abs(det) < DBL_EPSILON) {
            throw ArithmeticException("Cannot inverse a matrix with a zero determinant")
        }
        return Matrix4d(
            det3(m11, m21, m31, m12, m22, m32, m13, m23, m33) / det, -det3(m01, m21, m31, m02, m22, m32, m03, m23, m33) / det,
            det3(m01, m11, m31, m02, m12, m32, m03, m13, m33) / det, -det3(m01, m11, m21, m02, m12, m22, m03, m13, m23) / det,
            -det3(m10, m20, m30, m12, m22, m32, m13, m23, m33) / det, det3(m00, m20, m30, m02, m22, m32, m03, m23, m33) / det,
            -det3(m00, m10, m30, m02, m12, m32, m03, m13, m33) / det, det3(m00, m10, m20, m02, m12, m22, m03, m13, m23) / det,
            det3(m10, m20, m30, m11, m21, m31, m13, m23, m33) / det, -det3(m00, m20, m30, m01, m21, m31, m03, m23, m33) / det,
            det3(m00, m10, m30, m01, m11, m31, m03, m13, m33) / det, -det3(m00, m10, m20, m01, m11, m21, m03, m13, m23) / det,
            -det3(m10, m20, m30, m11, m21, m31, m12, m22, m32) / det, det3(m00, m20, m30, m01, m21, m31, m02, m22, m32) / det,
            -det3(m00, m10, m30, m01, m11, m31, m02, m12, m32) / det, det3(m00, m10, m20, m01, m11, m21, m02, m12, m22) / det)
    }

    fun toArray(): DoubleArray {
        return toArray(false)
    }

    fun toArray(columnMajor: Boolean): DoubleArray {
        return if (columnMajor) {
            doubleArrayOf(m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33)
        } else {
            doubleArrayOf(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)
        }
    }

    override fun toString(): String {
        return (m00.toString() + " " + m01 + " " + m02 + " " + m03 + "\n"
            + m10 + " " + m11 + " " + m12 + " " + m13 + "\n"
            + m20 + " " + m21 + " " + m22 + " " + m23 + "\n"
            + m30 + " " + m31 + " " + m32 + " " + m33 + "\n")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        other as? Matrix4d ?: return false
        return this.m00 == other.m00 && this.m01 == other.m01 && this.m02 == other.m02 && this.m03 == other.m03 &&
            this.m10 == other.m10 && this.m11 == other.m11 && this.m12 == other.m12 && this.m13 == other.m13 &&
            this.m20 == other.m20 && this.m21 == other.m21 && this.m22 == other.m22 && this.m23 == other.m23 &&
            this.m30 == other.m30 && this.m31 == other.m31 && this.m32 == other.m32 && this.m33 == other.m33
    }

    override fun hashCode(): Int {
        if (hashCode == 0) {
            var result = if (m00 != +0.0) m00.hashCode() else 0
            result = 31 * result + if (m01 != +0.0) m01.hashCode() else 0
            result = 31 * result + if (m02 != +0.0) m02.hashCode() else 0
            result = 31 * result + if (m03 != +0.0) m03.hashCode() else 0
            result = 31 * result + if (m10 != +0.0) m10.hashCode() else 0
            result = 31 * result + if (m11 != +0.0) m11.hashCode() else 0
            result = 31 * result + if (m12 != +0.0) m12.hashCode() else 0
            result = 31 * result + if (m13 != +0.0) m13.hashCode() else 0
            result = 31 * result + if (m20 != +0.0) m20.hashCode() else 0
            result = 31 * result + if (m21 != +0.0) m21.hashCode() else 0
            result = 31 * result + if (m22 != +0.0) m22.hashCode() else 0
            result = 31 * result + if (m23 != +0.0) m23.hashCode() else 0
            result = 31 * result + if (m30 != +0.0) m30.hashCode() else 0
            result = 31 * result + if (m31 != +0.0) m31.hashCode() else 0
            result = 31 * result + if (m32 != +0.0) m32.hashCode() else 0
            result = 31 * result + if (m33 != +0.0) m33.hashCode() else 0
            hashCode = if(result == 0) 1 else result
        }
        return hashCode
    }

    override fun clone(): Matrix4d {
        return Matrix4d(this)
    }

    fun toMutable(): MutableMatrix4d = MutableMatrix4d(this)

    companion object {
        private val DBL_EPSILON = Double.fromBits(0x3cb0000000000000L)
        val ZERO = Matrix4d(
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f)
        val IDENTITY = Matrix4d()

        /**
         * A thread local mutable matrix, useful for an no allocation intermediate for various operations
         */
        val temporaryMatrix: MutableMatrix4d by threadLocal { MutableMatrix4d() }

        internal fun createScaling(x: Double, y: Double, z: Double, w: Double): MutableMatrix4d {
            return temporaryMatrix.set(
                x, 0.0, 0.0, 0.0,
                0.0, y, 0.0, 0.0,
                0.0, 0.0, z, 0.0,
                0.0, 0.0, 0.0, w)
        }

        internal fun createTranslation(x: Double, y: Double, z: Double): MutableMatrix4d {
            return temporaryMatrix.set(
                1.0, 0.0, 0.0, x,
                0.0, 1.0, 0.0, y,
                0.0, 0.0, 1.0, z,
                0.0, 0.0, 0.0, 1.0)
        }

        internal fun createRotation(rot: Quaternion): MutableMatrix4d {
            var rot = rot
            rot = rot.normalize()
            return temporaryMatrix.set(
                1 - 2 * rot.y * rot.y - 2 * rot.z * rot.z,
                2 * rot.x * rot.y - 2 * rot.w * rot.z,
                2 * rot.x * rot.z + 2 * rot.w * rot.y, 0.0,
                2 * rot.x * rot.y + 2 * rot.w * rot.z,
                1 - 2 * rot.x * rot.x - 2 * rot.z * rot.z,
                2 * rot.y * rot.z - 2 * rot.w * rot.x, 0.0,
                2 * rot.x * rot.z - 2 * rot.w * rot.y,
                2 * rot.y * rot.z + 2 * rot.x * rot.w,
                1 - 2 * rot.x * rot.x - 2 * rot.y * rot.y, 0.0,
                0.0, 0.0, 0.0, 1.0)
        }

        /**
         * Creates a "look at" matrix for the given eye point.
         *
         * @param eye The position of the camera
         * @param at The point that the camera is looking at
         * @param up The "up" vector
         * @return A rotational transform that corresponds to a camera looking at the given point
         */
        fun createLookAt(eye: Vec3d, at: Vec3d, up: Vec3d): Matrix4d {
            val f = (at - eye).normalize()
            val s = (f cross up).normalize()
            val u = s cross f
            val mat = Matrix4d(
                s.x, s.y, s.z, 0.0,
                u.x, u.y, u.z, 0.0,
                -f.x, -f.y, -f.z, 0.0,
                0.0, 0.0, 0.0, 1.0)
            return mat.translate(-eye)
        }

        /**
         * Creates a perspective projection matrix with the given (x) FOV, aspect, near and far planes
         *
         * @param fov The field of view in the x direction
         * @param aspect The aspect ratio, usually width/height
         * @param near The near plane, cannot be 0
         * @param far the far plane, far cannot equal near
         * @return A perspective projection matrix built from the given values
         */
        fun createPerspective(fov: Float, aspect: Float, near: Float, far: Float): Matrix4d {
            return createPerspective(fov.toDouble(), aspect.toDouble(), near.toDouble(), far.toDouble())
        }

        /**
         * Creates a perspective projection matrix with the given (x) FOV, aspect, near and far planes
         *
         * @param fov The field of view in the x direction
         * @param aspect The aspect ratio, usually width/height
         * @param near The near plane, cannot be 0
         * @param far the far plane, far cannot equal near
         * @return A perspective projection matrix built from the given values
         */
        fun createPerspective(fov: Double, aspect: Double, near: Double, far: Double): Matrix4d {
            val scale = 1 / tan(fov * (PI / 360))
            return Matrix4d(
                scale / aspect, 0.0, 0.0, 0.0,
                0.0, scale, 0.0, 0.0,
                0.0, 0.0, (far + near) / (near - far), 2.0 * far * near / (near - far),
                0.0, 0.0, -1.0, 0.0)
        }

        /**
         * Creates an orthographic viewing frustum built from the provided values
         *
         * @param right the right most plane of the viewing frustum
         * @param left the left most plane of the viewing frustum
         * @param top the top plane of the viewing frustum
         * @param bottom the bottom plane of the viewing frustum
         * @param near the near plane of the viewing frustum
         * @param far the far plane of the viewing frustum
         * @return A viewing frustum built from the provided values
         */
        fun createOrthographic(right: Float, left: Float, top: Float, bottom: Float,
            near: Float, far: Float): Matrix4d {
            return createOrthographic(right.toDouble(), left.toDouble(), top.toDouble(), bottom.toDouble(), near.toDouble(), far.toDouble())
        }

        /**
         * Creates an orthographic viewing frustum built from the provided values
         *
         * @param right the right most plane of the viewing frustum
         * @param left the left most plane of the viewing frustum
         * @param top the top plane of the viewing frustum
         * @param bottom the bottom plane of the viewing frustum
         * @param near the near plane of the viewing frustum
         * @param far the far plane of the viewing frustum
         * @return A viewing frustum built from the provided values
         */
        fun createOrthographic(right: Double, left: Double, top: Double, bottom: Double,
            near: Double, far: Double): Matrix4d {
            return Matrix4d(
                2 / (right - left), 0.0, 0.0, -(right + left) / (right - left),
                0.0, 2 / (top - bottom), 0.0, -(top + bottom) / (top - bottom),
                0.0, 0.0, -2 / (far - near), -(far + near) / (far - near),
                0.0, 0.0, 0.0, 1.0)
        }

        internal fun det3(m00: Double, m01: Double, m02: Double,
            m10: Double, m11: Double, m12: Double,
            m20: Double, m21: Double, m22: Double): Double {
            return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m12 * m20) + m02 * (m10 * m21 - m11 * m20)
        }
    }
}
