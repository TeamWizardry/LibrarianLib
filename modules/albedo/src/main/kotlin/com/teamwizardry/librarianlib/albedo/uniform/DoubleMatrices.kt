package com.teamwizardry.librarianlib.albedo.uniform

import com.teamwizardry.librarianlib.core.mixin.IMatrix3f
import com.teamwizardry.librarianlib.core.mixin.IMatrix4f
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL40

public sealed class DoubleMatrixUniform(name: String, glConstant: Int, public val columns: Int, public val rows: Int) :
    Uniform(name, glConstant) {
    protected var values: DoubleArray = DoubleArray(columns * rows)

    public operator fun get(column: Int, row: Int): Double {
        checkIndex(column, row)
        return values[column * rows + row]
    }

    public operator fun set(column: Int, row: Int, value: Double) {
        checkIndex(column, row)
        values[column * rows + row] = value
    }

    private fun checkIndex(column: Int, row: Int) {
        val columnsOOB = column < 0 || column > columns
        val rowsOOB = row < 0 || row > rows
        when {
            columnsOOB && rowsOOB ->
                throw IndexOutOfBoundsException("Column $column and row $row are out of bounds for a $columns x $rows matrix")
            columnsOOB ->
                throw IndexOutOfBoundsException("Column $column is out of bounds for a $columns x $rows matrix")
            rowsOOB ->
                throw IndexOutOfBoundsException("Row $row is out of bounds for a $columns x $rows matrix")
        }
    }

}

public sealed class DoubleMatrixArrayUniform(
    name: String,
    glConstant: Int,
    length: Int,
    public val columns: Int,
    public val rows: Int
) : ArrayUniform(name, glConstant, length) {
    private val stride = columns * rows
    protected val values: DoubleArray = DoubleArray(length * stride)

    public operator fun get(index: Int, column: Int, row: Int): Double {
        return values[index * stride + column * rows + row]
    }

    public operator fun set(index: Int, column: Int, row: Int, value: Double) {
        values[index * stride + column * rows + row] = value
    }

    private fun checkIndex(index: Int, column: Int, row: Int) {
        val indexOOB = index < 0 || index > length
        val columnsOOB = column < 0 || column > columns
        val rowsOOB = row < 0 || row > rows
        when {
            indexOOB ->
                throw IndexOutOfBoundsException("Index $index is out of bounds for a matrix array of length $length")
            columnsOOB && rowsOOB ->
                throw IndexOutOfBoundsException("Column $column and row $row are out of bounds for a $columns x $rows matrix")
            columnsOOB ->
                throw IndexOutOfBoundsException("Column $column is out of bounds for a $columns x $rows matrix")
            rowsOOB ->
                throw IndexOutOfBoundsException("Row $row is out of bounds for a $columns x $rows matrix")
        }
    }
}

public class DoubleMat2x2Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT2, 2, 2) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(m00: Double, m01: Double, m10: Double, m11: Double) {
        this.m00 = m00
        this.m01 = m01
        this.m10 = m10
        this.m11 = m11
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(column: Int): Vec2d {
        return vec(this[column, 0], this[column, 1])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(column: Int, value: Vec2d) {
        this[column, 0] = value.x
        this[column, 1] = value.y
    }

    override fun push() {
        GL40.glUniformMatrix2dv(location, transpose, values)
    }
}

public class DoubleMat2x2ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT2, length, 2, 2) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(index: Int, m00: Double, m01: Double, m10: Double, m11: Double) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec2d {
        return vec(this[index, column, 0], this[index, column, 1])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec2d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
    }

    override fun push() {
        GL40.glUniformMatrix2dv(location, transpose, values)
    }
}

