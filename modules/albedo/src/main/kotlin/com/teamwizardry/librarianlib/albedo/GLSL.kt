package com.teamwizardry.librarianlib.albedo

import com.teamwizardry.librarianlib.core.bridge.IMatrix3f
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.renderer.Matrix3f
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL21
import org.lwjgl.system.MemoryStack
import java.lang.IndexOutOfBoundsException

//todo: improve naming (not GLSL.vec2, maybe Uniform.Vec2)
@Suppress("ClassName", "unused")
public abstract class GLSL(public val glConstant: Int) {
    internal var location: Int = -1
    public open val isArray: Boolean = false

    @JvmSynthetic
    internal abstract fun push()

    public companion object {
        /**
         * Creates a new struct
         */
        @JvmStatic
        public inline fun <reified T: GLSLStruct> struct(): T {
            val constructor = Mirror.reflectClass<T>().getDeclaredConstructor()
            return constructor()
        }

        /**
         * Creates a new struct array with the given length
         */
        @JvmStatic
        public inline fun <reified T: GLSLStruct> struct(length: Int): GLSLStructArray<T> {
            return GLSLStructArray(T::class.java, length)
        }
    }

    /*
    GLSL 1.10 - OpenGL 2.0
        + bool, bvec2, bvec3, bvec4
        + int, ivec2, ivec3, ivec4
        + float, vec2, vec3, vec4
        + mat2, mat3, mat4
        + sampler1D, sampler2D, sampler3D, samplerCube, sampler1DShadow, sampler2DShadow
    GLSL 1.20 - OpenGL 2.1
        + mat2x3, mat2x4, mat3x2, mat3x4, mat4x2, mat4x3
    GLSL 1.30 - OpenGL 3.0
        + uint, uvec2, uvec3, uvec4
        + sampler1DArray, sampler2DArray, sampler1DArrayShadow, sampler2DArrayShadow
        + isampler1D, isampler2D, isampler3D, isamplerCube, isampler1DArray, isampler2DArray
        + usampler1D, usampler2D, usampler3D, usamplerCube, usampler1DArray, usampler2DArray
    GLSL 1.40 - OpenGL 3.1
        + sampler2DRect, sampler2DRectShadow, samplerBuffer
        + isampler2DRect, isamplerBuffer
        + usampler2DRect, usamplerBuffer
    GLSL 1.50 - OpenGL 3.2
        + sampler2DMS, sampler2DMSArray
        + isampler2DMS, isampler2DMSArray
        + usampler2DMS, usampler2DMSArray
    GLSL 4.00 - OpenGL 4.0
        + double, dvec2, devec3, devec4
        + dmat2, dmat3, dmat4, dmat2x3, dmat2x4, dmat3x2, dmat3x4, dmat4x2, dmat4x3
        + samplerCubeShadow, samplerCubeArray, samplerCubeArrayShadow
        + isamplerCubeArray
        + usamplerCubeArray
    GLSL 4.20 - OpenGL 4.2
        + atomic_uint
        + image1D, image2D, image3D, imageCube, image2DRect, image1DArray, image2DArray, imageBuffer, image2DMS, image2DMSArray
        + iimage1D, iimage2D, iimage3D, iimageCube, iimage2DRect, iimage1DArray, iimage2DArray, iimageBuffer, iimage2DMS, iimage2DMSArray
        + uimage1D, uimage2D, uimage3D, uimageCube, uimage2DRect, uimage1DArray, uimage2DArray, uimageBuffer, uimage2DMS, uimage2DMSArray
     */

    public abstract class GLSLArray(glConstant: Int, public val length: Int): GLSL(glConstant) {
        internal var trueLength: Int = length
        override val isArray: Boolean = true
    }

    /**
     * Converts a bool to an int
     */
    protected fun bool(v: Boolean): Int = if (v) 1 else 0

    /**
     * Converts an int to a bool
     */
    protected fun bool(v: Int): Boolean = v != 0

    public class glBool: GLSL(GL20.GL_BOOL) {
        private var value: Boolean = false

        public fun get(): Boolean = value
        public fun set(value: Boolean) {
            this.value = value
        }

        override fun push() {
            GL20.glUniform1i(location, bool(value))
        }

        public class array(length: Int): GLSLArray(GL20.GL_BOOL, length) {
            private val values: IntArray = IntArray(length)

            public operator fun get(index: Int): Boolean = bool(values[index])
            public operator fun set(index: Int, value: Boolean) {
                values[index] = bool(value)
            }

