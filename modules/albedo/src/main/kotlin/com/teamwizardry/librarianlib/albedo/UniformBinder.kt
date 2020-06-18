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
        logger.debug("Binding uniforms for shader ${shader.shaderName}")
        val uniformInfos = getUniformInfos(program)
        val bindPoints = scanUniforms(shader)
        val glNames = uniformInfos.keys.sorted()
        val bindNames = bindPoints.keys.sorted()

        bindPoints.forEach { (name, uniform) ->
            val uniformInfo = uniformInfos[name] ?: return@forEach
            uniform.location = uniformInfo.location
            if (uniform is GLSL.GLSLArray)
                uniform.trueLength = uniformInfo.size
        }

        val missingNames = bindNames - glNames
        val unboundNames = glNames - bindNames
        val boundNames = glNames.intersect(bindNames)

        if(missingNames.isNotEmpty()) {
            logger.warn("${missingNames.size} uniforms are missing for ${shader.shaderName}: " +
                "The shader was missing these names:  [${missingNames.sorted().joinToString(", ")}]\n" +
                "OpenGL reported these uniform names: [${glNames.sorted().joinToString(", ")}]\n" +
                "The missing uniforms may have the wrong name, not exist at all, or if they were unused they may " +
                "have been optimized away by the graphics driver. This will not crash, however the missing uniforms " +
                "will be silently ignored, which may produce unexpected behavior."
            )
        }

        if(unboundNames.isNotEmpty()) {
            logger.warn("${missingNames.size} uniforms were never bound for ${shader.shaderName}: " +
                "These names were never bound: [${missingNames.sorted().joinToString(", ")}]\n" +
                "OpenGL reported these names:  [${glNames.sorted().joinToString(", ")}]\n" +
                "This will not cause a crash, however it's an indicator of a mismatched glsl shader and Albedo Shader" +
                "implementation"
            )
        }
        logger.debug("Successfully bound ${boundNames.size} uniforms")
        return bindPoints.values.filter { it.location != -1 }
    }

    private fun getUniformInfos(program: Int): Map<String, UniformInfo> {
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
        return uniformInfos
    }

    private fun scanUniforms(target: Any): Map<String, GLSL> {
        val out = mutableMapOf<String, GLSL>()
        Mirror.reflectClass(target.javaClass).fields.forEach { field ->
            when (val uniform = field.get<Any>(if(field.isStatic) null else target)) {
                is GLSL -> {
                    val glName = field.name + if(uniform is GLSL.GLSLArray) "[0]" else ""
                    out[glName] = uniform
                }
                is GLSLStruct -> {
                    val scanResult = scanUniforms(uniform)
                    scanResult.forEach { (name, glsl) ->
                        out["${field.name}.$name"] = glsl
                    }
                }
                is GLSLStructArray<*> -> {
                    for(i in 0 until uniform.length) {
                        val scanResult = scanUniforms(uniform[i])
                        scanResult.forEach { (name, glsl) ->
                            out["${field.name}[$i].$name"] = glsl
                        }
                    }
                }
            }
        }
        return out
    }

    private data class UniformInfo(val name: String, val type: Int, val size: Int, val location: Int)
}