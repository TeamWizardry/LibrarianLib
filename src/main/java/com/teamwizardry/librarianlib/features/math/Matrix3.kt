package com.teamwizardry.librarianlib.features.math



/**
 * Represents a 2d affine transform
 */
class Matrix3( //m<row><column>
        val m00: Double, val m01: Double, val m02: Double,
        val m10: Double, val m11: Double, val m12: Double,
        val m20: Double, val m21: Double, val m22: Double) {

    constructor() : this(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0
    )

    fun copy(): Matrix3 {
        return Matrix3(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        )
    }

    /**
     * Rotate this matrix
     *
     * @return A rotated matrix
     */
    fun rotate(amount: Double): Matrix3 {
        val cos = Math.cos(amount)
        val sin = Math.sin(amount)
        return Matrix3(
                m00 * cos + m01 * -sin, m00 * sin + m01 * cos, m02,
                m10 * cos + m11 * -sin, m10 * sin + m11 * cos, m12,
                m20 * cos + m21 * -sin, m20 * sin + m21 * cos, m22
        )
    }

    /**
     * Translate this matrix
     *
     * @return A translated matrix
     */
    fun translate(vec: Vec2d): Matrix3 {
        return Matrix3(
                m00, m01, m00 * vec.x + m01 * vec.y + m02,
                m10, m11, m10 * vec.x + m11 * vec.y + m12,
                m20, m21, m20 * vec.x + m21 * vec.y + m22
        )
    }

    /**
     * Scale this matrix
     *
     * @return A scaled matrix
     */
    fun scale(vec: Vec2d): Matrix3 {
        return Matrix3(
                m00 * vec.x, m01 * vec.y, m02,
                m10 * vec.x, m11 * vec.y, m12,
                m20 * vec.x, m21 * vec.y, m22
        )
    }

    /**
     * Multiplies this matrix by [mat].
     */
    fun multiply(mat: Matrix3): Matrix3  {
        return Matrix3(
                m00 * mat.m00 + m01 * mat.m10 + m02 * mat.m20,
                m00 * mat.m01 + m01 * mat.m11 + m02 * mat.m21,
                m00 * mat.m02 + m01 * mat.m12 + m02 * mat.m22,

                m10 * mat.m00 + m11 * mat.m10 + m12 * mat.m20,
                m10 * mat.m01 + m11 * mat.m11 + m12 * mat.m21,
                m10 * mat.m02 + m11 * mat.m12 + m12 * mat.m22,

                m20 * mat.m00 + m21 * mat.m10 + m22 * mat.m20,
                m20 * mat.m01 + m21 * mat.m11 + m22 * mat.m21,
                m20 * mat.m02 + m21 * mat.m12 + m22 * mat.m22
        )
    }

    /**
     * Multiplies the passed vector by this matrix
     */
    fun multiply(vec: Vec2d): Vec2d {
        return Vec2d(
                m00 * vec.x + m01 * vec.y + m02 * 1,
                m10 * vec.x + m11 * vec.y + m12 * 1
                //m20 * vec.x + m21 * vec.y + m22 * 1 // theoretical third component equal to 1
        )
    }

    operator fun times(mat: Matrix3) = this.multiply(mat)
    operator fun times(vec: Vec2d) = this.multiply(vec)

    /**
     * Get the determinant of this matrix
     */
    fun det(): Double {
        return (0
                + m00 * (m11 * m22 - m21 * m12)
                - m01 * (m10 * m22 - m12 * m20)
                + m02 * (m10 * m21 - m11 * m20)
                )
    }


    fun inverse(): Matrix3 {
        val invdet = 1/det()
        return Matrix3(
                (m11 * m22 - m21 * m12) * invdet,
                (m02 * m21 - m01 * m22) * invdet,
                (m01 * m12 - m02 * m11) * invdet,
                (m12 * m20 - m10 * m22) * invdet,
                (m00 * m22 - m02 * m20) * invdet,
                (m10 * m02 - m00 * m12) * invdet,
                (m10 * m21 - m20 * m11) * invdet,
                (m20 * m01 - m00 * m21) * invdet,
                (m00 * m11 - m10 * m01) * invdet
        )
    }

    fun ascii(): String {
        var s00 = m00.toString()
        var s10 = m10.toString()
        var s20 = m20.toString()
        var s01 = m01.toString()
        var s11 = m11.toString()
        var s21 = m21.toString()
        var s02 = m02.toString()
        var s12 = m12.toString()
        var s22 = m22.toString()
        val maxLen = arrayOf(s00, s10, s11, s01, s11, s21, s02, s12, s22).map { it.length }.max() ?: 0
        val p = '0'
        s00 = s00.padEnd(maxLen, p)
        s10 = s10.padEnd(maxLen, p)
        s20 = s20.padEnd(maxLen, p)
        s01 = s01.padEnd(maxLen, p)
        s11 = s11.padEnd(maxLen, p)
        s21 = s21.padEnd(maxLen, p)
        s02 = s02.padEnd(maxLen, p)
        s12 = s12.padEnd(maxLen, p)
        s22 = s22.padEnd(maxLen, p)

        return "⎡$s00 $s10 $s20⎤\n" +
                "⎢$s01 $s11 $s21⎥\n" +
                "⎣$s02 $s12 $s22⎦"
    }

}
