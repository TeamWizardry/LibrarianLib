package com.teamwizardry.librarianlib.albedo

import dev.thecodewarrior.mirror.Mirror
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

internal object UniformBinder {
    fun unbindAllUniforms(target: Any) {
        Mirror.reflectClass(target.javaClass).fields.forEach { field ->
            if(Mirror.reflect<GLSL>().isAssignableFrom(field.type)) {
                field.get<GLSL>(if(field.isStatic) null else target).location = -1
            }
            // TODO: struct recursion
        }
    }

    fun bindAllUniforms(target: Any, program: Int): List<GLSL> {
        val uniformInfos = mutableMapOf<String, UniformInfo>()
        MemoryStack.stackPush().use { stack ->
            val uniformCount = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORMS)
            val maxNameLength = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH)

            val glType = stack.mallocInt(1)
            val size = stack.mallocInt(1)
            val nameLength = stack.mallocInt(1)
            val nameBuffer = stack.malloc(maxNameLength)
            for(index in 0 until uniformCount) {
                glType.rewind()
                size.rewind()
                nameLength.rewind()
                nameBuffer.rewind()
                GL20.glGetActiveUniform(program, index, nameLength, size, glType, nameBuffer)
                val location = GL20.glGetUniformLocation(program, nameBuffer)
                val name = MemoryUtil.memASCII(nameBuffer, nameLength.get())
                uniformInfos[name] = UniformInfo(name, glType.get(), size.get(), location)
            }
        }
        var textureUnit = 0
        val glslObjects = mutableListOf<GLSL>()
        Mirror.reflectClass(target.javaClass).fields.forEach { field ->
            if(Mirror.reflect<GLSL>().isAssignableFrom(field.type)) {
                val uniform = field.get<GLSL>(if(field.isStatic) null else target)
                glslObjects.add(uniform)
                val uniformInfo = uniformInfos[field.name]
                uniform.location = uniformInfo?.location ?: -1
                when(uniform) {
                    is GLSL.GLSLSampler -> uniform.textureUnit = textureUnit++
                    is GLSL.GLSLArray -> uniform.trueLength = uniformInfo?.size ?: uniform.length
                    is GLSL.GLSLMatrix.GLSLMatrixArray -> uniform.trueLength = uniformInfo?.size ?: uniform.length
                    is GLSL.GLSLSampler.GLSLSamplerArray -> {
                        uniform.textureUnit = textureUnit
                        textureUnit += uniform.trueLength
                    }
                }
            }
            // TODO: struct recursion
        }
        return glslObjects
    }

    private data class UniformInfo(val name: String, val type: Int, val size: Int, val location: Int)
}