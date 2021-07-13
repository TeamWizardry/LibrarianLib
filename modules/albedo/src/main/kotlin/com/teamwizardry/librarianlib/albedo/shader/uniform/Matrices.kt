package com.teamwizardry.librarianlib.albedo.shader.uniform

import com.teamwizardry.librarianlib.core.mixin.IMatrix3f
import com.teamwizardry.librarianlib.core.mixin.IMatrix4f
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL21

/**
 * All the matrix uniform APIs represent the matrix in *row* major order. Internally they're transformed to column major
 * order, but this doesn't matter to you.
 *
 * The only time the major order matters is when you're asked to turn a flat array like `[a, b, c, d, e, f, g, h, i]`
 * into a matrix. The matrix itself *doesn't rotate.* The only difference is the order you string the values together
 * when you make them into a flat array. With that out of the way, here's what that looks like for a 3x3 matrix.
 * Keep in mind that these letters have nothing to do with the math, the only significance is the order they're stored
 * in memory.
 *
 * ```
 * "Row major"
 * ⎡ a b c ⎤
 * ⎢ d e f ⎥
 * ⎣ g h i ⎦
 *
 * "Column major"
 * ⎡ a d g ⎤
 * ⎢ b e h ⎥
 * ⎣ c f i ⎦
 * ```
 */
public sealed class MatrixUniform(name: String, glConstant: Int, public val columns: Int, public val rows: Int) : Uniform(name, glConstant) {
    protected var values: FloatArray = FloatArray(columns * rows)

    protected operator fun get(row: Int, column: Int): Float {
        return values[column * rows + row]
    }

    protected operator fun set(row: Int, column: Int, value: Float) {
        values[column * rows + row] = value
    }
}

public sealed class MatrixArrayUniform(
    name: String,
    glConstant: Int,
    length: Int,
    public val columns: Int,
    public val rows: Int
) : ArrayUniform(name, glConstant, length) {
    private val stride = columns * rows
    protected val values: FloatArray = FloatArray(length * stride)

    protected operator fun get(index: Int, row: Int, column: Int): Float {
        return values[index * stride + column * rows + row]
    }

    protected operator fun set(index: Int, row: Int, column: Int, value: Float) {
        values[index * stride + column * rows + row] = value
    }
}

public class Mat2x2Uniform(name: String) : MatrixUniform(name, GL20.GL_FLOAT_MAT2, columns = 2, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎣ m10 m11 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float,
        m10: Float, m11: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01
        this[1, 0] = m10; this[1, 1] = m11
    }

    override fun push() {
        GL20.glUniformMatrix2fv(location, false, values)
    }
}

public class Mat2x2ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL20.GL_FLOAT_MAT2, length, columns = 2, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎣ m10 m11 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float,
        m10: Float, m11: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01
        this[index, 1, 0] = m10; this[index, 1, 1] = m11
    }

    override fun push() {
        GL20.glUniformMatrix2fv(location, false, values)
    }
}

public class Mat3x3Uniform(name: String) : MatrixUniform(name, GL20.GL_FLOAT_MAT3, columns = 3, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎣ m20 m21 m22 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
        m20: Float, m21: Float, m22: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22
    }

    public fun set(matrix: Matrix3d) {
        this.set(
            matrix.m00.toFloat(), matrix.m01.toFloat(), matrix.m02.toFloat(),
            matrix.m10.toFloat(), matrix.m11.toFloat(), matrix.m12.toFloat(),
            matrix.m20.toFloat(), matrix.m21.toFloat(), matrix.m22.toFloat(),
        )
    }

    public fun set(matrix: Matrix3f) {
        val imatrix = mixinCast<IMatrix3f>(matrix)
        this.set(
            imatrix.m00, imatrix.m01, imatrix.m02,
            imatrix.m10, imatrix.m11, imatrix.m12,
            imatrix.m20, imatrix.m21, imatrix.m22,
        )
    }

    override fun push() {
        GL20.glUniformMatrix3fv(location, false, values)
    }
}

public class Mat3x3ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL20.GL_FLOAT_MAT3, length, columns = 3, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎣ m20 m21 m22 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
        m20: Float, m21: Float, m22: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22
    }

    public fun set(index: Int, matrix: Matrix3d) {
        this.set(
            index,
            matrix.m00.toFloat(), matrix.m01.toFloat(), matrix.m02.toFloat(),
            matrix.m10.toFloat(), matrix.m11.toFloat(), matrix.m12.toFloat(),
            matrix.m20.toFloat(), matrix.m21.toFloat(), matrix.m22.toFloat(),
        )
    }

    public fun set(index: Int, matrix: Matrix3f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix3f
        this.set(
            index,
            imatrix.m00, imatrix.m01, imatrix.m02,
            imatrix.m10, imatrix.m11, imatrix.m12,
            imatrix.m20, imatrix.m21, imatrix.m22,
        )
    }

    override fun push() {
        GL20.glUniformMatrix3fv(location, false, values)
    }
}

