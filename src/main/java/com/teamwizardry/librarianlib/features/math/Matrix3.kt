package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.helpers.vec
import javax.vecmath.Matrix3d
import javax.vecmath.Point3d
import kotlin.math.floor
import kotlin.math.max

class Matrix3 private constructor(private val matrix: Matrix3d, val frozen: Boolean) {
    constructor(): this(Matrix3d().apply { setIdentity() }, false)
    private val transform = Matrix3d()
    private val point = Point3d()

    fun invert(): Matrix3 {
        val inverseMatrix = this.copy()
        inverseMatrix.matrix.invert()
        return inverseMatrix
    }

    /**
     * Translates this matrix by the passed amount
     * @param x the amount to translate in the x axis
     * @param y the amount to translate in the y axis
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun translate(x: Double, y: Double) {
        transform.setIdentity()
        transform.m02 = x
        transform.m12 = y
        matrix.mul(transform)
    }

    /**
     * Scales this matrix about the origin
     * @param x the x scale factor
     * @param y the y scale factor
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun scale(x: Double, y: Double) {
        transform.setIdentity()
        transform.m00 = x
        transform.m11 = y
        matrix.mul(transform)
    }

    /**
     * Rotates this matrix about the origin by [angle] radians clockwise
     * @param angle the angle to rotate by in radians
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun rotate(angle: Double) {
        transform.setIdentity()
        val sin = Math.sin(-angle)
        val cos = Math.cos(-angle)
        transform.m00 = cos
        transform.m01 = sin
        transform.m10 = -sin
        transform.m11 = cos

        matrix.mul(transform)
    }

    /**
     * Translates this matrix by the passed amount
     * @param vec the amount to translate
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun translate(vec: Vec2d) = translate(vec.x, vec.y)

    /**
     * Scales this matrix about the origin
     * @param vec the scale factor
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun scale(vec: Vec2d) = scale(vec.x, vec.y)

    /**
     * Applies this matrix to the given point and returns the resulting point
     * @param point the point to transform
     * @return the transformed point
     */
    fun apply(point: Vec2d): Vec2d {
        this.point.x = point.x
        this.point.y = point.y
        this.point.z = 1.0
        matrix.transform(this.point)
        return vec(this.point.x, this.point.y)
    }

    /**
     * Applies the passed matrix to this one and stores the result in this matrix.
     * @param matrix the matrix to apply
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun mul(matrix: Matrix3) {
        this.matrix.mul(matrix.matrix)
    }

    /**
     * Creates a copy of this matrix. The returned matrix will not be [frozen], even if this matrix is.
     * @return an unfrozen copy of this matrix
     */
    fun copy(): Matrix3 {
        return Matrix3(matrix.clone() as Matrix3d, false)
    }

    /**
     * Creates a frozen copy of this matrix. Any attempt to modify that instance will result in an
     * [IllegalStateException].
     * @return a frozen copy of this matrix
     */
    fun frozen(): Matrix3 {
        return Matrix3(matrix.clone() as Matrix3d, true)
    }

    /**
     * Applies this matrix to the given point and returns the resulting point
     * @param point the point to transform
     * @return the transformed point
     */
    operator fun times(point: Vec2d) = apply(point)

    /**
     * Applies the passed matrix to this one and stores the result in this matrix.
     * @param matrix the matrix to apply
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    operator fun timesAssign(matrix: Matrix3) = mul(matrix)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix3) return false

        if (matrix != other.matrix) return false

        return true
    }

    override fun hashCode(): Int {
        return matrix.hashCode()
    }

    override fun toString(): String {
        val w0 = floor(max(matrix.m00, max(matrix.m10, matrix.m20))).toString().length + 1
        val w1 = floor(max(matrix.m01, max(matrix.m11, matrix.m21))).toString().length + 1
        val w2 = floor(max(matrix.m02, max(matrix.m12, matrix.m22))).toString().length + 1

        return """
            ⎡%$w0.2f %$w1.2f %$w2.2f⎤
            ⎢%$w0.2f %$w1.2f %$w2.2f⎥
            ⎣%$w0.2f %$w1.2f %$w2.2f⎦
        """.trimIndent().format(
            matrix.m00, matrix.m01, matrix.m02,
            matrix.m10, matrix.m11, matrix.m12,
            matrix.m20, matrix.m21, matrix.m22
        )
    }

    companion object {
        /**
         * A frozen identity matrix
         */
        @JvmField
        val identity = Matrix3().frozen()
    }


}
