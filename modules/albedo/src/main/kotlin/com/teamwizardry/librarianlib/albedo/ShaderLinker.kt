package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.uniform.*
import net.minecraft.client.gl.GlUniform
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

public object ShaderLinker {

    public fun linkUniforms(
        uniforms: List<AbstractUniform>,
        shader: Shader
    ): List<Uniform> {
        logger.debug("Linking uniforms [${uniforms.joinToString { it.name }}] against shader ${shader.shaderName}")
        val uniformInfos = shader.uniforms.associateBy { it.name }
        val resolvedUniforms = resolveUniformNames(uniforms)
        val glNames = uniformInfos.keys.sorted()
        val uniformNames = resolvedUniforms.keys.sorted()

        for((name, uniform) in resolvedUniforms) {
            val uniformInfo = uniformInfos[name] ?: continue
            uniform.location = uniformInfo.location
            if (uniform is ArrayUniform)
                uniform.trueLength = uniformInfo.size
        }

        val missingNames = uniformNames - glNames
        val unboundNames = glNames - uniformNames
        val boundNames = glNames.intersect(uniformNames)

        if (missingNames.isNotEmpty()) {
            logger.warn(
                "${missingNames.size} uniforms are missing for ${shader.shaderName}: \n" +
                        "The shader was missing these names:  [${missingNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these uniform names: [${glNames.sorted().joinToString(", ")}]\n" +
                        "The missing uniforms may have the wrong name, not exist at all, or if they were unused they " +
                        "may have been optimized away by the graphics driver. This will not crash, however the " +
                        "missing uniforms will be silently ignored, which may produce unexpected behavior."
            )
        }

        if (unboundNames.isNotEmpty()) {
            logger.warn(
                "${missingNames.size} uniforms were never bound for ${shader.shaderName}: \n" +
                        "These names were never bound: [${missingNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these names:  [${glNames.sorted().joinToString(", ")}]\n" +
                        "This will not cause a crash, however it's an indicator of a mismatched glsl shader and " +
                        "shader binding."
            )
        }

        logger.debug("Successfully bound ${boundNames.size} uniforms")
        return boundNames.map { resolvedUniforms.getValue(it) }
    }

    public fun linkAttributes(
        attributes: List<VertexLayoutElement>,
        shader: Shader
    ) {
        logger.debug("Linking attributes [${attributes.joinToString { it.name }}] against shader ${shader.shaderName}")
        val attributeInfos = shader.attributes.associateBy { it.name }
        val glNames = attributeInfos.keys.sorted()
        val attributeNames = attributes.map { it.name }.sorted()

        for(attribute in attributes) {
            val attributeInfo = attributeInfos[attribute.name] ?: continue
            attribute.index = attributeInfo.index
        }

        val missingNames = attributeNames - glNames
        val unboundNames = glNames - attributeNames
        val boundNames = glNames.intersect(attributeNames)

        if (missingNames.isNotEmpty()) {
            throw ShaderLinkerException(
                "${missingNames.size} attributes are missing for ${shader.shaderName}: \n" +
                        "The shader was missing these names:  [${missingNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these attribute names: [${glNames.sorted().joinToString(", ")}]"
            )
        }

        if (unboundNames.isNotEmpty()) {
            throw ShaderLinkerException(
                "${missingNames.size} attributes were never bound for ${shader.shaderName}: \n" +
                        "These names were never bound: [${missingNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these names:  [${glNames.sorted().joinToString(", ")}]"
            )
        }

        logger.debug("Successfully bound ${boundNames.size} attributes")
    }



    private fun resolveUniformNames(uniforms: List<AbstractUniform>): Map<String, Uniform> {
        val out = mutableMapOf<String, Uniform>()
        for (uniform in uniforms) {
            when (uniform) {
                is Uniform -> {
                    val glName = uniform.name + if (uniform is ArrayUniform) "[0]" else ""
                    out[glName] = uniform
                }
                is GLSLStruct -> {
                    val resolved = resolveUniformNames(uniform.fields)
                    resolved.forEach { (name, glsl) ->
                        out["${uniform.name}.$name"] = glsl
                    }
                }
                is GLSLStructArray<*> -> {
                    for (i in 0 until uniform.length) {
                        val scanResult = resolveUniformNames(uniform[i].fields)
                        scanResult.forEach { (name, glsl) ->
                            out["${uniform.name}[$i].$name"] = glsl
                        }
                    }
                }
            }
        }
        return out
    }

    private val logger = LibLibAlbedo.makeLogger<ShaderLinker>()
}