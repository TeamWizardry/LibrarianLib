package com.teamwizardry.librarianlib.albedo.uniform

import com.teamwizardry.librarianlib.core.mixin.IMatrix3f
import com.teamwizardry.librarianlib.core.mixin.IMatrix4f
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL40

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
public sealed class DoubleMatrixUniform(name: String, glConstant: Int, public val columns: Int, public val rows: Int) : Uniform(name, glConstant) {
    protected var values: DoubleArray = DoubleArray(columns * rows)

    protected operator fun get(row: Int, column: Int): Double {
        return values[column * rows + row]
    }

    protected operator fun set(row: Int, column: Int, value: Double) {
        values[column * rows + row] = value
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

    protected operator fun get(index: Int, row: Int, column: Int): Double {
        return values[index * stride + column * rows + row]
    }

    protected operator fun set(index: Int, row: Int, column: Int, value: Double) {
        values[index * stride + column * rows + row] = value
    }
}

public class DoubleMat2x2Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT2, columns = 2, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎣ m10 m11 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double,
        m10: Double, m11: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01
        this[1, 0] = m10; this[1, 1] = m11
    }

    override fun push() {
        GL40.glUniformMatrix2dv(location, true, values)
    }
}

public class DoubleMat2x2ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT2, length, columns = 2, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎣ m10 m11 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Double, m01: Double,
        m10: Double, m11: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01
        this[index, 1, 0] = m10; this[index, 1, 1] = m11
    }

    override fun push() {
        GL40.glUniformMatrix2dv(location, true, values)
    }
}

public class DoubleMat3x3Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT3, columns = 3, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎣ m20 m21 m22 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
        m20: Double, m21: Double, m22: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22
    }

    public fun set(matrix: Matrix3d) {
        this.set(
            matrix.m00, matrix.m01, matrix.m02,
            matrix.m10, matrix.m11, matrix.m12,
            matrix.m20, matrix.m21, matrix.m22,
        )
    }

    public fun set(matrix: Matrix3f) {
        val imatrix = mixinCast<IMatrix3f>(matrix)
        this.set(
            imatrix.m00.toDouble(), imatrix.m01.toDouble(), imatrix.m02.toDouble(),
            imatrix.m10.toDouble(), imatrix.m11.toDouble(), imatrix.m12.toDouble(),
            imatrix.m20.toDouble(), imatrix.m21.toDouble(), imatrix.m22.toDouble(),
        )
    }

    override fun push() {
        GL40.glUniformMatrix3dv(location, true, values)
    }
}

public class DoubleMat3x3ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT3, length, columns = 3, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎣ m20 m21 m22 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
        m20: Double, m21: Double, m22: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22
    }

    public fun set(index: Int, matrix: Matrix3d) {
        this.set(
            index,
            matrix.m00, matrix.m01, matrix.m02,
            matrix.m10, matrix.m11, matrix.m12,
            matrix.m20, matrix.m21, matrix.m22,
        )
    }

    public fun set(index: Int, matrix: Matrix3f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix3f
        this.set(
            index,
            imatrix.m00.toDouble(), imatrix.m01.toDouble(), imatrix.m02.toDouble(),
            imatrix.m10.toDouble(), imatrix.m11.toDouble(), imatrix.m12.toDouble(),
            imatrix.m20.toDouble(), imatrix.m21.toDouble(), imatrix.m22.toDouble(),
        )
    }

    override fun push() {
        GL40.glUniformMatrix3dv(location, true, values)
    }
}

