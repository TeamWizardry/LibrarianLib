package com.teamwizardry.librarianlib.albedo.uniform

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror

public sealed class UniformType {}

public class SimpleUniformType<T : Uniform, A : ArrayUniform>(
    private val type: Class<T>,
    private val arrayType: Class<A>
) : UniformType() {
    private val constructor: ConstructorMirror =
        Mirror.reflectClass(type).getDeclaredConstructor(Mirror.reflect<String>())
    private val arrayConstructor: ConstructorMirror =
        Mirror.reflectClass(arrayType).getDeclaredConstructor(Mirror.reflect<String>(), Mirror.types.int)

    public fun create(name: String): T {
        return constructor(name)
    }

    /**
     * @param length The length of the array
     */
    public fun createArray(name: String, length: Int): A {
        return arrayConstructor(name, length)
    }
}

public class SamplerUniformType(private val glConstant: Int, private val textureTarget: Int) : UniformType() {
    public fun create(name: String): SamplerUniform {
        return SamplerUniform(name, glConstant, textureTarget)
    }

    /**
     * NOTE!!! In GLSL code, sampler arrays can *only* be indexed using compile-time literal values.
     *
     * @param length The length of the array
     */
    public fun createArray(name: String, length: Int): SamplerArrayUniform {
        return SamplerArrayUniform(name, glConstant, textureTarget, length)
    }
}

public class MatrixUniformType<T : Uniform, A : ArrayUniform>(
    private val type: Class<T>,
    private val arrayType: Class<A>
) : UniformType() {
    private val constructor: ConstructorMirror =
        Mirror.reflectClass(type).getDeclaredConstructor(Mirror.reflect<String>(), Mirror.types.boolean)
    private val arrayConstructor: ConstructorMirror = Mirror.reflectClass(arrayType)
        .getDeclaredConstructor(Mirror.reflect<String>(), Mirror.types.boolean, Mirror.types.int)

    /**
     * @param transpose Whether to transpose this matrix when sending it to the shader. This is generally used when
     * sending transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
     * row-based.
     */
    public fun create(name: String, transpose: Boolean): T {
        return constructor(name, transpose)
    }

    /**
     * @param transpose Whether to transpose this matrix when sending it to the shader. This is generally used when
     * sending transform matrices to the shader, since OpenGL's transform matrices are column-based as opposed to
     * row-based.
     * @param length The length of the array
     */
    public fun createArray(name: String, transpose: Boolean, length: Int): A {
        return arrayConstructor(name, transpose, length)
    }
}

public object StructUniformType : UniformType() {
    public fun <T : GLSLStruct> create(type: Class<T>, name: String): T {
        return Mirror.reflectClass(type).getDeclaredConstructor(Mirror.reflect<String>()).invoke(name)
    }

    public fun <T : GLSLStruct> createArray(type: Class<T>, name: String, length: Int): GLSLStructArray<T> {
        val constructor = Mirror.reflectClass(type).getDeclaredConstructor(Mirror.reflect<String>())
        return GLSLStructArray(name, length) { constructor("$it") }
    }

    @JvmSynthetic
    public inline fun <reified T : GLSLStruct> create(name: String): T {
        return Mirror.reflectClass<T>().getDeclaredConstructor(Mirror.reflect<String>()).invoke(name)
    }

    @JvmSynthetic
    public inline fun <reified T : GLSLStruct> createArray(name: String, length: Int): GLSLStructArray<T> {
        val constructor = Mirror.reflectClass<T>().getDeclaredConstructor()
        return GLSLStructArray(name, length) { constructor("$it") }
    }
}

public object UnsupportedUniformType : UniformType()