public class DoubleMat3x3Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT3, 3, 3) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 0, Row 2 */
    public var m02: Double
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 1, Row 2 */
    public var m12: Double
        get() = this[1, 2]
        set(value) {
            this[1, 2] = value
        }

    /** Column 2, Row 0 */
    public var m20: Double
        get() = this[2, 0]
        set(value) {
            this[2, 0] = value
        }

    /** Column 2, Row 1 */
    public var m21: Double
        get() = this[2, 1]
        set(value) {
            this[2, 1] = value
        }

    /** Column 2, Row 2 */
    public var m22: Double
        get() = this[2, 2]
        set(value) {
            this[2, 2] = value
        }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        m00: Double,
        m01: Double,
        m02: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m20: Double,
        m21: Double,
        m22: Double
    ) {
        this.m00 = m00
        this.m01 = m01
        this.m02 = m02

        this.m10 = m10
        this.m11 = m11
        this.m12 = m12

        this.m20 = m20
        this.m21 = m21
        this.m22 = m22
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(column: Int): Vec3d {
        return vec(this[column, 0], this[column, 1], this[column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(column: Int, value: Vec3d) {
        this[column, 0] = value.x
        this[column, 1] = value.y
        this[column, 2] = value.z
    }

    public fun set(matrix: Matrix3d) {
        this.set(
            matrix.m00, matrix.m10, matrix.m20,
            matrix.m01, matrix.m11, matrix.m21,
            matrix.m02, matrix.m12, matrix.m22,
        )
    }

    public fun set(matrix: Matrix3f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix3f
        this.set(
            imatrix.m00.toDouble(), imatrix.m10.toDouble(), imatrix.m20.toDouble(),
            imatrix.m01.toDouble(), imatrix.m11.toDouble(), imatrix.m21.toDouble(),
            imatrix.m02.toDouble(), imatrix.m12.toDouble(), imatrix.m22.toDouble(),
        )
    }

    override fun push() {
        GL40.glUniformMatrix3dv(location, transpose, values)
    }
}

public class DoubleMat3x3ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT3, length, 3, 3) {

    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 0, Row 2 */
    public fun getM02(index: Int): Double = this[index, 0, 2]
    public fun setM02(index: Int, value: Double) {
        this[index, 0, 2] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 1, Row 2 */
    public fun getM12(index: Int): Double = this[index, 1, 2]
    public fun setM12(index: Int, value: Double) {
        this[index, 1, 2] = value
    }

    /** Column 2, Row 0 */
    public fun getM20(index: Int): Double = this[index, 2, 0]
    public fun setM20(index: Int, value: Double) {
        this[index, 2, 0] = value
    }

    /** Column 2, Row 1 */
    public fun getM21(index: Int): Double = this[index, 2, 1]
    public fun setM21(index: Int, value: Double) {
        this[index, 2, 1] = value
    }

    /** Column 2, Row 2 */
    public fun getM22(index: Int): Double = this[index, 2, 2]
    public fun setM22(index: Int, value: Double) {
        this[index, 2, 2] = value
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        index: Int,
        m00: Double,
        m01: Double,
        m02: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m20: Double,
        m21: Double,
        m22: Double
    ) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 0, 2] = m02

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
        this[index, 1, 2] = m12

        this[index, 2, 0] = m20
        this[index, 2, 1] = m21
        this[index, 2, 2] = m22
    }

    public fun set(index: Int, matrix: Matrix3d) {
        this.set(
            index,
            matrix.m00, matrix.m10, matrix.m20,
            matrix.m01, matrix.m11, matrix.m21,
            matrix.m02, matrix.m12, matrix.m22
        )
    }

    public fun set(index: Int, matrix: Matrix3f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix3f
        this.set(
            index,
            imatrix.m00.toDouble(), imatrix.m10.toDouble(), imatrix.m20.toDouble(),
            imatrix.m01.toDouble(), imatrix.m11.toDouble(), imatrix.m21.toDouble(),
            imatrix.m02.toDouble(), imatrix.m12.toDouble(), imatrix.m22.toDouble(),
        )
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec3d {
        return vec(this[index, column, 0], this[index, column, 1], this[index, column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec3d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
        this[index, column, 2] = value.z
    }

    override fun push() {
        GL40.glUniformMatrix3dv(location, transpose, values)
    }
}

public class DoubleMat4x4Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT4, 4, 4) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 0, Row 2 */
    public var m02: Double
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /** Column 0, Row 3 */
    public var m03: Double
        get() = this[0, 3]
        set(value) {
            this[0, 3] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 1, Row 2 */
    public var m12: Double
        get() = this[1, 2]
        set(value) {
            this[1, 2] = value
        }

    /** Column 1, Row 3 */
    public var m13: Double
        get() = this[1, 3]
        set(value) {
            this[1, 3] = value
        }

    /** Column 2, Row 0 */
    public var m20: Double
        get() = this[2, 0]
        set(value) {
            this[2, 0] = value
        }

    /** Column 2, Row 1 */
    public var m21: Double
        get() = this[2, 1]
        set(value) {
            this[2, 1] = value
        }

    /** Column 2, Row 2 */
    public var m22: Double
        get() = this[2, 2]
        set(value) {
            this[2, 2] = value
        }

    /** Column 2, Row 3 */
    public var m23: Double
        get() = this[2, 3]
        set(value) {
            this[2, 3] = value
        }

    /** Column 3, Row 0 */
    public var m30: Double
        get() = this[3, 0]
        set(value) {
            this[3, 0] = value
        }

    /** Column 3, Row 1 */
    public var m31: Double
        get() = this[3, 1]
        set(value) {
            this[3, 1] = value
        }

    /** Column 3, Row 2 */
    public var m32: Double
        get() = this[3, 2]
        set(value) {
            this[3, 2] = value
        }

    /** Column 3, Row 3 */
    public var m33: Double
        get() = this[3, 3]
        set(value) {
            this[3, 3] = value
        }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        m00: Double,
        m01: Double,
        m02: Double,
        m03: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m13: Double,
        m20: Double,
        m21: Double,
        m22: Double,
        m23: Double,
        m30: Double,
        m31: Double,
        m32: Double,
        m33: Double
    ) {
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

    public fun set(matrix: Matrix4d) {
        this.set(
            matrix.m00, matrix.m10, matrix.m20, matrix.m30,
            matrix.m01, matrix.m11, matrix.m21, matrix.m31,
            matrix.m02, matrix.m12, matrix.m22, matrix.m32,
            matrix.m03, matrix.m13, matrix.m23, matrix.m33
        )
    }

    public fun set(matrix: Matrix4f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix4f
        this.set(
            imatrix.m00.toDouble(), imatrix.m10.toDouble(), imatrix.m20.toDouble(), imatrix.m30.toDouble(),
            imatrix.m01.toDouble(), imatrix.m11.toDouble(), imatrix.m21.toDouble(), imatrix.m31.toDouble(),
            imatrix.m02.toDouble(), imatrix.m12.toDouble(), imatrix.m22.toDouble(), imatrix.m32.toDouble(),
            imatrix.m03.toDouble(), imatrix.m13.toDouble(), imatrix.m23.toDouble(), imatrix.m33.toDouble(),
        )
    }

    override fun push() {
        GL40.glUniformMatrix4dv(location, transpose, values)
    }
}

public class DoubleMat4x4ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT4, length, 4, 4) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 0, Row 2 */
    public fun getM02(index: Int): Double = this[index, 0, 2]
    public fun setM02(index: Int, value: Double) {
        this[index, 0, 2] = value
    }

    /** Column 0, Row 3 */
    public fun getM03(index: Int): Double = this[index, 0, 3]
    public fun setM03(index: Int, value: Double) {
        this[index, 0, 3] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 1, Row 2 */
    public fun getM12(index: Int): Double = this[index, 1, 2]
    public fun setM12(index: Int, value: Double) {
        this[index, 1, 2] = value
    }

    /** Column 1, Row 3 */
    public fun getM13(index: Int): Double = this[index, 1, 3]
    public fun setM13(index: Int, value: Double) {
        this[index, 1, 3] = value
    }

    /** Column 2, Row 0 */
    public fun getM20(index: Int): Double = this[index, 2, 0]
    public fun setM20(index: Int, value: Double) {
        this[index, 2, 0] = value
    }

    /** Column 2, Row 1 */
    public fun getM21(index: Int): Double = this[index, 2, 1]
    public fun setM21(index: Int, value: Double) {
        this[index, 2, 1] = value
    }

    /** Column 2, Row 2 */
    public fun getM22(index: Int): Double = this[index, 2, 2]
    public fun setM22(index: Int, value: Double) {
        this[index, 2, 2] = value
    }

    /** Column 2, Row 3 */
    public fun getM23(index: Int): Double = this[index, 2, 3]
    public fun setM23(index: Int, value: Double) {
        this[index, 2, 3] = value
    }

    /** Column 3, Row 0 */
    public fun getM30(index: Int): Double = this[index, 3, 0]
    public fun setM30(index: Int, value: Double) {
        this[index, 3, 0] = value
    }

    /** Column 3, Row 3 */
    public fun getM31(index: Int): Double = this[index, 3, 1]
    public fun setM31(index: Int, value: Double) {
        this[index, 3, 1] = value
    }

    /** Column 3, Row 2 */
    public fun getM32(index: Int): Double = this[index, 3, 2]
    public fun setM32(index: Int, value: Double) {
        this[index, 3, 2] = value
    }

    /** Column 3, Row 3 */
    public fun getM33(index: Int): Double = this[index, 3, 3]
    public fun setM33(index: Int, value: Double) {
        this[index, 3, 3] = value
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec3d {
        return vec(this[index, column, 0], this[index, column, 1], this[index, column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec3d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
        this[index, column, 2] = value.z
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        index: Int,
        m00: Double,
        m01: Double,
        m02: Double,
        m03: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m13: Double,
        m20: Double,
        m21: Double,
        m22: Double,
        m23: Double,
        m30: Double,
        m31: Double,
        m32: Double,
        m33: Double
    ) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 0, 2] = m02
        this[index, 0, 3] = m03

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
        this[index, 1, 2] = m12
        this[index, 1, 3] = m13

        this[index, 2, 0] = m20
        this[index, 2, 1] = m21
        this[index, 2, 2] = m22
        this[index, 2, 3] = m23

        this[index, 3, 0] = m30
        this[index, 3, 1] = m31
        this[index, 3, 2] = m32
        this[index, 3, 3] = m33
    }

    public fun set(index: Int, matrix: Matrix4d) {
        this.set(
            index,
            matrix.m00, matrix.m10, matrix.m20, matrix.m30,
            matrix.m01, matrix.m11, matrix.m21, matrix.m31,
            matrix.m02, matrix.m12, matrix.m22, matrix.m32,
            matrix.m03, matrix.m13, matrix.m23, matrix.m33
        )
    }

    public fun set(index: Int, matrix: Matrix4f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix4f
        this.set(
            index,
            imatrix.m00.toDouble(), imatrix.m10.toDouble(), imatrix.m20.toDouble(), imatrix.m30.toDouble(),
            imatrix.m01.toDouble(), imatrix.m11.toDouble(), imatrix.m21.toDouble(), imatrix.m31.toDouble(),
            imatrix.m02.toDouble(), imatrix.m12.toDouble(), imatrix.m22.toDouble(), imatrix.m32.toDouble(),
            imatrix.m03.toDouble(), imatrix.m13.toDouble(), imatrix.m23.toDouble(), imatrix.m33.toDouble()
        )
    }

    override fun push() {
        GL40.glUniformMatrix4dv(location, transpose, values)
    }
}

public class DoubleMat2x3Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT2x3, 2, 3) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 0, Row 2 */
    public var m02: Double
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 1, Row 2 */
    public var m12: Double
        get() = this[1, 2]
        set(value) {
            this[1, 2] = value
        }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(column: Int): Vec3d {
        return vec(this[column, 0], this[column, 1], this[column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(column: Int, value: Vec3d) {
        this[column, 0] = value.x
        this[column, 1] = value.y
        this[column, 2] = value.z
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(m00: Double, m01: Double, m02: Double, m10: Double, m11: Double, m12: Double) {
        this.m00 = m00
        this.m01 = m01
        this.m02 = m02

        this.m10 = m10
        this.m11 = m11
        this.m12 = m12
    }

    override fun push() {
        GL40.glUniformMatrix2x3dv(location, transpose, values)
    }
}

public class DoubleMat2x3ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT2x3, length, 2, 3) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 0, Row 2 */
    public fun getM02(index: Int): Double = this[index, 0, 2]
    public fun setM02(index: Int, value: Double) {
        this[index, 0, 2] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 1, Row 2 */
    public fun getM12(index: Int): Double = this[index, 1, 2]
    public fun setM12(index: Int, value: Double) {
        this[index, 1, 2] = value
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec3d {
        return vec(this[index, column, 0], this[index, column, 1], this[index, column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec3d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
        this[index, column, 2] = value.z
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(index: Int, m00: Double, m01: Double, m02: Double, m10: Double, m11: Double, m12: Double) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 0, 2] = m02

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
        this[index, 1, 2] = m12
    }

    override fun push() {
        GL40.glUniformMatrix2x3dv(location, transpose, values)
    }
}

public class DoubleMat2x4Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT2x4, 2, 4) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 0, Row 2 */
    public var m02: Double
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /** Column 0, Row 3 */
    public var m03: Double
        get() = this[0, 3]
        set(value) {
            this[0, 3] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 1, Row 2 */
    public var m12: Double
        get() = this[1, 2]
        set(value) {
            this[1, 2] = value
        }

    /** Column 1, Row 3 */
    public var m13: Double
        get() = this[1, 3]
        set(value) {
            this[1, 3] = value
        }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        m00: Double,
        m01: Double,
        m02: Double,
        m03: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m13: Double
    ) {
        this.m00 = m00
        this.m01 = m01
        this.m02 = m02
        this.m03 = m03

        this.m10 = m10
        this.m11 = m11
        this.m12 = m12
        this.m13 = m13
    }

    override fun push() {
        GL40.glUniformMatrix2x4dv(location, transpose, values)
    }
}

public class DoubleMat2x4ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT2x4, length, 2, 4) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 0, Row 2 */
    public fun getM02(index: Int): Double = this[index, 0, 2]
    public fun setM02(index: Int, value: Double) {
        this[index, 0, 2] = value
    }

    /** Column 0, Row 3 */
    public fun getM03(index: Int): Double = this[index, 0, 3]
    public fun setM03(index: Int, value: Double) {
        this[index, 0, 3] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 1, Row 2 */
    public fun getM12(index: Int): Double = this[index, 1, 2]
    public fun setM12(index: Int, value: Double) {
        this[index, 1, 2] = value
    }

    /** Column 1, Row 3 */
    public fun getM13(index: Int): Double = this[index, 1, 3]
    public fun setM13(index: Int, value: Double) {
        this[index, 1, 3] = value
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        index: Int,
        m00: Double,
        m01: Double,
        m02: Double,
        m03: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m13: Double
    ) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 0, 2] = m02
        this[index, 0, 3] = m03

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
        this[index, 1, 2] = m12
        this[index, 1, 3] = m13
    }

    override fun push() {
        GL40.glUniformMatrix2x4dv(location, transpose, values)
    }
}

public class DoubleMat3x2Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT3x2, 3, 2) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 2, Row 0 */
    public var m20: Double
        get() = this[2, 0]
        set(value) {
            this[2, 0] = value
        }

    /** Column 2, Row 1 */
    public var m21: Double
        get() = this[2, 1]
        set(value) {
            this[2, 1] = value
        }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(column: Int): Vec2d {
        return vec(this[column, 0], this[column, 1])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(column: Int, value: Vec2d) {
        this[column, 0] = value.x
        this[column, 1] = value.y
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(m00: Double, m01: Double, m10: Double, m11: Double, m20: Double, m21: Double) {
        this.m00 = m00
        this.m01 = m01

        this.m10 = m10
        this.m11 = m11

        this.m20 = m20
        this.m21 = m21
    }

    override fun push() {
        GL40.glUniformMatrix3x2dv(location, transpose, values)
    }
}

public class DoubleMat3x2ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT3x2, length, 3, 2) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 2, Row 0 */
    public fun getM20(index: Int): Double = this[index, 2, 0]
    public fun setM20(index: Int, value: Double) {
        this[index, 2, 0] = value
    }

    /** Column 2, Row 1 */
    public fun getM21(index: Int): Double = this[index, 2, 1]
    public fun setM21(index: Int, value: Double) {
        this[index, 2, 1] = value
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec2d {
        return vec(this[index, column, 0], this[index, column, 1])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec2d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(index: Int, m00: Double, m01: Double, m10: Double, m11: Double, m20: Double, m21: Double) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11

        this[index, 2, 0] = m20
        this[index, 2, 1] = m21
    }

    override fun push() {
        GL40.glUniformMatrix3x2dv(location, transpose, values)
    }
}