public class DoubleMat4x4Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT4, columns = 4, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎢ m20 m21 m22 m23 ⎥
     * ⎣ m30 m31 m32 m33 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02; this[0, 3] = m03
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12; this[1, 3] = m13
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22; this[2, 3] = m23
        this[3, 0] = m30; this[3, 1] = m31; this[3, 2] = m32; this[3, 3] = m33
    }

    public fun set(matrix: Matrix4d) {
        this.set(
            matrix.m00, matrix.m01, matrix.m02, matrix.m03,
            matrix.m10, matrix.m11, matrix.m12, matrix.m13,
            matrix.m20, matrix.m21, matrix.m22, matrix.m23,
            matrix.m30, matrix.m31, matrix.m32, matrix.m33,
        )
    }

    public fun set(matrix: Matrix4f) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val imatrix = matrix as IMatrix4f
        this.set(
            imatrix.m00.toDouble(), imatrix.m01.toDouble(), imatrix.m02.toDouble(), imatrix.m03.toDouble(),
            imatrix.m10.toDouble(), imatrix.m11.toDouble(), imatrix.m12.toDouble(), imatrix.m13.toDouble(),
            imatrix.m20.toDouble(), imatrix.m21.toDouble(), imatrix.m22.toDouble(), imatrix.m23.toDouble(),
            imatrix.m30.toDouble(), imatrix.m31.toDouble(), imatrix.m32.toDouble(), imatrix.m33.toDouble(),
        )
    }

    override fun push() {
        GL40.glUniformMatrix4dv(location, true, values)
    }
}

public class DoubleMat4x4ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT4, length, columns = 4, rows = 4) {

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
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02; this[index, 0, 3] = m03
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12; this[index, 1, 3] = m13
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22; this[index, 2, 3] = m23
        this[index, 3, 0] = m30; this[index, 3, 1] = m31; this[index, 3, 2] = m32; this[index, 3, 3] = m33
    }

    public fun set(index: Int, matrix: Matrix4d) {
        this.set(
            index,
            matrix.m00, matrix.m01, matrix.m02, matrix.m03,
            matrix.m10, matrix.m11, matrix.m12, matrix.m13,
            matrix.m20, matrix.m21, matrix.m22, matrix.m23,
            matrix.m30, matrix.m31, matrix.m32, matrix.m33,
        )
    }

    public fun set(index: Int, matrix: Matrix4f) {
        val imatrix = mixinCast<IMatrix4f>(matrix)
        this.set(
            index,
            imatrix.m00.toDouble(), imatrix.m01.toDouble(), imatrix.m02.toDouble(), imatrix.m03.toDouble(),
            imatrix.m10.toDouble(), imatrix.m11.toDouble(), imatrix.m12.toDouble(), imatrix.m13.toDouble(),
            imatrix.m20.toDouble(), imatrix.m21.toDouble(), imatrix.m22.toDouble(), imatrix.m23.toDouble(),
            imatrix.m30.toDouble(), imatrix.m31.toDouble(), imatrix.m32.toDouble(), imatrix.m33.toDouble(),
        )
    }

    override fun push() {
        GL40.glUniformMatrix4dv(location, true, values)
    }
}

public class DoubleMat2x3Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT2x3, columns = 2, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎣ m20 m21 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double,
        m10: Double, m11: Double,
        m20: Double, m21: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01
        this[1, 0] = m10; this[1, 1] = m11
        this[2, 0] = m20; this[2, 1] = m21
    }

    override fun push() {
        GL40.glUniformMatrix2x3dv(location, true, values)
    }
}

public class DoubleMat2x3ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT2x3, length, columns = 2, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎣ m20 m21 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Double, m01: Double,
        m10: Double, m11: Double,
        m20: Double, m21: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01
        this[index, 1, 0] = m10; this[index, 1, 1] = m11
        this[index, 2, 0] = m20; this[index, 2, 1] = m21
    }

    override fun push() {
        GL40.glUniformMatrix2x3dv(location, true, values)
    }
}

public class DoubleMat2x4Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT2x4, columns = 2, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 ⎤
     * ⎢ m10 m11 ⎥
     * ⎢ m20 m21 ⎥
     * ⎣ m30 m31 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double,
        m10: Double, m11: Double,
        m20: Double, m21: Double,
        m30: Double, m31: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01
        this[1, 0] = m10; this[1, 1] = m11
        this[2, 0] = m20; this[2, 1] = m21
        this[3, 0] = m30; this[3, 1] = m31
    }

    override fun push() {
        GL40.glUniformMatrix2x4dv(location, true, values)
    }
}

