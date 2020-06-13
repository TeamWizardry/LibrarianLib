package com.teamwizardry.librarianlib.albedo

import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL21
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryStack

@Suppress("ClassName", "unused")
sealed class GLSL(val glConstant: Int) {
    internal var location: Int = -1
    protected abstract fun push()

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
    // bool, int, uint, and float are special-cased

    class vec2: GLSL(GL20.GL_FLOAT_VEC2) {
        var x: Float = 0f
            private set
        var y: Float = 0f
            private set

        fun set(x: Float, y: Float) {
            this.x = x
            this.y = y
            this.push()
        }

        override fun push() {
            GL20.glUniform2f(location, x, y)
        }
    }
    /*
    class vec3(var x: Float, var y: Float, var z: Float): GLSL(GL20.GL_FLOAT_VEC3)
    class vec4(var x: Float, var y: Float, var z: Float, var w: Float): GLSL(GL20.GL_FLOAT_VEC4)

    class bvec2(var x: Boolean, var y: Boolean): GLSL(GL20.GL_BOOL_VEC2)
    class bvec3(var x: Boolean, var y: Boolean, var z: Boolean): GLSL(GL20.GL_BOOL_VEC3)
    class bvec4(var x: Boolean, var y: Boolean, var z: Boolean, var w: Boolean): GLSL(GL20.GL_BOOL_VEC4)

    class ivec2(var x: Int, var y: Int): GLSL(GL20.GL_INT_VEC2)
    class ivec3(var x: Int, var y: Int, var z: Int): GLSL(GL20.GL_INT_VEC3)
    class ivec4(var x: Int, var y: Int, var z: Int, var w: Int): GLSL(GL20.GL_INT_VEC4)

    class uint(var v: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC2)
    class uvec2(var x: Long, var y: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC2)
    class uvec3(var x: Long, var y: Long, var z: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC3)
    class uvec4(var x: Long, var y: Long, var z: Long, var w: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC4)

    // mat2x2 = mat2
    class mat2(
        var m00: Float, var m10: Float,
        var m01: Float, var m11: Float
    ): GLSL(GL20.GL_FLOAT_MAT2)
    // mat3x3 = mat3
    class mat3(
        var m00: Float, var m10: Float, var m20: Float,
        var m01: Float, var m11: Float, var m21: Float,
        var m02: Float, var m12: Float, var m22: Float
    ): GLSL(GL20.GL_FLOAT_MAT3)
    // mat4x4 = mat4
    class mat4(
        var m00: Float, var m10: Float, var m20: Float, var m30: Float,
        var m01: Float, var m11: Float, var m21: Float, var m31: Float,
        var m02: Float, var m12: Float, var m22: Float, var m32: Float,
        var m03: Float, var m13: Float, var m23: Float, var m33: Float
    ): GLSL(GL20.GL_FLOAT_MAT4)

    class mat2x3(
        var m00: Float, var m10: Float,
        var m01: Float, var m11: Float,
        var m02: Float, var m12: Float
    ): GLSL(GL21.GL_FLOAT_MAT2x3)
    class mat2x4(
        var m00: Float, var m10: Float,
        var m01: Float, var m11: Float,
        var m02: Float, var m12: Float,
        var m03: Float, var m13: Float
    ): GLSL(GL21.GL_FLOAT_MAT2x4)

    class mat3x2(
        var m00: Float, var m10: Float, var m20: Float,
        var m01: Float, var m11: Float, var m21: Float
    ): GLSL(GL21.GL_FLOAT_MAT3x2)
    class mat3x4(
        var m00: Float, var m10: Float, var m20: Float,
        var m01: Float, var m11: Float, var m21: Float,
        var m02: Float, var m12: Float, var m22: Float,
        var m03: Float, var m13: Float, var m23: Float
    ): GLSL(GL21.GL_FLOAT_MAT3x4)

    class mat4x2(
        var m00: Float, var m10: Float, var m20: Float, var m30: Float,
        var m01: Float, var m11: Float, var m21: Float, var m31: Float
    ): GLSL(GL21.GL_FLOAT_MAT4x2)
    class mat4x3(
        var m00: Float, var m10: Float, var m20: Float, var m30: Float,
        var m01: Float, var m11: Float, var m21: Float, var m31: Float,
        var m02: Float, var m12: Float, var m22: Float, var m32: Float
    ): GLSL(GL21.GL_FLOAT_MAT4x3)

    abstract class Opaque internal constructor(glConstant: Int): GLSL(glConstant) {
        abstract var glHandle: Int
    }
    class sampler1D(override var glHandle: Int): Opaque(GL20.GL_SAMPLER_1D)
    class sampler2D(override var glHandle: Int): Opaque(GL20.GL_SAMPLER_2D)
    class sampler3D(override var glHandle: Int): Opaque(GL20.GL_SAMPLER_3D)
    class samplerCube(override var glHandle: Int): Opaque(GL20.GL_SAMPLER_CUBE)
    class sampler1DShadow(override var glHandle: Int): Opaque(GL20.GL_SAMPLER_1D_SHADOW)
    class sampler2DShadow(override var glHandle: Int): Opaque(GL20.GL_SAMPLER_2D_SHADOW)
    class sampler1DArray(override var glHandle: Int): Opaque(GL30.GL_SAMPLER_1D_ARRAY)
    class sampler2DArray(override var glHandle: Int): Opaque(GL30.GL_SAMPLER_2D_ARRAY)
    class sampler1DArrayShadow(override var glHandle: Int): Opaque(GL30.GL_SAMPLER_1D_ARRAY_SHADOW)
    class sampler2DArrayShadow(override var glHandle: Int): Opaque(GL30.GL_SAMPLER_2D_ARRAY_SHADOW)

    class isampler1D(override var glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_1D)
    class isampler2D(override var glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_2D)
    class isampler3D(override var glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_3D)
    class isamplerCube(override var glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_CUBE)
    class isampler1DArray(override var glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_1D_ARRAY)
    class isampler2DArray(override var glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_2D_ARRAY)

    class usampler1D(override var glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_1D)
    class usampler2D(override var glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_2D)
    class usampler3D(override var glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_3D)
    class usamplerCube(override var glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_CUBE)
    class usampler1DArray(override var glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY)
    class usampler2DArray(override var glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY)
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