public class DoubleMat3x4Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT3x4, 3, 4) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 0, Row 2 */
    public var m02: Double
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /** Column 0, Row 3 */
    public var m03: Double
        get() = this[0, 3]
        set(value) {
            this[0, 3] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 1, Row 2 */
    public var m12: Double
        get() = this[1, 2]
        set(value) {
            this[1, 2] = value
        }

    /** Column 1, Row 3 */
    public var m13: Double
        get() = this[1, 3]
        set(value) {
            this[1, 3] = value
        }

    /** Column 2, Row 0 */
    public var m20: Double
        get() = this[2, 0]
        set(value) {
            this[2, 0] = value
        }

    /** Column 2, Row 1 */
    public var m21: Double
        get() = this[2, 1]
        set(value) {
            this[2, 1] = value
        }

    /** Column 2, Row 2 */
    public var m22: Double
        get() = this[2, 2]
        set(value) {
            this[2, 2] = value
        }

    /** Column 2, Row 3 */
    public var m23: Double
        get() = this[2, 3]
        set(value) {
            this[2, 3] = value
        }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        m00: Double,
        m01: Double,
        m02: Double,
        m03: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m13: Double,
        m20: Double,
        m21: Double,
        m22: Double,
        m23: Double
    ) {
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
    }

    override fun push() {
        GL40.glUniformMatrix3x4dv(location, transpose, values)
    }
}