            override fun push() {
                GL20.glUniform1iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class glInt: GLSL(GL20.GL_INT) {
        private var value: Int = 0

        public fun get(): Int = value
        public fun set(value: Int) {
            this.value = value
        }

        override fun push() {
            GL20.glUniform1i(location, value)
        }

        public class array(length: Int): GLSLArray(GL20.GL_INT, length) {
            private val values: IntArray = IntArray(length)

            public operator fun get(index: Int): Int = values[index]
            public operator fun set(index: Int, value: Int) {
                values[index] = value
            }

            override fun push() {
                GL20.glUniform1iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class glFloat: GLSL(GL20.GL_FLOAT) {
        private var value: Float = 0f

        public fun get(): Float = value
        public fun set(value: Float) {
            this.value = value
        }

        override fun push() {
            GL20.glUniform1f(location, value)
        }

        public class array(length: Int): GLSLArray(GL20.GL_FLOAT, length) {
            private val values: FloatArray = FloatArray(length)

            public operator fun get(index: Int): Float = values[index]
            public operator fun set(index: Int, value: Float) {
                values[index] = value
            }

            override fun push() {
                GL20.glUniform1fv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    //region Float vectors ======================================================================================================
    public class vec2: GLSL(GL20.GL_FLOAT_VEC2) {
        public var x: Float = 0f
        public var y: Float = 0f

        public fun get(): Vec2d = vec(x, y)
        public fun set(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        public fun set(value: Vec2d) {
            set(value.xf, value.yf)
        }

        override fun push() {
            GL20.glUniform2f(location, x, y)
        }

        public class array(length: Int): GLSLArray(GL20.GL_FLOAT_VEC2, length) {
            private val values: FloatArray = FloatArray(length * 2)

            public fun getX(index: Int): Float = values[index * 2]
            public fun getY(index: Int): Float = values[index * 2 + 1]
            public fun setX(index: Int, x: Float) {
                values[index * 2] = x
            }

            public fun setY(index: Int, y: Float) {
                values[index * 2 + 1] = y
            }

            public fun set(index: Int, x: Float, y: Float) {
                setX(index, x)
                setY(index, y)
            }

            public operator fun get(index: Int): Vec2d = vec(values[index * 2], values[index * 2 + 1])
            public operator fun set(index: Int, value: Vec2d) {
                set(index, value.xf, value.yf)
            }

            override fun push() {
                GL20.glUniform2fv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class vec3: GLSL(GL20.GL_FLOAT_VEC3) {
        public var x: Float = 0f
        public var y: Float = 0f
        public var z: Float = 0f

        public fun get(): Vec3d = vec(x, y, z)
        public fun set(x: Float, y: Float, z: Float) {
            this.x = x
            this.y = y
            this.z = z
        }

        public fun set(value: Vec3d) {
            set(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
        }

        override fun push() {
            GL20.glUniform3f(location, x, y, z)
        }

        public class array(length: Int): GLSLArray(GL20.GL_FLOAT_VEC3, length) {
            private val values: FloatArray = FloatArray(length * 3)

            public fun getX(index: Int): Float = values[index * 3]
            public fun getY(index: Int): Float = values[index * 3 + 1]
            public fun getZ(index: Int): Float = values[index * 3 + 2]
            public fun setX(index: Int, x: Float) {
                values[index * 3] = x
            }

            public fun setY(index: Int, y: Float) {
                values[index * 3 + 1] = y
            }

            public fun setZ(index: Int, z: Float) {
                values[index * 3 + 2] = z
            }

            public fun set(index: Int, x: Float, y: Float, z: Float) {
                setX(index, x)
                setY(index, y)
                setZ(index, z)
            }

            public operator fun get(index: Int): Vec3d = vec(values[index * 3], values[index * 3 + 1], values[index * 3 + 2])
            public operator fun set(index: Int, value: Vec3d) {
                set(index, value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
            }

            override fun push() {
                GL20.glUniform3fv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class vec4: GLSL(GL20.GL_FLOAT_VEC4) {
        public var x: Float = 0f
        public var y: Float = 0f
        public var z: Float = 0f
        public var w: Float = 0f

        public fun set(x: Float, y: Float, z: Float, w: Float) {
            this.x = x
            this.y = y
            this.z = z
            this.w = w
        }

        override fun push() {
            GL20.glUniform4f(location, x, y, z, w)
        }

        public class array(length: Int): GLSLArray(GL20.GL_FLOAT_VEC4, length) {
            private val values: FloatArray = FloatArray(length * 4)

            public fun getX(index: Int): Float = values[index * 4]
            public fun getY(index: Int): Float = values[index * 4 + 1]
            public fun getZ(index: Int): Float = values[index * 4 + 2]
            public fun getW(index: Int): Float = values[index * 4 + 3]
            public fun setX(index: Int, x: Float) {
                values[index * 4] = x
            }

            public fun setY(index: Int, y: Float) {
                values[index * 4 + 1] = y
            }

            public fun setZ(index: Int, z: Float) {
                values[index * 4 + 2] = z
            }

            public fun setW(index: Int, w: Float) {
                values[index * 4 + 3] = w
            }

            public fun set(index: Int, x: Float, y: Float, z: Float, w: Float) {
                setX(index, x)
                setY(index, y)
                setZ(index, z)
                setW(index, w)
            }

            override fun push() {
                GL20.glUniform4fv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }
    //endregion

    //region Bool vectors =======================================================================================================
    public class bvec2: GLSL(GL20.GL_BOOL_VEC2) {
        public var x: Boolean = false
        public var y: Boolean = false

        public fun set(x: Boolean, y: Boolean) {
            this.x = x
            this.y = y
        }

        override fun push() {
            GL20.glUniform2i(location, bool(x), bool(y))
        }

        public class array(length: Int): GLSLArray(GL20.GL_BOOL_VEC2, length) {
            private val values: IntArray = IntArray(length * 2)

            public fun getX(index: Int): Boolean = bool(values[index * 2])
            public fun getY(index: Int): Boolean = bool(values[index * 2 + 1])
            public fun setX(index: Int, x: Boolean) {
                values[index * 2] = bool(x)
            }

            public fun setY(index: Int, y: Boolean) {
                values[index * 2 + 1] = bool(y)
            }

            public fun set(index: Int, x: Boolean, y: Boolean) {
                setX(index, x)
                setY(index, y)
            }

            override fun push() {
                GL20.glUniform2iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class bvec3: GLSL(GL20.GL_BOOL_VEC3) {
        public var x: Boolean = false
        public var y: Boolean = false
        public var z: Boolean = false

        public fun set(x: Boolean, y: Boolean, z: Boolean) {
            this.x = x
            this.y = y
            this.z = z
        }

        override fun push() {
            GL20.glUniform3i(location, bool(x), bool(y), bool(z))
        }

        public class array(length: Int): GLSLArray(GL20.GL_BOOL_VEC3, length) {
            private val values: IntArray = IntArray(length * 3)

            public fun getX(index: Int): Boolean = bool(values[index * 3])
            public fun getY(index: Int): Boolean = bool(values[index * 3 + 1])
            public fun getZ(index: Int): Boolean = bool(values[index * 3 + 2])
            public fun setX(index: Int, x: Boolean) {
                values[index * 3] = bool(x)
            }

            public fun setY(index: Int, y: Boolean) {
                values[index * 3 + 1] = bool(y)
            }

            public fun setZ(index: Int, z: Boolean) {
                values[index * 3 + 2] = bool(z)
            }

            public fun set(index: Int, x: Boolean, y: Boolean, z: Boolean) {
                setX(index, x)
                setY(index, y)
                setZ(index, z)
            }

            override fun push() {
                GL20.glUniform3iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class bvec4: GLSL(GL20.GL_BOOL_VEC4) {
        public var x: Boolean = false
        public var y: Boolean = false
        public var z: Boolean = false
        public var w: Boolean = false

        public fun set(x: Boolean, y: Boolean, z: Boolean, w: Boolean) {
            this.x = x
            this.y = y
            this.z = z
            this.w = w
        }

        override fun push() {
            GL20.glUniform4i(location, bool(x), bool(y), bool(z), bool(w))
        }

        public class array(length: Int): GLSLArray(GL20.GL_BOOL_VEC4, length) {
            private val values: IntArray = IntArray(length * 4)

            public fun getX(index: Int): Boolean = bool(values[index * 4])
            public fun getY(index: Int): Boolean = bool(values[index * 4 + 1])
            public fun getZ(index: Int): Boolean = bool(values[index * 4 + 2])
            public fun getW(index: Int): Boolean = bool(values[index * 4 + 3])
            public fun setX(index: Int, x: Boolean) {
                values[index * 4] = bool(x)
            }

            public fun setY(index: Int, y: Boolean) {
                values[index * 4 + 1] = bool(y)
            }

            public fun setZ(index: Int, z: Boolean) {
                values[index * 4 + 2] = bool(z)
            }

            public fun setW(index: Int, w: Boolean) {
                values[index * 4 + 3] = bool(w)
            }

            public fun set(index: Int, x: Boolean, y: Boolean, z: Boolean, w: Boolean) {
                setX(index, x)
                setY(index, y)
                setZ(index, z)
                setW(index, w)
            }

            override fun push() {
                GL20.glUniform4iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }
    //endregion

    //region Int vectors ========================================================================================================
    public class ivec2: GLSL(GL20.GL_INT_VEC2) {
        public var x: Int = 0
        public var y: Int = 0

        public fun set(x: Int, y: Int) {
            this.x = x
            this.y = y
        }

        override fun push() {
            GL20.glUniform2i(location, x, y)
        }

        public class array(length: Int): GLSLArray(GL20.GL_INT_VEC2, length) {
            private val values: IntArray = IntArray(length * 2)

            public fun getX(index: Int): Int = values[index * 2]
            public fun getY(index: Int): Int = values[index * 2 + 1]
            public fun setX(index: Int, x: Int) {
                values[index * 2] = x
            }

            public fun setY(index: Int, y: Int) {
                values[index * 2 + 1] = y
            }

            public fun set(index: Int, x: Int, y: Int) {
                setX(index, x)
                setY(index, y)
            }

            override fun push() {
                GL20.glUniform2iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class ivec3: GLSL(GL20.GL_INT_VEC3) {
        public var x: Int = 0
        public var y: Int = 0
        public var z: Int = 0

        public fun set(x: Int, y: Int, z: Int) {
            this.x = x
            this.y = y
            this.z = z
        }

        override fun push() {
            GL20.glUniform3i(location, x, y, z)
        }

        public class array(length: Int): GLSLArray(GL20.GL_INT_VEC3, length) {
            private val values: IntArray = IntArray(length * 3)

            public fun getX(index: Int): Int = values[index * 3]
            public fun getY(index: Int): Int = values[index * 3 + 1]
            public fun getZ(index: Int): Int = values[index * 3 + 2]
            public fun setX(index: Int, x: Int) {
                values[index * 3] = x
            }

            public fun setY(index: Int, y: Int) {
                values[index * 3 + 1] = y
            }

            public fun setZ(index: Int, z: Int) {
                values[index * 3 + 2] = z
            }

            public fun set(index: Int, x: Int, y: Int, z: Int) {
                setX(index, x)
                setY(index, y)
                setZ(index, z)
            }

            override fun push() {
                GL20.glUniform3iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class ivec4: GLSL(GL20.GL_INT_VEC4) {
        public var x: Int = 0
        public var y: Int = 0
        public var z: Int = 0
        public var w: Int = 0

        public fun set(x: Int, y: Int, z: Int, w: Int) {
            this.x = x
            this.y = y
            this.z = z
            this.w = w
        }

        override fun push() {
            GL20.glUniform4i(location, x, y, z, w)
        }

        public class array(length: Int): GLSLArray(GL20.GL_INT_VEC4, length) {
            private val values: IntArray = IntArray(length * 4)

            public fun getX(index: Int): Int = values[index * 4]
            public fun getY(index: Int): Int = values[index * 4 + 1]
            public fun getZ(index: Int): Int = values[index * 4 + 2]
            public fun getW(index: Int): Int = values[index * 4 + 3]
            public fun setX(index: Int, x: Int) {
                values[index * 4] = x
            }

            public fun setY(index: Int, y: Int) {
                values[index * 4 + 1] = y
            }

            public fun setZ(index: Int, z: Int) {
                values[index * 4 + 2] = z
            }

            public fun setW(index: Int, w: Int) {
                values[index * 4 + 3] = w
            }

            public fun set(index: Int, x: Int, y: Int, z: Int, w: Int) {
                setX(index, x)
                setY(index, y)
                setZ(index, z)
                setW(index, w)
            }

            override fun push() {
                GL20.glUniform4iv(location, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }
    //endregion

    //region Matrices ===========================================================================================================
    // Note: the parameters for the `set(m00, m01, m10, m11)` methods are _deliberately_ not split on index boundaries
    // so they don't suggest the incorrect column/row order

    public abstract class GLSLMatrix(glConstant: Int, public val columns: Int, public val rows: Int): GLSL(glConstant) {
        protected var values: FloatArray = FloatArray(columns * rows)

        public operator fun get(column: Int, row: Int): Float {
            checkIndex(column, row)
            return values[column * rows + row]
        }

        public operator fun set(column: Int, row: Int, value: Float) {
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

        public abstract class GLSLMatrixArray(glConstant: Int, length: Int, public val columns: Int, public val rows: Int): GLSLArray(glConstant, length) {
            private val stride = columns * rows
            protected val values: FloatArray = FloatArray(length * stride)

            public operator fun get(index: Int, column: Int, row: Int): Float {
                return values[index * stride + column * rows + row]
            }

            public operator fun set(index: Int, column: Int, row: Int, value: Float) {
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
    }

    //region Square Matrices ====================================================================================================
    public class mat2: GLSLMatrix(GL20.GL_FLOAT_MAT2, 2, 2) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(m00: Float, m01: Float, m10: Float, m11: Float) {
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
            this[column, 0] = value.xf
            this[column, 1] = value.yf
        }

        override fun push() {
            GL20.glUniformMatrix2fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL20.GL_FLOAT_MAT2, length, 2, 2) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int, m00: Float, m01: Float, m10: Float, m11: Float) {
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
                this[index, column, 0] = value.xf
                this[index, column, 1] = value.yf
            }

            override fun push() {
                GL20.glUniformMatrix2fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class mat3 @JvmOverloads constructor(
        /**
         * Whether to transpose this matrix when sending it to the shader. This is generally used when sending
         * transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
         * row-based.
         */
        public val transpose: Boolean = false
    ): GLSLMatrix(GL20.GL_FLOAT_MAT3, 3, 3) {

        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 0, Row 2 */
        public var m02: Float
            get() = this[0, 2]
            set(value) {
                this[0, 2] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 1, Row 2 */
        public var m12: Float
            get() = this[1, 2]
            set(value) {
                this[1, 2] = value
            }

        /** Column 2, Row 0 */
        public var m20: Float
            get() = this[2, 0]
            set(value) {
                this[2, 0] = value
            }

        /** Column 2, Row 1 */
        public var m21: Float
            get() = this[2, 1]
            set(value) {
                this[2, 1] = value
            }

        /** Column 2, Row 2 */
        public var m22: Float
            get() = this[2, 2]
            set(value) {
                this[2, 2] = value
            }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float, m22: Float
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
            this[column, 0] = value.x.toFloat()
            this[column, 1] = value.y.toFloat()
            this[column, 2] = value.z.toFloat()
        }

        public fun set(matrix: Matrix3d) {
            this.set(
                matrix.m00.toFloat(), matrix.m10.toFloat(), matrix.m20.toFloat(),
                matrix.m01.toFloat(), matrix.m11.toFloat(), matrix.m21.toFloat(),
                matrix.m02.toFloat(), matrix.m12.toFloat(), matrix.m22.toFloat()
            )
        }

        public fun set(matrix: Matrix3f) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val imatrix = matrix as IMatrix3f
            this.set(
                imatrix.m00, imatrix.m10, imatrix.m20,
                imatrix.m01, imatrix.m11, imatrix.m21,
                imatrix.m02, imatrix.m12, imatrix.m22
            )
        }

        override fun push() {
            GL20.glUniformMatrix3fv(location, transpose, values)
        }

        public class array @JvmOverloads constructor(
            length: Int,
            /**
             * Whether to transpose this matrix when sending it to the shader. This is generally used when sending
             * transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
             * row-based.
             */
            public val transpose: Boolean = false
        ): GLSLMatrixArray(GL20.GL_FLOAT_MAT3, length, 3, 3) {

            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 0, Row 2 */
            public fun getM02(index: Int): Float = this[index, 0, 2]
            public fun setM02(index: Int, value: Float) {
                this[index, 0, 2] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 1, Row 2 */
            public fun getM12(index: Int): Float = this[index, 1, 2]
            public fun setM12(index: Int, value: Float) {
                this[index, 1, 2] = value
            }

            /** Column 2, Row 0 */
            public fun getM20(index: Int): Float = this[index, 2, 0]
            public fun setM20(index: Int, value: Float) {
                this[index, 2, 0] = value
            }

            /** Column 2, Row 1 */
            public fun getM21(index: Int): Float = this[index, 2, 1]
            public fun setM21(index: Int, value: Float) {
                this[index, 2, 1] = value
            }

            /** Column 2, Row 2 */
            public fun getM22(index: Int): Float = this[index, 2, 2]
            public fun setM22(index: Int, value: Float) {
                this[index, 2, 2] = value
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float,
                m22: Float
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
                this.set(index,
                    matrix.m00.toFloat(), matrix.m10.toFloat(), matrix.m20.toFloat(),
                    matrix.m01.toFloat(), matrix.m11.toFloat(), matrix.m21.toFloat(),
                    matrix.m02.toFloat(), matrix.m12.toFloat(), matrix.m22.toFloat()
                )
            }

            public fun set(index: Int, matrix: Matrix3f) {
                @Suppress("CAST_NEVER_SUCCEEDS")
                val imatrix = matrix as IMatrix3f
                this.set(index,
                    imatrix.m00, imatrix.m10, imatrix.m20,
                    imatrix.m01, imatrix.m11, imatrix.m21,
                    imatrix.m02, imatrix.m12, imatrix.m22
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
                this[index, column, 0] = value.x.toFloat()
                this[index, column, 1] = value.y.toFloat()
                this[index, column, 2] = value.z.toFloat()
            }

            override fun push() {
                GL20.glUniformMatrix3fv(location, transpose, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)

            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int, transpose: Boolean): array = array(length, transpose)
        }
    }

    public class mat4 @JvmOverloads constructor(
        /**
         * Whether to transpose this matrix when sending it to the shader. This is generally used when sending
         * transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
         * row-based.
         */
        public val transpose: Boolean = false
    ): GLSLMatrix(GL20.GL_FLOAT_MAT4, 4, 4) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 0, Row 2 */
        public var m02: Float
            get() = this[0, 2]
            set(value) {
                this[0, 2] = value
            }

        /** Column 0, Row 3 */
        public var m03: Float
            get() = this[0, 3]
            set(value) {
                this[0, 3] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 1, Row 2 */
        public var m12: Float
            get() = this[1, 2]
            set(value) {
                this[1, 2] = value
            }

        /** Column 1, Row 3 */
        public var m13: Float
            get() = this[1, 3]
            set(value) {
                this[1, 3] = value
            }

        /** Column 2, Row 0 */
        public var m20: Float
            get() = this[2, 0]
            set(value) {
                this[2, 0] = value
            }

        /** Column 2, Row 1 */
        public var m21: Float
            get() = this[2, 1]
            set(value) {
                this[2, 1] = value
            }

        /** Column 2, Row 2 */
        public var m22: Float
            get() = this[2, 2]
            set(value) {
                this[2, 2] = value
            }

        /** Column 2, Row 3 */
        public var m23: Float
            get() = this[2, 3]
            set(value) {
                this[2, 3] = value
            }

        /** Column 3, Row 0 */
        public var m30: Float
            get() = this[3, 0]
            set(value) {
                this[3, 0] = value
            }

        /** Column 3, Row 1 */
        public var m31: Float
            get() = this[3, 1]
            set(value) {
                this[3, 1] = value
            }

        /** Column 3, Row 2 */
        public var m32: Float
            get() = this[3, 2]
            set(value) {
                this[3, 2] = value
            }

        /** Column 3, Row 3 */
        public var m33: Float
            get() = this[3, 3]
            set(value) {
                this[3, 3] = value
            }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float, m20: Float,
            m21: Float, m22: Float, m23: Float, m30: Float, m31: Float, m32: Float, m33: Float
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
                matrix.m00.toFloat(), matrix.m10.toFloat(), matrix.m20.toFloat(), matrix.m30.toFloat(),
                matrix.m01.toFloat(), matrix.m11.toFloat(), matrix.m21.toFloat(), matrix.m31.toFloat(),
                matrix.m02.toFloat(), matrix.m12.toFloat(), matrix.m22.toFloat(), matrix.m32.toFloat(),
                matrix.m03.toFloat(), matrix.m13.toFloat(), matrix.m23.toFloat(), matrix.m33.toFloat()
            )
        }

        public fun set(matrix: Matrix4f) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val imatrix = matrix as IMatrix4f
            this.set(
                imatrix.m00, imatrix.m10, imatrix.m20, imatrix.m30,
                imatrix.m01, imatrix.m11, imatrix.m21, imatrix.m31,
                imatrix.m02, imatrix.m12, imatrix.m22, imatrix.m32,
                imatrix.m03, imatrix.m13, imatrix.m23, imatrix.m33
            )
        }

        override fun push() {
            GL20.glUniformMatrix4fv(location, transpose, values)
        }

        public class array @JvmOverloads constructor(
            length: Int,
            /**
             * Whether to transpose this matrix when sending it to the shader. This is generally used when sending
             * transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
             * row-based.
             */
            public val transpose: Boolean = false
        ): GLSLMatrixArray(GL20.GL_FLOAT_MAT4, length, 4, 4) {

            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 0, Row 2 */
            public fun getM02(index: Int): Float = this[index, 0, 2]
            public fun setM02(index: Int, value: Float) {
                this[index, 0, 2] = value
            }

            /** Column 0, Row 3 */
            public fun getM03(index: Int): Float = this[index, 0, 3]
            public fun setM03(index: Int, value: Float) {
                this[index, 0, 3] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 1, Row 2 */
            public fun getM12(index: Int): Float = this[index, 1, 2]
            public fun setM12(index: Int, value: Float) {
                this[index, 1, 2] = value
            }

            /** Column 1, Row 3 */
            public fun getM13(index: Int): Float = this[index, 1, 3]
            public fun setM13(index: Int, value: Float) {
                this[index, 1, 3] = value
            }

            /** Column 2, Row 0 */
            public fun getM20(index: Int): Float = this[index, 2, 0]
            public fun setM20(index: Int, value: Float) {
                this[index, 2, 0] = value
            }

            /** Column 2, Row 1 */
            public fun getM21(index: Int): Float = this[index, 2, 1]
            public fun setM21(index: Int, value: Float) {
                this[index, 2, 1] = value
            }

            /** Column 2, Row 2 */
            public fun getM22(index: Int): Float = this[index, 2, 2]
            public fun setM22(index: Int, value: Float) {
                this[index, 2, 2] = value
            }

            /** Column 2, Row 3 */
            public fun getM23(index: Int): Float = this[index, 2, 3]
            public fun setM23(index: Int, value: Float) {
                this[index, 2, 3] = value
            }

            /** Column 3, Row 0 */
            public fun getM30(index: Int): Float = this[index, 3, 0]
            public fun setM30(index: Int, value: Float) {
                this[index, 3, 0] = value
            }

            /** Column 3, Row 3 */
            public fun getM31(index: Int): Float = this[index, 3, 1]
            public fun setM31(index: Int, value: Float) {
                this[index, 3, 1] = value
            }

            /** Column 3, Row 2 */
            public fun getM32(index: Int): Float = this[index, 3, 2]
            public fun setM32(index: Int, value: Float) {
                this[index, 3, 2] = value
            }

            /** Column 3, Row 3 */
            public fun getM33(index: Int): Float = this[index, 3, 3]
            public fun setM33(index: Int, value: Float) {
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
                this[index, column, 0] = value.x.toFloat()
                this[index, column, 1] = value.y.toFloat()
                this[index, column, 2] = value.z.toFloat()
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float,
                m20: Float, m21: Float, m22: Float, m23: Float, m30: Float, m31: Float, m32: Float, m33: Float
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
                this.set(index,
                    matrix.m00.toFloat(), matrix.m10.toFloat(), matrix.m20.toFloat(), matrix.m30.toFloat(),
                    matrix.m01.toFloat(), matrix.m11.toFloat(), matrix.m21.toFloat(), matrix.m31.toFloat(),
                    matrix.m02.toFloat(), matrix.m12.toFloat(), matrix.m22.toFloat(), matrix.m32.toFloat(),
                    matrix.m03.toFloat(), matrix.m13.toFloat(), matrix.m23.toFloat(), matrix.m33.toFloat()
                )
            }

            public fun set(index: Int, matrix: Matrix4f) {
                @Suppress("CAST_NEVER_SUCCEEDS")
                val imatrix = matrix as IMatrix4f
                this.set(index,
                    imatrix.m00, imatrix.m10, imatrix.m20, imatrix.m30,
                    imatrix.m01, imatrix.m11, imatrix.m21, imatrix.m31,
                    imatrix.m02, imatrix.m12, imatrix.m22, imatrix.m32,
                    imatrix.m03, imatrix.m13, imatrix.m23, imatrix.m33
                )
            }

            override fun push() {
                GL20.glUniformMatrix4fv(location, transpose, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)

            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int, transpose: Boolean): array = array(length, transpose)
        }
    }
    //endregion

    //region Non-square matrices ================================================================================================
    public class mat2x3: GLSLMatrix(GL21.GL_FLOAT_MAT2x3, 2, 3) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 0, Row 2 */
        public var m02: Float
            get() = this[0, 2]
            set(value) {
                this[0, 2] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 1, Row 2 */
        public var m12: Float
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
            this[column, 0] = value.x.toFloat()
            this[column, 1] = value.y.toFloat()
            this[column, 2] = value.z.toFloat()
        }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float
        ) {
            this.m00 = m00
            this.m01 = m01
            this.m02 = m02

            this.m10 = m10
            this.m11 = m11
            this.m12 = m12
        }

        override fun push() {
            GL21.glUniformMatrix2x3fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL21.GL_FLOAT_MAT2x3, length, 2, 3) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 0, Row 2 */
            public fun getM02(index: Int): Float = this[index, 0, 2]
            public fun setM02(index: Int, value: Float) {
                this[index, 0, 2] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 1, Row 2 */
            public fun getM12(index: Int): Float = this[index, 1, 2]
            public fun setM12(index: Int, value: Float) {
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
                this[index, column, 0] = value.x.toFloat()
                this[index, column, 1] = value.y.toFloat()
                this[index, column, 2] = value.z.toFloat()
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float
            ) {
                this[index, 0, 0] = m00
                this[index, 0, 1] = m01
                this[index, 0, 2] = m02

                this[index, 1, 0] = m10
                this[index, 1, 1] = m11
                this[index, 1, 2] = m12
            }

            override fun push() {
                GL21.glUniformMatrix2x3fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class mat2x4: GLSLMatrix(GL21.GL_FLOAT_MAT2x4, 2, 4) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 0, Row 2 */
        public var m02: Float
            get() = this[0, 2]
            set(value) {
                this[0, 2] = value
            }

        /** Column 0, Row 3 */
        public var m03: Float
            get() = this[0, 3]
            set(value) {
                this[0, 3] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 1, Row 2 */
        public var m12: Float
            get() = this[1, 2]
            set(value) {
                this[1, 2] = value
            }

        /** Column 1, Row 3 */
        public var m13: Float
            get() = this[1, 3]
            set(value) {
                this[1, 3] = value
            }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float
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
            GL21.glUniformMatrix2x4fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL21.GL_FLOAT_MAT2x4, length, 2, 4) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 0, Row 2 */
            public fun getM02(index: Int): Float = this[index, 0, 2]
            public fun setM02(index: Int, value: Float) {
                this[index, 0, 2] = value
            }

            /** Column 0, Row 3 */
            public fun getM03(index: Int): Float = this[index, 0, 3]
            public fun setM03(index: Int, value: Float) {
                this[index, 0, 3] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 1, Row 2 */
            public fun getM12(index: Int): Float = this[index, 1, 2]
            public fun setM12(index: Int, value: Float) {
                this[index, 1, 2] = value
            }

            /** Column 1, Row 3 */
            public fun getM13(index: Int): Float = this[index, 1, 3]
            public fun setM13(index: Int, value: Float) {
                this[index, 1, 3] = value
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float
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
                GL21.glUniformMatrix2x4fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class mat3x2: GLSLMatrix(GL21.GL_FLOAT_MAT3x2, 3, 2) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 2, Row 0 */
        public var m20: Float
            get() = this[2, 0]
            set(value) {
                this[2, 0] = value
            }

        /** Column 2, Row 1 */
        public var m21: Float
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
            this[column, 0] = value.xf
            this[column, 1] = value.yf
        }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m10: Float, m11: Float, m20: Float, m21: Float
        ) {
            this.m00 = m00
            this.m01 = m01

            this.m10 = m10
            this.m11 = m11

            this.m20 = m20
            this.m21 = m21
        }

        override fun push() {
            GL21.glUniformMatrix3x2fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL21.GL_FLOAT_MAT3x2, length, 3, 2) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 2, Row 0 */
            public fun getM20(index: Int): Float = this[index, 2, 0]
            public fun setM20(index: Int, value: Float) {
                this[index, 2, 0] = value
            }

            /** Column 2, Row 1 */
            public fun getM21(index: Int): Float = this[index, 2, 1]
            public fun setM21(index: Int, value: Float) {
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
                this[index, column, 0] = value.xf
                this[index, column, 1] = value.yf
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m10: Float, m11: Float, m20: Float, m21: Float
            ) {
                this[index, 0, 0] = m00
                this[index, 0, 1] = m01

                this[index, 1, 0] = m10
                this[index, 1, 1] = m11

                this[index, 2, 0] = m20
                this[index, 2, 1] = m21
            }

            override fun push() {
                GL21.glUniformMatrix3x2fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class mat3x4: GLSLMatrix(GL21.GL_FLOAT_MAT3x4, 3, 4) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 0, Row 2 */
        public var m02: Float
            get() = this[0, 2]
            set(value) {
                this[0, 2] = value
            }

        /** Column 0, Row 3 */
        public var m03: Float
            get() = this[0, 3]
            set(value) {
                this[0, 3] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 1, Row 2 */
        public var m12: Float
            get() = this[1, 2]
            set(value) {
                this[1, 2] = value
            }

        /** Column 1, Row 3 */
        public var m13: Float
            get() = this[1, 3]
            set(value) {
                this[1, 3] = value
            }

        /** Column 2, Row 0 */
        public var m20: Float
            get() = this[2, 0]
            set(value) {
                this[2, 0] = value
            }

        /** Column 2, Row 1 */
        public var m21: Float
            get() = this[2, 1]
            set(value) {
                this[2, 1] = value
            }

        /** Column 2, Row 2 */
        public var m22: Float
            get() = this[2, 2]
            set(value) {
                this[2, 2] = value
            }

        /** Column 2, Row 3 */
        public var m23: Float
            get() = this[2, 3]
            set(value) {
                this[2, 3] = value
            }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float, m20: Float,
            m21: Float, m22: Float, m23: Float
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
            GL21.glUniformMatrix3x4fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL21.GL_FLOAT_MAT3x4, length, 3, 4) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 0, Row 2 */
            public fun getM02(index: Int): Float = this[index, 0, 2]
            public fun setM02(index: Int, value: Float) {
                this[index, 0, 2] = value
            }

            /** Column 0, Row 3 */
            public fun getM03(index: Int): Float = this[index, 0, 3]
            public fun setM03(index: Int, value: Float) {
                this[index, 0, 3] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 1, Row 2 */
            public fun getM12(index: Int): Float = this[index, 1, 2]
            public fun setM12(index: Int, value: Float) {
                this[index, 1, 2] = value
            }

            /** Column 1, Row 3 */
            public fun getM13(index: Int): Float = this[index, 1, 3]
            public fun setM13(index: Int, value: Float) {
                this[index, 1, 3] = value
            }

            /** Column 2, Row 0 */
            public fun getM20(index: Int): Float = this[index, 2, 0]
            public fun setM20(index: Int, value: Float) {
                this[index, 2, 0] = value
            }

            /** Column 2, Row 1 */
            public fun getM21(index: Int): Float = this[index, 2, 1]
            public fun setM21(index: Int, value: Float) {
                this[index, 2, 1] = value
            }

            /** Column 2, Row 2 */
            public fun getM22(index: Int): Float = this[index, 2, 2]
            public fun setM22(index: Int, value: Float) {
                this[index, 2, 2] = value
            }

            /** Column 2, Row 3 */
            public fun getM23(index: Int): Float = this[index, 2, 3]
            public fun setM23(index: Int, value: Float) {
                this[index, 2, 3] = value
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m02: Float, m03: Float, m10: Float, m11: Float, m12: Float, m13: Float,
                m20: Float, m21: Float, m22: Float, m23: Float
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
                GL21.glUniformMatrix3x4fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class mat4x2: GLSLMatrix(GL21.GL_FLOAT_MAT4x2, 4, 2) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 2, Row 0 */
        public var m20: Float
            get() = this[2, 0]
            set(value) {
                this[2, 0] = value
            }

        /** Column 2, Row 1 */
        public var m21: Float
            get() = this[2, 1]
            set(value) {
                this[2, 1] = value
            }

        /** Column 3, Row 0 */
        public var m30: Float
            get() = this[3, 0]
            set(value) {
                this[3, 0] = value
            }

        /** Column 3, Row 1 */
        public var m31: Float
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
            this[column, 0] = value.xf
            this[column, 1] = value.yf
        }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m10: Float, m11: Float, m20: Float, m21: Float, m30: Float, m31: Float
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
            GL21.glUniformMatrix4x2fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL21.GL_FLOAT_MAT4x2, length, 4, 2) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 2, Row 0 */
            public fun getM20(index: Int): Float = this[index, 2, 0]
            public fun setM20(index: Int, value: Float) {
                this[index, 2, 0] = value
            }

            /** Column 2, Row 1 */
            public fun getM21(index: Int): Float = this[index, 2, 1]
            public fun setM21(index: Int, value: Float) {
                this[index, 2, 1] = value
            }

            /** Column 3, Row 0 */
            public fun getM30(index: Int): Float = this[index, 3, 0]
            public fun setM30(index: Int, value: Float) {
                this[index, 3, 0] = value
            }

            /** Column 3, Row 3 */
            public fun getM31(index: Int): Float = this[index, 3, 1]
            public fun setM31(index: Int, value: Float) {
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
                this[index, column, 0] = value.xf
                this[index, column, 1] = value.yf
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m10: Float, m11: Float, m20: Float, m21: Float, m30: Float, m31: Float
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
                GL21.glUniformMatrix4x2fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class mat4x3: GLSLMatrix(GL21.GL_FLOAT_MAT4x3, 4, 3) {
        /** Column 0, Row 0 */
        public var m00: Float
            get() = this[0, 0]
            set(value) {
                this[0, 0] = value
            }

        /** Column 0, Row 1 */
        public var m01: Float
            get() = this[0, 1]
            set(value) {
                this[0, 1] = value
            }

        /** Column 0, Row 2 */
        public var m02: Float
            get() = this[0, 2]
            set(value) {
                this[0, 2] = value
            }

        /** Column 1, Row 0 */
        public var m10: Float
            get() = this[1, 0]
            set(value) {
                this[1, 0] = value
            }

        /** Column 1, Row 1 */
        public var m11: Float
            get() = this[1, 1]
            set(value) {
                this[1, 1] = value
            }

        /** Column 1, Row 2 */
        public var m12: Float
            get() = this[1, 2]
            set(value) {
                this[1, 2] = value
            }

        /** Column 2, Row 0 */
        public var m20: Float
            get() = this[2, 0]
            set(value) {
                this[2, 0] = value
            }

        /** Column 2, Row 1 */
        public var m21: Float
            get() = this[2, 1]
            set(value) {
                this[2, 1] = value
            }

        /** Column 2, Row 2 */
        public var m22: Float
            get() = this[2, 2]
            set(value) {
                this[2, 2] = value
            }

        /** Column 3, Row 0 */
        public var m30: Float
            get() = this[3, 0]
            set(value) {
                this[3, 0] = value
            }

        /** Column 3, Row 1 */
        public var m31: Float
            get() = this[3, 1]
            set(value) {
                this[3, 1] = value
            }

        /** Column 2, Row 2 */
        public var m32: Float
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
            this[column, 0] = value.x.toFloat()
            this[column, 1] = value.y.toFloat()
            this[column, 2] = value.z.toFloat()
        }

        /**
         * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
         */
        public fun set(
            m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float, m22: Float,
            m30: Float, m31: Float, m32: Float
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
            GL21.glUniformMatrix4x3fv(location, false, values)
        }

        public class array(length: Int): GLSLMatrixArray(GL21.GL_FLOAT_MAT4x3, length, 4, 3) {
            /** Column 0, Row 0 */
            public fun getM00(index: Int): Float = this[index, 0, 0]
            public fun setM00(index: Int, value: Float) {
                this[index, 0, 0] = value
            }

            /** Column 0, Row 1 */
            public fun getM01(index: Int): Float = this[index, 0, 1]
            public fun setM01(index: Int, value: Float) {
                this[index, 0, 1] = value
            }

            /** Column 0, Row 2 */
            public fun getM02(index: Int): Float = this[index, 0, 2]
            public fun setM02(index: Int, value: Float) {
                this[index, 0, 2] = value
            }

            /** Column 1, Row 0 */
            public fun getM10(index: Int): Float = this[index, 1, 0]
            public fun setM10(index: Int, value: Float) {
                this[index, 1, 0] = value
            }

            /** Column 1, Row 1 */
            public fun getM11(index: Int): Float = this[index, 1, 1]
            public fun setM11(index: Int, value: Float) {
                this[index, 1, 1] = value
            }

            /** Column 1, Row 2 */
            public fun getM12(index: Int): Float = this[index, 1, 2]
            public fun setM12(index: Int, value: Float) {
                this[index, 1, 2] = value
            }

            /** Column 2, Row 0 */
            public fun getM20(index: Int): Float = this[index, 2, 0]
            public fun setM20(index: Int, value: Float) {
                this[index, 2, 0] = value
            }

            /** Column 2, Row 1 */
            public fun getM21(index: Int): Float = this[index, 2, 1]
            public fun setM21(index: Int, value: Float) {
                this[index, 2, 1] = value
            }

            /** Column 2, Row 2 */
            public fun getM22(index: Int): Float = this[index, 2, 2]
            public fun setM22(index: Int, value: Float) {
                this[index, 2, 2] = value
            }

            /** Column 3, Row 0 */
            public fun getM30(index: Int): Float = this[index, 3, 0]
            public fun setM30(index: Int, value: Float) {
                this[index, 3, 0] = value
            }

            /** Column 3, Row 3 */
            public fun getM31(index: Int): Float = this[index, 3, 1]
            public fun setM31(index: Int, value: Float) {
                this[index, 3, 1] = value
            }

            /** Column 3, Row 2 */
            public fun getM32(index: Int): Float = this[index, 3, 2]
            public fun setM32(index: Int, value: Float) {
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
                this[index, column, 0] = value.x.toFloat()
                this[index, column, 1] = value.y.toFloat()
                this[index, column, 2] = value.z.toFloat()
            }

            /**
             * Column-wise (m01 is column 0, row 1), so parameters are ordered top to bottom then left to right.
             */
            public fun set(index: Int,
                m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float,
                m22: Float, m30: Float, m31: Float, m32: Float
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
                GL21.glUniformMatrix4x3fv(location, false, values)
            }
        }

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }
    //endregion

    //endregion

    //region Samplers ===========================================================================================================

    public abstract class GLSLSampler(glConstant: Int, public val textureTarget: Int): GLSL(glConstant) {
        internal var textureUnit: Int = 0

        private var value: Int = 0

        public fun get(): Int = value
        public fun set(value: Int) {
            this.value = value
        }

        override fun push() {
            GL20.glUniform1i(location, textureUnit)
        }

        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public abstract class GLSLSamplerArray(glConstant: Int, public val textureTarget: Int, length: Int): GLSLArray(glConstant, length) {
            internal var textureUnits: IntArray = IntArray(length)

            private val values: IntArray = IntArray(length)

            public operator fun get(index: Int): Int = values[index]
            public operator fun set(index: Int, value: Int) {
                values[index] = value
            }

            override fun push() {
                MemoryStack.stackPush().use { stack ->
                    val units = stack.mallocInt(trueLength)
                    for (i in 0 until trueLength) {
                        units.put(textureUnits[i])
                    }
                    units.rewind()
                    GL20.glUniform1iv(location, units)
                }
            }
        }
    }

    public class sampler1D: GLSLSampler(GL20.GL_SAMPLER_1D, GL11.GL_TEXTURE_1D) {
        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public class array(length: Int): GLSLSamplerArray(GL20.GL_SAMPLER_1D, GL11.GL_TEXTURE_1D, length)

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class sampler2D: GLSLSampler(GL20.GL_SAMPLER_2D, GL11.GL_TEXTURE_2D) {
        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public class array(length: Int): GLSLSamplerArray(GL20.GL_SAMPLER_2D, GL11.GL_TEXTURE_2D, length)

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class sampler3D: GLSLSampler(GL20.GL_SAMPLER_3D, GL12.GL_TEXTURE_3D) {
        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public class array(length: Int): GLSLSamplerArray(GL20.GL_SAMPLER_3D, GL12.GL_TEXTURE_3D, length)

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class samplerCube: GLSLSampler(GL20.GL_SAMPLER_CUBE, GL13.GL_TEXTURE_CUBE_MAP) {
        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public class array(length: Int): GLSLSamplerArray(GL20.GL_SAMPLER_CUBE, GL13.GL_TEXTURE_CUBE_MAP, length)

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class sampler1DShadow: GLSLSampler(GL20.GL_SAMPLER_1D_SHADOW, GL11.GL_TEXTURE_1D) {
        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public class array(length: Int): GLSLSamplerArray(GL20.GL_SAMPLER_1D_SHADOW, GL11.GL_TEXTURE_1D, length)

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    public class sampler2DShadow: GLSLSampler(GL20.GL_SAMPLER_2D_SHADOW, GL11.GL_TEXTURE_2D) {
        /** NOTE!!! Sampler arrays can *only* be indexed using compile-time literal values. */
        public class array(length: Int): GLSLSamplerArray(GL20.GL_SAMPLER_2D_SHADOW, GL11.GL_TEXTURE_2D, length)

        public companion object {
            /**
             * Easily create an array
             */
            @JvmSynthetic
            public operator fun get(length: Int): array = array(length)
        }
    }

    //endregion

/*
public class vec3(public var x: Float, public var y: Float, public var z: Float): GLSL(GL20.GL_FLOAT_VEC3)
public class vec4(public var x: Float, public var y: Float, public var z: Float, public var w: Float): GLSL(GL20.GL_FLOAT_VEC4)
public class bvec2(public var x: Boolean, public var y: Boolean): GLSL(GL20.GL_BOOL_VEC2)
public class bvec3(public var x: Boolean, public var y: Boolean, public var z: Boolean): GLSL(GL20.GL_BOOL_VEC3)
public class bvec4(public var x: Boolean, public var y: Boolean, public var z: Boolean, public var w: Boolean): GLSL(GL20.GL_BOOL_VEC4)
public class ivec2(public var x: Int, public var y: Int): GLSL(GL20.GL_INT_VEC2)
public class ivec3(public var x: Int, public var y: Int, public var z: Int): GLSL(GL20.GL_INT_VEC3)
public class ivec4(public var x: Int, public var y: Int, public var z: Int, public var w: Int): GLSL(GL20.GL_INT_VEC4)
// mat2x2 = mat2
public class mat2(
    public var m00: Float, public var m10: Float,
    public var m01: Float, public var m11: Float
): GLSL(GL20.GL_FLOAT_MAT2)
// mat3x3 = mat3
public class mat3(
    public var m00: Float, public var m10: Float, public var m20: Float,
    public var m01: Float, public var m11: Float, public var m21: Float,
    public var m02: Float, public var m12: Float, public var m22: Float
): GLSL(GL20.GL_FLOAT_MAT3)
// mat4x4 = mat4
public class mat4(
    public var m00: Float, public var m10: Float, public var m20: Float, public var m30: Float,
    public var m01: Float, public var m11: Float, public var m21: Float, public var m31: Float,
    public var m02: Float, public var m12: Float, public var m22: Float, public var m32: Float,
    public var m03: Float, public var m13: Float, public var m23: Float, public var m33: Float
): GLSL(GL20.GL_FLOAT_MAT4)
public class mat2x3(
    public var m00: Float, public var m10: Float,
    public var m01: Float, public var m11: Float,
    public var m02: Float, public var m12: Float
): GLSL(GL21.GL_FLOAT_MAT2x3)
public class mat2x4(
    public var m00: Float, public var m10: Float,
    public var m01: Float, public var m11: Float,
    public var m02: Float, public var m12: Float,
    public var m03: Float, public var m13: Float
): GLSL(GL21.GL_FLOAT_MAT2x4)
public class mat3x2(
    public var m00: Float, public var m10: Float, public var m20: Float,
    public var m01: Float, public var m11: Float, public var m21: Float
): GLSL(GL21.GL_FLOAT_MAT3x2)
public class mat3x4(
    public var m00: Float, public var m10: Float, public var m20: Float,
    public var m01: Float, public var m11: Float, public var m21: Float,
    public var m02: Float, public var m12: Float, public var m22: Float,
    public var m03: Float, public var m13: Float, public var m23: Float
): GLSL(GL21.GL_FLOAT_MAT3x4)
public class mat4x2(
    public var m00: Float, public var m10: Float, public var m20: Float, public var m30: Float,
    public var m01: Float, public var m11: Float, public var m21: Float, public var m31: Float
): GLSL(GL21.GL_FLOAT_MAT4x2)
public class mat4x3(
    public var m00: Float, public var m10: Float, public var m20: Float, public var m30: Float,
    public var m01: Float, public var m11: Float, public var m21: Float, public var m31: Float,
    public var m02: Float, public var m12: Float, public var m22: Float, public var m32: Float
): GLSL(GL21.GL_FLOAT_MAT4x3)
abstract public class Opaque internal constructor(glConstant: Int): GLSL(glConstant) {
    abstract public var glHandle: Int
}
public class sampler1D(override public var glHandle: Int): Opaque(GL20.GL_SAMPLER_1D)
public class sampler2D(override public var glHandle: Int): Opaque(GL20.GL_SAMPLER_2D)
public class sampler3D(override public var glHandle: Int): Opaque(GL20.GL_SAMPLER_3D)
public class samplerCube(override public var glHandle: Int): Opaque(GL20.GL_SAMPLER_CUBE)
public class sampler1DShadow(override public var glHandle: Int): Opaque(GL20.GL_SAMPLER_1D_SHADOW)
public class sampler2DShadow(override public var glHandle: Int): Opaque(GL20.GL_SAMPLER_2D_SHADOW)
 */
/*
GL20.GL_BOOL, it is 35670 (0x8b56)
GL20.GL_BOOL_VEC2, it is 35671 (0x8b57)
GL20.GL_BOOL_VEC3, it is 35672 (0x8b58)
GL20.GL_BOOL_VEC4, it is 35673 (0x8b59)
GL11.GL_INT, it is 5124 (0x1404)
GL20.GL_INT_VEC2, it is 35667 (0x8b53)
GL20.GL_INT_VEC3, it is 35668 (0x8b54)
GL20.GL_INT_VEC4, it is 35669 (0x8b55)
GL11.GL_UNSIGNED_INT, it is 5125 (0x1405)
GL30.GL_UNSIGNED_INT_VEC2, it is 36294 (0x8dc6)
GL30.GL_UNSIGNED_INT_VEC3, it is 36295 (0x8dc7)
GL30.GL_UNSIGNED_INT_VEC4, it is 36296 (0x8dc8)
GL11.GL_FLOAT, it is 5126 (0x1406)
GL20.GL_FLOAT_VEC2, it is 35664 (0x8b50)
GL20.GL_FLOAT_VEC3, it is 35665 (0x8b51)
GL20.GL_FLOAT_VEC4, it is 35666 (0x8b52)
GL20.GL_FLOAT_MAT2, it is 35674 (0x8b5a)
GL20.GL_FLOAT_MAT3, it is 35675 (0x8b5b)
GL20.GL_FLOAT_MAT4, it is 35676 (0x8b5c)
GL21.GL_FLOAT_MAT2x3, it is 35685 (0x8b65)
GL21.GL_FLOAT_MAT2x4, it is 35686 (0x8b66)
GL21.GL_FLOAT_MAT3x2, it is 35687 (0x8b67)
GL21.GL_FLOAT_MAT3x4, it is 35688 (0x8b68)
GL21.GL_FLOAT_MAT4x2, it is 35689 (0x8b69)
GL21.GL_FLOAT_MAT4x3, it is 35690 (0x8b6a)
GL20.GL_SAMPLER_1D, it is 35677 (0x8b5d)
GL20.GL_SAMPLER_1D_SHADOW, it is 35681 (0x8b61)
GL30.GL_SAMPLER_1D_ARRAY, it is 36288 (0x8dc0)
GL30.GL_SAMPLER_1D_ARRAY_SHADOW, it is 36291 (0x8dc3)
GL20.GL_SAMPLER_2D, it is 35678 (0x8b5e)
GL20.GL_SAMPLER_2D_SHADOW, it is 35682 (0x8b62)
GL30.GL_SAMPLER_2D_ARRAY, it is 36289 (0x8dc1)
GL30.GL_SAMPLER_2D_ARRAY_SHADOW, it is 36292 (0x8dc4)
GL32.GL_SAMPLER_2D_MULTISAMPLE, it is 37128 (0x9108)
GL32.GL_SAMPLER_2D_MULTISAMPLE_ARRAY, it is 37131 (0x910b)
GL20.GL_SAMPLER_3D, it is 35679 (0x8b5f)
GL20.GL_SAMPLER_CUBE, it is 35680 (0x8b60)
GL30.GL_SAMPLER_CUBE_SHADOW, it is 36293 (0x8dc5)
GL30.GL_SAMPLER_BUFFER, it is 36290 (0x8dc2)
GL31.GL_SAMPLER_2D_RECT, it is 35683 (0x8b63)
GL31.GL_SAMPLER_2D_RECT_SHADOW, it is 35684 (0x8b64)
GL30.GL_INT_SAMPLER_1D, it is 36297 (0x8dc9)
GL30.GL_INT_SAMPLER_1D_ARRAY, it is 36302 (0x8dce)
GL30.GL_INT_SAMPLER_2D, it is 36298 (0x8dca)
GL30.GL_INT_SAMPLER_2D_ARRAY, it is 36303 (0x8dcf)
GL32.GL_INT_SAMPLER_2D_MULTISAMPLE, it is 37129 (0x9109)
GL32.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, it is 37132 (0x910c)
GL30.GL_INT_SAMPLER_3D, it is 36299 (0x8dcb)
GL30.GL_INT_SAMPLER_CUBE, it is 36300 (0x8dcc)
GL30.GL_INT_SAMPLER_BUFFER, it is 36304 (0x8dd0)
GL30.GL_INT_SAMPLER_2D_RECT, it is 36301 (0x8dcd)
GL30.GL_UNSIGNED_INT_SAMPLER_1D, it is 36305 (0x8dd1)
GL30.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, it is 36310 (0x8dd6)
GL30.GL_UNSIGNED_INT_SAMPLER_2D, it is 36306 (0x8dd2)
GL30.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, it is 36311 (0x8dd7)
GL32.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, it is 37130 (0x910a)
GL32.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, it is 37133 (0x910d)
GL30.GL_UNSIGNED_INT_SAMPLER_3D, it is 36307 (0x8dd3)
GL30.GL_UNSIGNED_INT_SAMPLER_CUBE, it is 36308 (0x8dd4)
GL30.GL_UNSIGNED_INT_SAMPLER_BUFFER, it is 36312 (0x8dd8)
GL30.GL_UNSIGNED_INT_SAMPLER_2D_RECT, it is 36309 (0x8dd5)
 */
}