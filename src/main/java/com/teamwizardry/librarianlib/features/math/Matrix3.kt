package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.helpers.vec
import javax.vecmath.Matrix3d
import javax.vecmath.Point3d

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
     * Returns an inverse of this matrix, avoiding issues with zero scales by collapsing positions onto a line
     */
    fun invertSafely(): Matrix3 {
        val inverseMatrix = this.copy()
        val xZero = inverseMatrix.matrix.m00 == 0.0 && inverseMatrix.matrix.m01 == 0.0
        val yZero = inverseMatrix.matrix.m10 == 0.0 && inverseMatrix.matrix.m11 == 0.0
        if(xZero) inverseMatrix.matrix.m00 = 1.0
        if(yZero) inverseMatrix.matrix.m11 = 1.0
        inverseMatrix.matrix.invert()
        if(xZero) {
            inverseMatrix.matrix.m00 = Double.POSITIVE_INFINITY
            inverseMatrix.matrix.m01 = 0.0
        }
        if(yZero) {
            inverseMatrix.matrix.m10 = 0.0
            inverseMatrix.matrix.m11 = Double.POSITIVE_INFINITY
        }
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
     * Rotates this matrix about the origin by [angle] radians
     * @param angle the angle to rotate by in radians
     *
     * @throws IllegalStateException if this matrix is [frozen]
     */
    fun rotate(angle: Double) {
        transform.setIdentity()
        val sin = Math.sin(angle)
        val cos = Math.cos(angle)
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
        return vec(point.x, point.y)
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

    companion object {
        /**
         * A frozen identity matrix
         */
        @JvmField
        val identity = Matrix3().frozen()
    }
}