public class Mat4x4Uniform(name: String) : MatrixUniform(name, GL20.GL_FLOAT_MAT4, columns = 4, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎢ m20 m21 m22 m23 ⎥
     * ⎣ m30 m31 m32 m33 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
        m30: Float, m31: Float, m32: Float, m33: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02; this[0, 3] = m03
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12; this[1, 3] = m13
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22; this[2, 3] = m23
        this[3, 0] = m30; this[3, 1] = m31; this[3, 2] = m32; this[3, 3] = m33
    }

    public fun set(matrix: Matrix4d) {
        this.set(
            matrix.m00.toFloat(), matrix.m01.toFloat(), matrix.m02.toFloat(), matrix.m03.toFloat(),
            matrix.m10.toFloat(), matrix.m11.toFloat(), matrix.m12.toFloat(), matrix.m13.toFloat(),
            matrix.m20.toFloat(), matrix.m21.toFloat(), matrix.m22.toFloat(), matrix.m23.toFloat(),
            matrix.m30.toFloat(), matrix.m31.toFloat(), matrix.m32.toFloat(), matrix.m33.toFloat(),
        )
    }

    public fun set(matrix: Matrix4f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix4f
        this.set(
            imatrix.m00, imatrix.m01, imatrix.m02, imatrix.m03,
            imatrix.m10, imatrix.m11, imatrix.m12, imatrix.m13,
            imatrix.m20, imatrix.m21, imatrix.m22, imatrix.m23,
            imatrix.m30, imatrix.m31, imatrix.m32, imatrix.m33,
        )
    }

    override fun push() {
        GL20.glUniformMatrix4fv(location, false, values)
    }
}

public class Mat4x4ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL20.GL_FLOAT_MAT4, length, columns = 4, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎢ m20 m21 m22 m23 ⎥
     * ⎣ m30 m31 m32 m33 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
        m30: Float, m31: Float, m32: Float, m33: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02; this[index, 0, 3] = m03
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12; this[index, 1, 3] = m13
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22; this[index, 2, 3] = m23
        this[index, 3, 0] = m30; this[index, 3, 1] = m31; this[index, 3, 2] = m32; this[index, 3, 3] = m33
    }

    public fun set(index: Int, matrix: Matrix4d) {
        this.set(
            index,
            matrix.m00.toFloat(), matrix.m01.toFloat(), matrix.m02.toFloat(), matrix.m03.toFloat(),
            matrix.m10.toFloat(), matrix.m11.toFloat(), matrix.m12.toFloat(), matrix.m13.toFloat(),
            matrix.m20.toFloat(), matrix.m21.toFloat(), matrix.m22.toFloat(), matrix.m23.toFloat(),
            matrix.m30.toFloat(), matrix.m31.toFloat(), matrix.m32.toFloat(), matrix.m33.toFloat(),
        )
    }

    public fun set(index: Int, matrix: Matrix4f) {
        val imatrix = mixinCast<IMatrix4f>(matrix)
        this.set(
            index,
            imatrix.m00, imatrix.m01, imatrix.m02, imatrix.m03,
            imatrix.m10, imatrix.m11, imatrix.m12, imatrix.m13,
            imatrix.m20, imatrix.m21, imatrix.m22, imatrix.m23,
            imatrix.m30, imatrix.m31, imatrix.m32, imatrix.m33,
        )
    }

    override fun push() {
        GL20.glUniformMatrix4fv(location, false, values)
    }
}

public class Mat2x3Uniform(name: String) : MatrixUniform(name, GL21.GL_FLOAT_MAT2x3, columns = 2, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎣ m20 m21 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float,
        m10: Float, m11: Float,
        m20: Float, m21: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01
        this[1, 0] = m10; this[1, 1] = m11
        this[2, 0] = m20; this[2, 1] = m21
    }

    override fun push() {
        GL21.glUniformMatrix2x3fv(location, false, values)
    }
}

public class Mat2x3ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL21.GL_FLOAT_MAT2x3, length, columns = 2, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎣ m20 m21 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float,
        m10: Float, m11: Float,
        m20: Float, m21: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01
        this[index, 1, 0] = m10; this[index, 1, 1] = m11
        this[index, 2, 0] = m20; this[index, 2, 1] = m21
    }

    override fun push() {
        GL21.glUniformMatrix2x3fv(location, false, values)
    }
}