public class DoubleMat3x4ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT3x4, length, 3, 4) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 0, Row 2 */
    public fun getM02(index: Int): Double = this[index, 0, 2]
    public fun setM02(index: Int, value: Double) {
        this[index, 0, 2] = value
    }

    /** Column 0, Row 3 */
    public fun getM03(index: Int): Double = this[index, 0, 3]
    public fun setM03(index: Int, value: Double) {
        this[index, 0, 3] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 1, Row 2 */
    public fun getM12(index: Int): Double = this[index, 1, 2]
    public fun setM12(index: Int, value: Double) {
        this[index, 1, 2] = value
    }

    /** Column 1, Row 3 */
    public fun getM13(index: Int): Double = this[index, 1, 3]
    public fun setM13(index: Int, value: Double) {
        this[index, 1, 3] = value
    }

    /** Column 2, Row 0 */
    public fun getM20(index: Int): Double = this[index, 2, 0]
    public fun setM20(index: Int, value: Double) {
        this[index, 2, 0] = value
    }

    /** Column 2, Row 1 */
    public fun getM21(index: Int): Double = this[index, 2, 1]
    public fun setM21(index: Int, value: Double) {
        this[index, 2, 1] = value
    }

    /** Column 2, Row 2 */
    public fun getM22(index: Int): Double = this[index, 2, 2]
    public fun setM22(index: Int, value: Double) {
        this[index, 2, 2] = value
    }

    /** Column 2, Row 3 */
    public fun getM23(index: Int): Double = this[index, 2, 3]
    public fun setM23(index: Int, value: Double) {
        this[index, 2, 3] = value
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        index: Int,
        m00: Double,
        m01: Double,
        m02: Double,
        m03: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m13: Double,
        m20: Double,
        m21: Double,
        m22: Double,
        m23: Double
    ) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 0, 2] = m02
        this[index, 0, 3] = m03

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
        this[index, 1, 2] = m12
        this[index, 1, 3] = m13

        this[index, 2, 0] = m20
        this[index, 2, 1] = m21
        this[index, 2, 2] = m22
        this[index, 2, 3] = m23
    }

    override fun push() {
        GL40.glUniformMatrix3x4dv(location, transpose, values)
    }
}

