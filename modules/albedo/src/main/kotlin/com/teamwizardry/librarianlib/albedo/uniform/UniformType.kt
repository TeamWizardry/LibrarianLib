package com.teamwizardry.librarianlib.albedo.uniform

import com.teamwizardry.librarianlib.albedo.GLSLStruct
import com.teamwizardry.librarianlib.albedo.GLSLStructArray
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror

public sealed class UniformType {}

public class SimpleUniformType<T : Uniform, A : ArrayUniform>(
    private val type: Class<T>,
    private val arrayType: Class<A>
) : UniformType() {
    private val constructor: ConstructorMirror = Mirror.reflectClass(type).getDeclaredConstructor()
    private val arrayConstructor: ConstructorMirror =
        Mirror.reflectClass(arrayType).getDeclaredConstructor(Mirror.types.int)

    public fun create(): T {
        return constructor()
    }

    /**
     * @param length The length of the array
     */
    public fun createArray(length: Int): A {
        return arrayConstructor(length)
    }
}

public class SamplerUniformType(private val glConstant: Int, private val textureTarget: Int) : UniformType() {
    public fun create(): SamplerUniform {
        return SamplerUniform(glConstant, textureTarget)
    }

    /**
     * NOTE!!! In GLSL code, sampler arrays can *only* be indexed using compile-time literal values.
     *
     * @param length The length of the array
     */
    public fun createArray(length: Int): SamplerArrayUniform {
        return SamplerArrayUniform(glConstant, textureTarget, length)
    }
}

public class TransposableMatrixUniformType<T : Uniform, A : ArrayUniform>(
    private val type: Class<T>,
    private val arrayType: Class<A>
) : UniformType() {
    private val constructor: ConstructorMirror =
        Mirror.reflectClass(type).getDeclaredConstructor(Mirror.types.boolean)
    private val arrayConstructor: ConstructorMirror =
        Mirror.reflectClass(arrayType).getDeclaredConstructor(Mirror.types.boolean, Mirror.types.int)

    /**
     * @param transpose Whether to transpose this matrix when sending it to the shader. This is generally used when
     * sending transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
     * row-based.
     */
    public fun create(transpose: Boolean): T {
        return constructor(transpose)
    }

    /**
     * @param transpose Whether to transpose this matrix when sending it to the shader. This is generally used when
     * sending transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
     * row-based.
     * @param length The length of the array
     */
    public fun createArray(transpose: Boolean, length: Int): A {
        return arrayConstructor(transpose, length)
    }
}

public object StructUniformType : UniformType() {
    public fun <T: GLSLStruct> create(type: Class<T>): T {
        return Mirror.reflectClass(type).getDeclaredConstructor().invoke()
    }

    /**
     * @param length The length of the array
     */
    public fun <T: GLSLStruct> createArray(type: Class<T>, length: Int): GLSLStructArray<T> {
        val constructor = Mirror.reflectClass(type).getDeclaredConstructor()
        return GLSLStructArray(length) { constructor() }
    }

    @JvmSynthetic
    public inline fun <reified T: GLSLStruct> create(): T {
        return Mirror.reflectClass<T>().getDeclaredConstructor().invoke()
    }

    /**
     * @param length The length of the array
     */
    @JvmSynthetic
    public inline fun <reified T: GLSLStruct> createArray(length: Int): GLSLStructArray<T> {
        val constructor = Mirror.reflectClass<T>().getDeclaredConstructor()
        return GLSLStructArray(length) { constructor() }
    }
}


public object UnsupportedUniformType : UniformType()