public class Mat2x4Uniform(name: String) : MatrixUniform(name, GL21.GL_FLOAT_MAT2x4, columns = 2, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎢ m20 m21 ⎥
     * ⎣ m30 m31 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float,
        m10: Float, m11: Float,
        m20: Float, m21: Float,
        m30: Float, m31: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01
        this[1, 0] = m10; this[1, 1] = m11
        this[2, 0] = m20; this[2, 1] = m21
        this[3, 0] = m30; this[3, 1] = m31
    }

    override fun push() {
        GL21.glUniformMatrix2x4fv(location, false, values)
    }
}

public class Mat2x4ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL21.GL_FLOAT_MAT2x4, length, columns = 2, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎢ m20 m21 ⎥
     * ⎣ m30 m31 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float,
        m10: Float, m11: Float,
        m20: Float, m21: Float,
        m30: Float, m31: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01
        this[index, 1, 0] = m10; this[index, 1, 1] = m11
        this[index, 2, 0] = m20; this[index, 2, 1] = m21
        this[index, 3, 0] = m30; this[index, 3, 1] = m31
    }

    override fun push() {
        GL21.glUniformMatrix2x4fv(location, false, values)
    }
}

public class Mat3x2Uniform(name: String) : MatrixUniform(name, GL21.GL_FLOAT_MAT3x2, columns = 3, rows = 2) {
    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎣ m10 m11 m12 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02;
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12;
    }

    override fun push() {
        GL21.glUniformMatrix3x2fv(location, false, values)
    }
}

public class Mat3x2ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL21.GL_FLOAT_MAT3x2, length, columns = 3, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎣ m30 m31 m32 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12
    }
    override fun push() {
        GL21.glUniformMatrix3x2fv(location, false, values)
    }
}

public class Mat3x4Uniform(name: String) : MatrixUniform(name, GL21.GL_FLOAT_MAT3x4, columns = 3, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎢ m20 m21 m22 ⎥
     * ⎣ m30 m31 m32 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
        m20: Float, m21: Float, m22: Float,
        m30: Float, m31: Float, m32: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22
        this[3, 0] = m30; this[3, 1] = m31; this[3, 2] = m32
    }

    override fun push() {
        GL21.glUniformMatrix3x4fv(location, false, values)
    }
}

public class Mat3x4ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL21.GL_FLOAT_MAT3x4, length, columns = 3, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎢ m20 m21 m22 ⎥
     * ⎣ m30 m31 m32 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float, m02: Float,
        m10: Float, m11: Float, m12: Float,
        m20: Float, m21: Float, m22: Float,
        m30: Float, m31: Float, m32: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22
        this[index, 3, 0] = m30; this[index, 3, 1] = m31; this[index, 3, 2] = m32
    }

    override fun push() {
        GL21.glUniformMatrix3x4fv(location, false, values)
    }
}

public class Mat4x2Uniform(name: String) : MatrixUniform(name, GL21.GL_FLOAT_MAT4x2, columns = 4, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎣ m10 m11 m12 m13 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02; this[0, 3] = m03
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12; this[1, 3] = m13
    }

    override fun push() {
        GL21.glUniformMatrix4x2fv(location, false, values)
    }
}

public class Mat4x2ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL21.GL_FLOAT_MAT4x2, length, columns = 4, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎣ m10 m11 m12 m13 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02; this[index, 0, 3] = m03
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12; this[index, 1, 3] = m13
    }

    override fun push() {
        GL21.glUniformMatrix4x2fv(location, false, values)
    }
}

public class Mat4x3Uniform(name: String) : MatrixUniform(name, GL21.GL_FLOAT_MAT4x3, columns = 4, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎣ m20 m21 m22 m23 ⎦
     * ```
     */
    public fun set(
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02; this[0, 3] = m03
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12; this[1, 3] = m13
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22; this[2, 3] = m23
    }

    override fun push() {
        GL21.glUniformMatrix4x3fv(location, false, values)
    }
}

public class Mat4x3ArrayUniform(name: String, length: Int) : MatrixArrayUniform(name, GL21.GL_FLOAT_MAT4x3, length, columns = 4, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎣ m20 m21 m22 m23 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Float, m01: Float, m02: Float, m03: Float,
        m10: Float, m11: Float, m12: Float, m13: Float,
        m20: Float, m21: Float, m22: Float, m23: Float,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02; this[index, 0, 3] = m03
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12; this[index, 1, 3] = m13
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22; this[index, 2, 3] = m23
    }

    override fun push() {
        GL21.glUniformMatrix4x3fv(location, false, values)
    }
}