public class DoubleMat4x2Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT4x2, 4, 2) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 2, Row 0 */
    public var m20: Double
        get() = this[2, 0]
        set(value) {
            this[2, 0] = value
        }

    /** Column 2, Row 1 */
    public var m21: Double
        get() = this[2, 1]
        set(value) {
            this[2, 1] = value
        }

    /** Column 3, Row 0 */
    public var m30: Double
        get() = this[3, 0]
        set(value) {
            this[3, 0] = value
        }

    /** Column 3, Row 1 */
    public var m31: Double
        get() = this[3, 1]
        set(value) {
            this[3, 1] = value
        }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(column: Int): Vec2d {
        return vec(this[column, 0], this[column, 1])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(column: Int, value: Vec2d) {
        this[column, 0] = value.x
        this[column, 1] = value.y
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        m00: Double,
        m01: Double,
        m10: Double,
        m11: Double,
        m20: Double,
        m21: Double,
        m30: Double,
        m31: Double
    ) {
        this.m00 = m00
        this.m01 = m01

        this.m10 = m10
        this.m11 = m11

        this.m20 = m20
        this.m21 = m21

        this.m30 = m30
        this.m31 = m31
    }

    override fun push() {
        GL40.glUniformMatrix4x2dv(location, transpose, values)
    }
}

