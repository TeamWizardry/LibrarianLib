package com.teamwizardry.librarianlib.albedo.uniforms

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.logger
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL21
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer

sealed class Uniform<T>(val program: Int, val location: Int, val type: UniformType) {
    /**
     * The uniform name
     */
    val name: String

    /**
     * The number of array elements in this uniform, or 1 if the uniform is not an array
     */
    val size: Int

    init {
        val (name, size, glType) = MemoryStack.stackPush().use { stack ->
            val maxNameLength = stack.mallocInt(1)
            GL20.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxNameLength)

            val glType = stack.mallocInt(1)
            val size = stack.mallocInt(1)
            val nameLength = stack.mallocInt(1)
            val nameBuffer = stack.malloc(maxNameLength.get())
            GL20.glGetActiveUniform(program, location, nameLength, size, glType, nameBuffer)
            Triple(MemoryUtil.memASCII(nameBuffer, nameLength.get()), size.get(), glType.get())
        }
        this.name = name
        this.size = size
        if(glType != this.type.ordinal) {
            throw IllegalArgumentException("Uniform $name's type is not equal to $type")
        }
    }

    abstract fun set(value: T)

    class None internal constructor(program: Int, location: Int) : Uniform<Any?>(program, location, UniformType.NONE) {
        init {
            logger.warn("[Shader %s] Uniform `%s`'s type, `%s`, hasn't been implemented yet", owner.glProgram, name, type.name)
        }

        override fun set(value: Any?) {
            // nop
        }
    }

    /**
     * When the uniform type isn't supported, generally when the type is only present in newer GLSL versions
     */
    class Unsupported internal constructor(program: Int, location: Int) : Uniform<Any?>(program, location, UniformType.NONE) {
        init {
            logger.warn("[Shader %s] Uniform `%s` has unsupported type `%s`", owner.glProgram, name, type.name)
        }

        override fun set(value: Any?) {
            // nop
        }
    }
}