public class DoubleMat2x4ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT2x4, length, columns = 2, rows = 4) {

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
        m00: Double, m01: Double,
        m10: Double, m11: Double,
        m20: Double, m21: Double,
        m30: Double, m31: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01
        this[index, 1, 0] = m10; this[index, 1, 1] = m11
        this[index, 2, 0] = m20; this[index, 2, 1] = m21
        this[index, 3, 0] = m30; this[index, 3, 1] = m31
    }

    override fun push() {
        GL40.glUniformMatrix2x4dv(location, true, values)
    }
}

public class DoubleMat3x2Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT3x2, columns = 3, rows = 2) {
    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎣ m10 m11 m12 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02;
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12;
    }

    override fun push() {
        GL40.glUniformMatrix3x2dv(location, true, values)
    }
}

public class DoubleMat3x2ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT3x2, length, columns = 3, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎣ m30 m31 m32 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12
    }
    override fun push() {
        GL40.glUniformMatrix3x2dv(location, true, values)
    }
}

public class DoubleMat3x4Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT3x4, columns = 3, rows = 4) {

    /**
     * ```
     * ⎡ m00 m01 m02 ⎤
     * ⎢ m10 m11 m12 ⎥
     * ⎢ m20 m21 m22 ⎥
     * ⎣ m30 m31 m32 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
        m20: Double, m21: Double, m22: Double,
        m30: Double, m31: Double, m32: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22
        this[3, 0] = m30; this[3, 1] = m31; this[3, 2] = m32
    }

    override fun push() {
        GL40.glUniformMatrix3x4dv(location, true, values)
    }
}

public class DoubleMat3x4ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT3x4, length, columns = 3, rows = 4) {

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
        m00: Double, m01: Double, m02: Double,
        m10: Double, m11: Double, m12: Double,
        m20: Double, m21: Double, m22: Double,
        m30: Double, m31: Double, m32: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22
        this[index, 3, 0] = m30; this[index, 3, 1] = m31; this[index, 3, 2] = m32
    }

    override fun push() {
        GL40.glUniformMatrix3x4dv(location, true, values)
    }
}

public class DoubleMat4x2Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT4x2, columns = 4, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎣ m10 m11 m12 m13 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02; this[0, 3] = m03
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12; this[1, 3] = m13
    }

    override fun push() {
        GL40.glUniformMatrix4x2dv(location, true, values)
    }
}

public class DoubleMat4x2ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT4x2, length, columns = 4, rows = 2) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎣ m10 m11 m12 m13 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02; this[index, 0, 3] = m03
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12; this[index, 1, 3] = m13
    }

    override fun push() {
        GL40.glUniformMatrix4x2dv(location, true, values)
    }
}

public class DoubleMat4x3Uniform(name: String) : DoubleMatrixUniform(name, GL40.GL_FLOAT_MAT4x3, columns = 4, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎣ m20 m21 m22 m23 ⎦
     * ```
     */
    public fun set(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
    ) {
        this[0, 0] = m00; this[0, 1] = m01; this[0, 2] = m02; this[0, 3] = m03
        this[1, 0] = m10; this[1, 1] = m11; this[1, 2] = m12; this[1, 3] = m13
        this[2, 0] = m20; this[2, 1] = m21; this[2, 2] = m22; this[2, 3] = m23
    }

    override fun push() {
        GL40.glUniformMatrix4x3dv(location, true, values)
    }
}

public class DoubleMat4x3ArrayUniform(name: String, length: Int) : DoubleMatrixArrayUniform(name, GL40.GL_FLOAT_MAT4x3, length, columns = 4, rows = 3) {

    /**
     * ```
     * ⎡ m00 m01 m02 m03 ⎤
     * ⎢ m10 m11 m12 m13 ⎥
     * ⎣ m20 m21 m22 m23 ⎦
     * ```
     */
    public fun set(
        index: Int,
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
    ) {
        this[index, 0, 0] = m00; this[index, 0, 1] = m01; this[index, 0, 2] = m02; this[index, 0, 3] = m03
        this[index, 1, 0] = m10; this[index, 1, 1] = m11; this[index, 1, 2] = m12; this[index, 1, 3] = m13
        this[index, 2, 0] = m20; this[index, 2, 1] = m21; this[index, 2, 2] = m22; this[index, 2, 3] = m23
    }

    override fun push() {
        GL40.glUniformMatrix4x3dv(location, true, values)
    }
}