public class DoubleMat4x2ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT4x2, length, 4, 2) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 2, Row 0 */
    public fun getM20(index: Int): Double = this[index, 2, 0]
    public fun setM20(index: Int, value: Double) {
        this[index, 2, 0] = value
    }

    /** Column 2, Row 1 */
    public fun getM21(index: Int): Double = this[index, 2, 1]
    public fun setM21(index: Int, value: Double) {
        this[index, 2, 1] = value
    }

    /** Column 3, Row 0 */
    public fun getM30(index: Int): Double = this[index, 3, 0]
    public fun setM30(index: Int, value: Double) {
        this[index, 3, 0] = value
    }

    /** Column 3, Row 3 */
    public fun getM31(index: Int): Double = this[index, 3, 1]
    public fun setM31(index: Int, value: Double) {
        this[index, 3, 1] = value
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec2d {
        return vec(this[index, column, 0], this[index, column, 1])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec2d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        index: Int,
        m00: Double,
        m01: Double,
        m10: Double,
        m11: Double,
        m20: Double,
        m21: Double,
        m30: Double,
        m31: Double
    ) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11

        this[index, 2, 0] = m20
        this[index, 2, 1] = m21

        this[index, 3, 0] = m30
        this[index, 3, 1] = m31
    }

    override fun push() {
        GL40.glUniformMatrix4x2dv(location, transpose, values)
    }
}

