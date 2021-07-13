package com.teamwizardry.librarianlib.albedo.shader.uniform

import org.lwjgl.opengl.GL41.*

public sealed class AbstractUniform(
    /**
     * The name of the uniform (or field within a struct)
     */
    public val name: String
)

public sealed class Uniform(name: String, public val glConstant: Int): AbstractUniform(name) {
    public var location: Int = -1
        @JvmSynthetic internal set

    @JvmSynthetic
    internal abstract fun push()

    @Suppress("unused")
    public companion object {
        /*
        Full type list:
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

        //region GLSL 1.10 - OpenGL 2.0
        @JvmField
        public val bool: SimpleUniformType<BoolUniform, BoolArrayUniform> = simple()

        @JvmField
        public val bvec2: SimpleUniformType<BoolVec2Uniform, BoolVec2ArrayUniform> = simple()

        @JvmField
        public val bvec3: SimpleUniformType<BoolVec3Uniform, BoolVec3ArrayUniform> = simple()

        @JvmField
        public val bvec4: SimpleUniformType<BoolVec4Uniform, BoolVec4ArrayUniform> = simple()

        @JvmField
        public val glInt: SimpleUniformType<IntUniform, IntArrayUniform> = simple()

        public val int: SimpleUniformType<IntUniform, IntArrayUniform>
            @JvmSynthetic get() = glInt // for kotlin

        @JvmField
        public val ivec2: SimpleUniformType<IntVec2Uniform, IntVec2ArrayUniform> = simple()

        @JvmField
        public val ivec3: SimpleUniformType<IntVec3Uniform, IntVec3ArrayUniform> = simple()

        @JvmField
        public val ivec4: SimpleUniformType<IntVec4Uniform, IntVec4ArrayUniform> = simple()

        @JvmField
        public val glFloat: SimpleUniformType<FloatUniform, FloatArrayUniform> = simple()

        public val float: SimpleUniformType<FloatUniform, FloatArrayUniform>
            @JvmSynthetic get() = glFloat // for kotlin

        @JvmField
        public val vec2: SimpleUniformType<FloatVec2Uniform, FloatVec2ArrayUniform> = simple()

        @JvmField
        public val vec3: SimpleUniformType<FloatVec3Uniform, FloatVec3ArrayUniform> = simple()

        @JvmField
        public val vec4: SimpleUniformType<FloatVec4Uniform, FloatVec4ArrayUniform> = simple()

        @JvmField
        public val mat2: SimpleUniformType<Mat2x2Uniform, Mat2x2ArrayUniform> = simple()

        @JvmField
        public val mat3: SimpleUniformType<Mat3x3Uniform, Mat3x3ArrayUniform> = simple()

        @JvmField
        public val mat4: SimpleUniformType<Mat4x4Uniform, Mat4x4ArrayUniform> = simple()

        @JvmField
        public val sampler1D: SamplerUniformType = sampler(GL_SAMPLER_1D, GL_TEXTURE_1D)

        @JvmField
        public val sampler2D: SamplerUniformType = sampler(GL_SAMPLER_2D, GL_TEXTURE_2D)

        @JvmField
        public val sampler3D: SamplerUniformType = sampler(GL_SAMPLER_3D, GL_TEXTURE_3D)

        @JvmField
        public val samplerCube: SamplerUniformType = sampler(GL_SAMPLER_CUBE, GL_TEXTURE_CUBE_MAP)

        @JvmField
        public val sampler1DShadow: SamplerUniformType = sampler(GL_SAMPLER_1D_SHADOW, GL_TEXTURE_1D)

        @JvmField
        public val sampler2DShadow: SamplerUniformType = sampler(GL_SAMPLER_2D_SHADOW, GL_TEXTURE_2D)
        //endregion

        //region GLSL 1.20 - OpenGL 2.1
        @JvmField
        public val mat2x3: SimpleUniformType<Mat2x3Uniform, Mat2x3ArrayUniform> = simple()

        @JvmField
        public val mat2x4: SimpleUniformType<Mat2x4Uniform, Mat2x4ArrayUniform> = simple()

        @JvmField
        public val mat3x2: SimpleUniformType<Mat3x2Uniform, Mat3x2ArrayUniform> = simple()

        @JvmField
        public val mat3x4: SimpleUniformType<Mat3x4Uniform, Mat3x4ArrayUniform> = simple()

        @JvmField
        public val mat4x2: SimpleUniformType<Mat4x2Uniform, Mat4x2ArrayUniform> = simple()

        @JvmField
        public val mat4x3: SimpleUniformType<Mat4x3Uniform, Mat4x3ArrayUniform> = simple()
        //endregion

        //region GLSL 1.30 - OpenGL 3.0
        @JvmField
        public val uint: SimpleUniformType<UnsignedIntUniform, UnsignedIntArrayUniform> = simple()

        @JvmField
        public val uvec2: SimpleUniformType<UnsignedIntVec2Uniform, UnsignedIntVec2ArrayUniform> = simple()

        @JvmField
        public val uvec3: SimpleUniformType<UnsignedIntVec3Uniform, UnsignedIntVec3ArrayUniform> = simple()

        @JvmField
        public val uvec4: SimpleUniformType<UnsignedIntVec4Uniform, UnsignedIntVec4ArrayUniform> = simple()

        @JvmField
        public val sampler1DArray: SamplerUniformType = sampler(GL_SAMPLER_1D_ARRAY, GL_TEXTURE_1D_ARRAY)

        @JvmField
        public val sampler2DArray: SamplerUniformType = sampler(GL_SAMPLER_2D_ARRAY, GL_TEXTURE_2D_ARRAY)

        @JvmField
        public val sampler1DArrayShadow: SamplerUniformType = sampler(GL_SAMPLER_1D_ARRAY_SHADOW, GL_TEXTURE_1D_ARRAY)

        @JvmField
        public val sampler2DArrayShadow: SamplerUniformType = sampler(GL_SAMPLER_2D_ARRAY_SHADOW, GL_TEXTURE_2D_ARRAY)

        @JvmField
        public val isampler1D: SamplerUniformType = sampler(GL_INT_SAMPLER_1D, GL_TEXTURE_1D)

        @JvmField
        public val isampler2D: SamplerUniformType = sampler(GL_INT_SAMPLER_2D, GL_TEXTURE_2D)

        @JvmField
        public val isampler3D: SamplerUniformType = sampler(GL_INT_SAMPLER_3D, GL_TEXTURE_3D)

        @JvmField
        public val isamplerCube: SamplerUniformType = sampler(GL_INT_SAMPLER_CUBE, GL_TEXTURE_CUBE_MAP)

        @JvmField
        public val isampler1DArray: SamplerUniformType = sampler(GL_INT_SAMPLER_1D, GL_TEXTURE_1D_ARRAY)

        @JvmField
        public val isampler2DArray: SamplerUniformType = sampler(GL_INT_SAMPLER_2D, GL_TEXTURE_2D_ARRAY)

        @JvmField
        public val usampler1D: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_1D, GL_TEXTURE_1D)

        @JvmField
        public val usampler2D: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_2D, GL_TEXTURE_2D)

        @JvmField
        public val usampler3D: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_3D, GL_TEXTURE_3D)

        @JvmField
        public val usamplerCube: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_CUBE, GL_TEXTURE_CUBE_MAP)

        @JvmField
        public val usampler1DArray: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_1D, GL_TEXTURE_1D_ARRAY)

        @JvmField
        public val usampler2DArray: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_2D, GL_TEXTURE_2D_ARRAY)
        //endregion

        //region GLSL 1.40 - OpenGL 3.1
        @JvmField
        public val sampler2DRect: SamplerUniformType = sampler(GL_SAMPLER_2D_RECT, GL_TEXTURE_RECTANGLE)

        @JvmField
        public val sampler2DRectShadow: SamplerUniformType = sampler(GL_SAMPLER_2D_RECT_SHADOW, GL_TEXTURE_RECTANGLE)

        @JvmField
        public val samplerBuffer: SamplerUniformType = sampler(GL_SAMPLER_BUFFER, GL_TEXTURE_BUFFER)

        @JvmField
        public val isampler2DRect: SamplerUniformType = sampler(GL_INT_SAMPLER_2D_RECT, GL_TEXTURE_RECTANGLE)

        @JvmField
        public val isamplerBuffer: SamplerUniformType = sampler(GL_INT_SAMPLER_BUFFER, GL_TEXTURE_BUFFER)

        @JvmField
        public val usampler2DRect: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_2D_RECT, GL_TEXTURE_RECTANGLE)

        @JvmField
        public val usamplerBuffer: SamplerUniformType = sampler(GL_UNSIGNED_INT_SAMPLER_BUFFER, GL_TEXTURE_BUFFER)
        //endregion

        //region GLSL 1.50 - OpenGL 3.2
        @JvmField
        public val sampler2DMS: SamplerUniformType = sampler(GL_SAMPLER_2D_MULTISAMPLE, GL_TEXTURE_2D_MULTISAMPLE)

        @JvmField
        public val sampler2DMSArray: SamplerUniformType =
            sampler(GL_SAMPLER_2D_MULTISAMPLE_ARRAY, GL_TEXTURE_2D_MULTISAMPLE_ARRAY)

        @JvmField
        public val isampler2DMS: SamplerUniformType = sampler(GL_INT_SAMPLER_2D_MULTISAMPLE, GL_TEXTURE_2D_MULTISAMPLE)

        @JvmField
        public val isampler2DMSArray: SamplerUniformType =
            sampler(GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, GL_TEXTURE_2D_MULTISAMPLE_ARRAY)

        @JvmField
        public val usampler2DMS: SamplerUniformType =
            sampler(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, GL_TEXTURE_2D_MULTISAMPLE)

        @JvmField
        public val usampler2DMSArray: SamplerUniformType =
            sampler(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, GL_TEXTURE_2D_MULTISAMPLE_ARRAY)
        //endregion

        //region GLSL 4.00 - OpenGL 4.0
        @JvmField
        public val glDouble: SimpleUniformType<DoubleUniform, DoubleArrayUniform> = simple()

        public val double: SimpleUniformType<DoubleUniform, DoubleArrayUniform>
            @JvmSynthetic get() = glDouble // for kotlin

        @JvmField
        public val dvec2: SimpleUniformType<DoubleVec2Uniform, DoubleVec2ArrayUniform> = simple()

        @JvmField
        public val devec3: SimpleUniformType<DoubleVec3Uniform, DoubleVec3ArrayUniform> = simple()

        @JvmField
        public val devec4: SimpleUniformType<DoubleVec4Uniform, DoubleVec4ArrayUniform> = simple()

        @JvmField
        public val dmat2: SimpleUniformType<DoubleMat2x2Uniform, DoubleMat2x2ArrayUniform> = simple()

        @JvmField
        public val dmat3: SimpleUniformType<DoubleMat3x3Uniform, DoubleMat3x3ArrayUniform> = simple()

        @JvmField
        public val dmat4: SimpleUniformType<DoubleMat4x4Uniform, DoubleMat4x4ArrayUniform> = simple()

        @JvmField
        public val dmat2x3: SimpleUniformType<DoubleMat2x3Uniform, DoubleMat2x3ArrayUniform> = simple()

        @JvmField
        public val dmat2x4: SimpleUniformType<DoubleMat2x4Uniform, DoubleMat2x4ArrayUniform> = simple()

        @JvmField
        public val dmat3x2: SimpleUniformType<DoubleMat3x2Uniform, DoubleMat3x2ArrayUniform> = simple()

        @JvmField
        public val dmat3x4: SimpleUniformType<DoubleMat3x4Uniform, DoubleMat3x4ArrayUniform> = simple()

        @JvmField
        public val dmat4x2: SimpleUniformType<DoubleMat4x2Uniform, DoubleMat4x2ArrayUniform> = simple()

        @JvmField
        public val dmat4x3: SimpleUniformType<DoubleMat4x3Uniform, DoubleMat4x3ArrayUniform> = simple()

        @JvmField
        public val samplerCubeShadow: SamplerUniformType = sampler(GL_SAMPLER_CUBE_SHADOW, GL_TEXTURE_CUBE_MAP)

        @JvmField
        public val samplerCubeArray: SamplerUniformType = sampler(GL_SAMPLER_CUBE_MAP_ARRAY, GL_TEXTURE_CUBE_MAP_ARRAY)

        @JvmField
        public val samplerCubeArrayShadow: SamplerUniformType =
            sampler(GL_SAMPLER_CUBE_MAP_ARRAY_SHADOW, GL_TEXTURE_CUBE_MAP_ARRAY)

        @JvmField
        public val isamplerCubeArray: SamplerUniformType =
            sampler(GL_INT_SAMPLER_CUBE_MAP_ARRAY, GL_TEXTURE_CUBE_MAP_ARRAY)

        @JvmField
        public val usamplerCubeArray: SamplerUniformType =
            sampler(GL_UNSIGNED_INT_SAMPLER_CUBE_MAP_ARRAY, GL_TEXTURE_CUBE_MAP_ARRAY)
        //endregion

        //region GLSL 4.20 - OpenGL 4.2
        @JvmField
        public val atomic_uint: UnsupportedUniformType = unsupported()

        @JvmField
        public val image1D: UnsupportedUniformType = unsupported()

        @JvmField
        public val image2D: UnsupportedUniformType = unsupported()

        @JvmField
        public val image3D: UnsupportedUniformType = unsupported()

        @JvmField
        public val imageCube: UnsupportedUniformType = unsupported()

        @JvmField
        public val image2DRect: UnsupportedUniformType = unsupported()

        @JvmField
        public val image1DArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val image2DArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val imageBuffer: UnsupportedUniformType = unsupported()

        @JvmField
        public val image2DMS: UnsupportedUniformType = unsupported()

        @JvmField
        public val image2DMSArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage1D: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage2D: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage3D: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimageCube: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage2DRect: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage1DArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage2DArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimageBuffer: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage2DMS: UnsupportedUniformType = unsupported()

        @JvmField
        public val iimage2DMSArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage1D: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage2D: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage3D: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimageCube: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage2DRect: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage1DArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage2DArray: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimageBuffer: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage2DMS: UnsupportedUniformType = unsupported()

        @JvmField
        public val uimage2DMSArray: UnsupportedUniformType = unsupported()

        //endregion

        @JvmField
        public val struct: StructUniformType = StructUniformType

        //region helpers
        private inline fun <reified T : Uniform, reified A : ArrayUniform> simple(): SimpleUniformType<T, A> {
            return SimpleUniformType(T::class.java, A::class.java)
        }

        private fun sampler(glConstant: Int, textureTarget: Int): SamplerUniformType {
            return SamplerUniformType(glConstant, textureTarget)
        }

        private fun unsupported(): UnsupportedUniformType {
            return UnsupportedUniformType
        }
        //endregion
    }
}

public sealed class ArrayUniform(name: String, glConstant: Int, public val length: Int) : Uniform(name, glConstant) {
    internal var trueLength: Int = length
}
