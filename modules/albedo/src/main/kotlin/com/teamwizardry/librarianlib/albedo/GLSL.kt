package com.teamwizardry.librarianlib.albedo

import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL21
import org.lwjgl.opengl.GL30

@Suppress("ClassName", "unused")
sealed class GLSL(val glConstant: Int) {
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

    data class vec2(val x: Float, val y: Float): GLSL(GL20.GL_FLOAT_VEC2)
    data class vec3(val x: Float, val y: Float, val z: Float): GLSL(GL20.GL_FLOAT_VEC3)
    data class vec4(val x: Float, val y: Float, val z: Float, val w: Float): GLSL(GL20.GL_FLOAT_VEC4)

    data class bvec2(val x: Boolean, val y: Boolean): GLSL(GL20.GL_BOOL_VEC2)
    data class bvec3(val x: Boolean, val y: Boolean, val z: Boolean): GLSL(GL20.GL_BOOL_VEC3)
    data class bvec4(val x: Boolean, val y: Boolean, val z: Boolean, val w: Boolean): GLSL(GL20.GL_BOOL_VEC4)

    data class ivec2(val x: Int, val y: Int): GLSL(GL20.GL_INT_VEC2)
    data class ivec3(val x: Int, val y: Int, val z: Int): GLSL(GL20.GL_INT_VEC3)
    data class ivec4(val x: Int, val y: Int, val z: Int, val w: Int): GLSL(GL20.GL_INT_VEC4)

    data class uint(val v: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC2)
    data class uvec2(val x: Long, val y: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC2)
    data class uvec3(val x: Long, val y: Long, val z: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC3)
    data class uvec4(val x: Long, val y: Long, val z: Long, val w: Long): GLSL(GL30.GL_UNSIGNED_INT_VEC4)

    // mat2x2 = mat2
    data class mat2(
        val m00: Float, val m10: Float,
        val m01: Float, val m11: Float
    ): GLSL(GL20.GL_FLOAT_MAT2)
    // mat3x3 = mat3
    data class mat3(
        val m00: Float, val m10: Float, val m20: Float,
        val m01: Float, val m11: Float, val m21: Float,
        val m02: Float, val m12: Float, val m22: Float
    ): GLSL(GL20.GL_FLOAT_MAT3)
    // mat4x4 = mat4
    data class mat4(
        val m00: Float, val m10: Float, val m20: Float, val m30: Float,
        val m01: Float, val m11: Float, val m21: Float, val m31: Float,
        val m02: Float, val m12: Float, val m22: Float, val m32: Float,
        val m03: Float, val m13: Float, val m23: Float, val m33: Float
    ): GLSL(GL20.GL_FLOAT_MAT4)

    data class mat2x3(
        val m00: Float, val m10: Float,
        val m01: Float, val m11: Float,
        val m02: Float, val m12: Float
    ): GLSL(GL21.GL_FLOAT_MAT2x3)
    data class mat2x4(
        val m00: Float, val m10: Float,
        val m01: Float, val m11: Float,
        val m02: Float, val m12: Float,
        val m03: Float, val m13: Float
    ): GLSL(GL21.GL_FLOAT_MAT2x4)

    data class mat3x2(
        val m00: Float, val m10: Float, val m20: Float,
        val m01: Float, val m11: Float, val m21: Float
    ): GLSL(GL21.GL_FLOAT_MAT3x2)
    data class mat3x4(
        val m00: Float, val m10: Float, val m20: Float,
        val m01: Float, val m11: Float, val m21: Float,
        val m02: Float, val m12: Float, val m22: Float,
        val m03: Float, val m13: Float, val m23: Float
    ): GLSL(GL21.GL_FLOAT_MAT3x4)

    data class mat4x2(
        val m00: Float, val m10: Float, val m20: Float, val m30: Float,
        val m01: Float, val m11: Float, val m21: Float, val m31: Float
    ): GLSL(GL21.GL_FLOAT_MAT4x2)
    data class mat4x3(
        val m00: Float, val m10: Float, val m20: Float, val m30: Float,
        val m01: Float, val m11: Float, val m21: Float, val m31: Float,
        val m02: Float, val m12: Float, val m22: Float, val m32: Float
    ): GLSL(GL21.GL_FLOAT_MAT4x3)

    abstract class Opaque internal constructor(glConstant: Int): GLSL(glConstant) {
        abstract val glHandle: Int
    }
    data class sampler1D(override val glHandle: Int): Opaque(GL20.GL_SAMPLER_1D)
    data class sampler2D(override val glHandle: Int): Opaque(GL20.GL_SAMPLER_2D)
    data class sampler3D(override val glHandle: Int): Opaque(GL20.GL_SAMPLER_3D)
    data class samplerCube(override val glHandle: Int): Opaque(GL20.GL_SAMPLER_CUBE)
    data class sampler1DShadow(override val glHandle: Int): Opaque(GL20.GL_SAMPLER_1D_SHADOW)
    data class sampler2DShadow(override val glHandle: Int): Opaque(GL20.GL_SAMPLER_2D_SHADOW)
    data class sampler1DArray(override val glHandle: Int): Opaque(GL30.GL_SAMPLER_1D_ARRAY)
    data class sampler2DArray(override val glHandle: Int): Opaque(GL30.GL_SAMPLER_2D_ARRAY)
    data class sampler1DArrayShadow(override val glHandle: Int): Opaque(GL30.GL_SAMPLER_1D_ARRAY_SHADOW)
    data class sampler2DArrayShadow(override val glHandle: Int): Opaque(GL30.GL_SAMPLER_2D_ARRAY_SHADOW)

    data class isampler1D(override val glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_1D)
    data class isampler2D(override val glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_2D)
    data class isampler3D(override val glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_3D)
    data class isamplerCube(override val glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_CUBE)
    data class isampler1DArray(override val glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_1D_ARRAY)
    data class isampler2DArray(override val glHandle: Int): Opaque(GL30.GL_INT_SAMPLER_2D_ARRAY)

    data class usampler1D(override val glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_1D)
    data class usampler2D(override val glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_2D)
    data class usampler3D(override val glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_3D)
    data class usamplerCube(override val glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_CUBE)
    data class usampler1DArray(override val glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY)
    data class usampler2DArray(override val glHandle: Int): Opaque(GL30.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY)

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