public class DoubleMat4x3Uniform(name: String, public val transpose: Boolean) :
    DoubleMatrixUniform(name, GL40.GL_DOUBLE_MAT4x3, 4, 3) {
    /** Column 0, Row 0 */
    public var m00: Double
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /** Column 0, Row 1 */
    public var m01: Double
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /** Column 0, Row 2 */
    public var m02: Double
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /** Column 1, Row 0 */
    public var m10: Double
        get() = this[1, 0]
        set(value) {
            this[1, 0] = value
        }

    /** Column 1, Row 1 */
    public var m11: Double
        get() = this[1, 1]
        set(value) {
            this[1, 1] = value
        }

    /** Column 1, Row 2 */
    public var m12: Double
        get() = this[1, 2]
        set(value) {
            this[1, 2] = value
        }

    /** Column 2, Row 0 */
    public var m20: Double
        get() = this[2, 0]
        set(value) {
            this[2, 0] = value
        }

    /** Column 2, Row 1 */
    public var m21: Double
        get() = this[2, 1]
        set(value) {
            this[2, 1] = value
        }

    /** Column 2, Row 2 */
    public var m22: Double
        get() = this[2, 2]
        set(value) {
            this[2, 2] = value
        }

    /** Column 3, Row 0 */
    public var m30: Double
        get() = this[3, 0]
        set(value) {
            this[3, 0] = value
        }

    /** Column 3, Row 1 */
    public var m31: Double
        get() = this[3, 1]
        set(value) {
            this[3, 1] = value
        }

    /** Column 2, Row 2 */
    public var m32: Double
        get() = this[3, 2]
        set(value) {
            this[3, 2] = value
        }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(column: Int): Vec3d {
        return vec(this[column, 0], this[column, 1], this[column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(column: Int, value: Vec3d) {
        this[column, 0] = value.x
        this[column, 1] = value.y
        this[column, 2] = value.z
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        m00: Double,
        m01: Double,
        m02: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m20: Double,
        m21: Double,
        m22: Double,
        m30: Double,
        m31: Double,
        m32: Double
    ) {
        this.m00 = m00
        this.m01 = m01
        this.m02 = m02

        this.m10 = m10
        this.m11 = m11
        this.m12 = m12

        this.m20 = m20
        this.m21 = m21
        this.m22 = m22

        this.m30 = m30
        this.m31 = m31
        this.m32 = m32
    }

    override fun push() {
        GL40.glUniformMatrix4x3dv(location, transpose, values)
    }
}

public class DoubleMat4x3ArrayUniform(name: String, public val transpose: Boolean, length: Int) :
    DoubleMatrixArrayUniform(name, GL40.GL_DOUBLE_MAT4x3, length, 4, 3) {
    /** Column 0, Row 0 */
    public fun getM00(index: Int): Double = this[index, 0, 0]
    public fun setM00(index: Int, value: Double) {
        this[index, 0, 0] = value
    }

    /** Column 0, Row 1 */
    public fun getM01(index: Int): Double = this[index, 0, 1]
    public fun setM01(index: Int, value: Double) {
        this[index, 0, 1] = value
    }

    /** Column 0, Row 2 */
    public fun getM02(index: Int): Double = this[index, 0, 2]
    public fun setM02(index: Int, value: Double) {
        this[index, 0, 2] = value
    }

    /** Column 1, Row 0 */
    public fun getM10(index: Int): Double = this[index, 1, 0]
    public fun setM10(index: Int, value: Double) {
        this[index, 1, 0] = value
    }

    /** Column 1, Row 1 */
    public fun getM11(index: Int): Double = this[index, 1, 1]
    public fun setM11(index: Int, value: Double) {
        this[index, 1, 1] = value
    }

    /** Column 1, Row 2 */
    public fun getM12(index: Int): Double = this[index, 1, 2]
    public fun setM12(index: Int, value: Double) {
        this[index, 1, 2] = value
    }

    /** Column 2, Row 0 */
    public fun getM20(index: Int): Double = this[index, 2, 0]
    public fun setM20(index: Int, value: Double) {
        this[index, 2, 0] = value
    }

    /** Column 2, Row 1 */
    public fun getM21(index: Int): Double = this[index, 2, 1]
    public fun setM21(index: Int, value: Double) {
        this[index, 2, 1] = value
    }

    /** Column 2, Row 2 */
    public fun getM22(index: Int): Double = this[index, 2, 2]
    public fun setM22(index: Int, value: Double) {
        this[index, 2, 2] = value
    }

    /** Column 3, Row 0 */
    public fun getM30(index: Int): Double = this[index, 3, 0]
    public fun setM30(index: Int, value: Double) {
        this[index, 3, 0] = value
    }

    /** Column 3, Row 3 */
    public fun getM31(index: Int): Double = this[index, 3, 1]
    public fun setM31(index: Int, value: Double) {
        this[index, 3, 1] = value
    }

    /** Column 3, Row 2 */
    public fun getM32(index: Int): Double = this[index, 3, 2]
    public fun setM32(index: Int, value: Double) {
        this[index, 3, 2] = value
    }

    /**
     * Get the specified column as a vector
     */
    public operator fun get(index: Int, column: Int): Vec3d {
        return vec(this[index, column, 0], this[index, column, 1], this[index, column, 2])
    }

    /**
     * Set the specified column as a vector
     */
    public operator fun set(index: Int, column: Int, value: Vec3d) {
        this[index, column, 0] = value.x
        this[index, column, 1] = value.y
        this[index, column, 2] = value.z
    }

    /**
     * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
     */
    public fun set(
        index: Int,
        m00: Double,
        m01: Double,
        m02: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m20: Double,
        m21: Double,
        m22: Double,
        m30: Double,
        m31: Double,
        m32: Double
    ) {
        this[index, 0, 0] = m00
        this[index, 0, 1] = m01
        this[index, 0, 2] = m02

        this[index, 1, 0] = m10
        this[index, 1, 1] = m11
        this[index, 1, 2] = m12

        this[index, 2, 0] = m20
        this[index, 2, 1] = m21
        this[index, 2, 2] = m22

        this[index, 3, 0] = m30
        this[index, 3, 1] = m31
        this[index, 3, 2] = m32
    }

    override fun push() {
        GL40.glUniformMatrix4x3dv(location, transpose, values)
    }
}
