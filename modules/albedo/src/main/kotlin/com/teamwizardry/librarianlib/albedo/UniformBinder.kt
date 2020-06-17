package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import dev.thecodewarrior.mirror.Mirror
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

internal object UniformBinder {
    fun unbindAllUniforms(target: Shader) {
        Mirror.reflectClass(target.javaClass).fields.forEach { field ->
            if(Mirror.reflect<GLSL>().isAssignableFrom(field.type)) {
                val uniform = field.get<GLSL>(if(field.isStatic) null else target)
                uniform.location = -1
                if(uniform is GLSL.GLSLArray) uniform.trueLength = uniform.length
                if(uniform is GLSL.GLSLSampler) uniform.textureUnit = 0
                if(uniform is GLSL.GLSLSampler.GLSLSamplerArray) uniform.textureUnits.fill(0)
            }
            // TODO: struct recursion
        }
    }

    fun bindAllUniforms(shader: Shader, program: Int): List<GLSL> {
        val uniformInfos = mutableMapOf<String, UniformInfo>()
        MemoryStack.stackPush().use { stack ->
            val uniformCount = GlStateManager.getProgram(program, GL20.GL_ACTIVE_UNIFORMS)
            val maxNameLength = GlStateManager.getProgram(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH)

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
                val name = MemoryUtil.memASCII(nameBuffer, nameLength.get())
                val location = GlStateManager.getUniformLocation(program, name)
                uniformInfos[name] = UniformInfo(name, glType.get(), size.get(), location)
            }
        }
        val glslObjects = mutableListOf<GLSL>()
        val missing = mutableListOf<String>()
        Mirror.reflectClass(shader.javaClass).fields.forEach { field ->
            if(Mirror.reflect<GLSL>().isAssignableFrom(field.type)) {
                val uniform = field.get<GLSL>(if(field.isStatic) null else shader)
                glslObjects.add(uniform)
                val glName = field.name + if(uniform is GLSL.GLSLArray) "[0]" else ""
                val uniformInfo = uniformInfos[glName]
                if(uniformInfo == null) {
                    missing.add(glName)
                }
                uniform.location = uniformInfo?.location ?: -1
                if (uniform is GLSL.GLSLArray)
                    uniform.trueLength = uniformInfo?.size ?: uniform.length
            }
            // TODO: struct recursion
        }
        if(missing.isNotEmpty()) {
            logger.warn("Albedo couldn't find ${missing.size} uniforms for ${shader.shaderName}: " +
                "The shader was missing these names:  " +
                "[${missing.sorted().joinToString(", ") { "`$it`" }}]\n" +
                "OpenGL reported these uniform names: " +
                "[${uniformInfos.keys.sorted().joinToString(", ") { "`$it`" }}]\n" +
                "The missing uniforms may have the wrong name, not exist at all, or if they were unused they may " +
                "have been optimized away by the graphics driver. This will not crash, however the missing uniforms " +
                "will be silently ignored, which may produce unexpected behavior."
            )
        }
        return glslObjects
    }

    private data class UniformInfo(val name: String, val type: Int, val size: Int, val location: Int)
}