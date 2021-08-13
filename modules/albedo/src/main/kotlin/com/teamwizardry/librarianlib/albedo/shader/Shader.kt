package com.teamwizardry.librarianlib.albedo.shader

import com.teamwizardry.librarianlib.albedo.LibLibAlbedo
import com.teamwizardry.librarianlib.albedo.ShaderBindingException
import com.teamwizardry.librarianlib.albedo.ShaderCompilationException
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.*
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import org.lwjgl.opengl.GL40.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

public class Shader private constructor(
    /**
     * An arbitrary name used for logging
     */
    public val name: String,
    private val shaders: Map<Stage, ShaderFile>
) {
    /**
     * The OpenGL handle for the shader program
     */
    public var glProgram: Int by GlResourceGc.track(this, 0) { glDeleteProgram(it) }
        private set
    public var uniforms: List<UniformInfo> = emptyList()
        private set
    public var attributes: List<AttributeInfo> = emptyList()
        private set

    init {
        @Suppress("LeakingThis")
        ReloadListener.allShaders.add(this)
        compile(Client.resourceManager)
    }

    public fun delete() {
        glDeleteProgram(glProgram)
        glProgram = 0
    }

    public fun bindUniforms(uniforms: List<AbstractUniform>): List<Uniform> {
        logger.debug("Binding uniforms [${uniforms.map { it.name }.sorted().joinToString(", ")}] against shader $name")
        val uniformInfos = this.uniforms.associateBy { it.name }
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
                "${missingNames.size} uniforms are missing for $name: \n" +
                        "The shader was missing these names:  [${missingNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these uniform names: [${glNames.sorted().joinToString(", ")}]\n" +
                        "The missing uniforms may have the wrong name, not exist at all, or if they were unused they " +
                        "may have been optimized away by the graphics driver. This will not crash, however the " +
                        "missing uniforms will be silently ignored, which may produce unexpected behavior."
            )
        }

        if (unboundNames.isNotEmpty()) {
            logger.warn(
                "${unboundNames.size} uniforms were never bound for $name: \n" +
                        "These names were never bound: [${unboundNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these names:  [${glNames.sorted().joinToString(", ")}]\n" +
                        "This will not cause a crash, however it's an indicator of a mismatched glsl shader and " +
                        "shader binding."
            )
        }

        logger.debug("Successfully bound ${boundNames.size} uniforms")
        return boundNames.map { resolvedUniforms.getValue(it) }
    }

    public fun bindAttributes(attributes: List<VertexLayoutElement>) {
        logger.debug("Binding attributes [${attributes.map { it.name }.sorted().joinToString(", ")}] against shader $name")
        val attributeInfos = this.attributes.associateBy { it.name }
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
            throw ShaderBindingException(
                "${missingNames.size} attributes are missing for $name: \n" +
                        "The shader was missing these names:  [${missingNames.sorted().joinToString(", ")}]\n" +
                        "OpenGL reported these attribute names: [${glNames.sorted().joinToString(", ")}]"
            )
        }

        if (unboundNames.isNotEmpty()) {
            throw ShaderBindingException(
                "${missingNames.size} attributes were never bound for $name: \n" +
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

    private fun compile(resourceManager: ResourceManager) {
        logger.info("Creating shader program $name:")
        glDeleteProgram(glProgram)
        glProgram = 0
        val shaderHandles = mutableListOf<Int>()
        try {
            for((stage, file) in shaders) {
                shaderHandles.add(
                    ShaderCompiler.compileShader(
                        stage,
                        ShaderCompiler.preprocessShader(file.location, file.defines, resourceManager)
                    )
                )
            }
            glDeleteProgram(glProgram)
            glProgram = linkProgram(shaderHandles)
        } finally {
            if (glProgram != 0) {
                for(handle in shaderHandles) {
                    glDetachShader(glProgram, handle)
                }
            }
            for(handle in shaderHandles) {
                glDeleteShader(handle)
            }
        }
        logger.debug("Finished compiling shader program")
        logger.debug("Reading uniforms...")
        readUniforms()
        logger.debug("Found ${uniforms.size} uniforms: [${uniforms.map { it.name }.sorted().joinToString(", ")}]")
        logger.debug("Reading attributes...")
        readAttributes()
        logger.debug("Found ${attributes.size} attributes: [${attributes.map { it.name }.sorted().joinToString(", ")}]")
    }

    private fun linkProgram(handles: List<Int>): Int {
        logger.debug("Linking shader")
        val program = glCreateProgram()
        if (program == 0)
            throw ShaderCompilationException("could not create program object")

        for(handle in handles) {
            glAttachShader(program, handle)
        }

        glLinkProgram(program)

        val status = glGetProgrami(program, GL_LINK_STATUS)
        if (status == GL_FALSE) {
            val logLength = glGetProgrami(program, GL_INFO_LOG_LENGTH)
            val log = glGetProgramInfoLog(program, logLength)
            glDeleteProgram(program)
            logger.error("Error linking shader")
            throw ShaderCompilationException("Could not link program: $log")
        }

        return program
    }

    private fun readUniforms() {
        val uniforms = mutableListOf<UniformInfo>()
        MemoryStack.stackPush().use { stack ->
            val uniformCount = glGetProgrami(glProgram, GL_ACTIVE_UNIFORMS)
            val maxNameLength = glGetProgrami(glProgram, GL_ACTIVE_UNIFORM_MAX_LENGTH)

            val glType = stack.mallocInt(1)
            val size = stack.mallocInt(1)
            val nameLength = stack.mallocInt(1)
            val nameBuffer = stack.malloc(maxNameLength)
            for (index in 0 until uniformCount) {
                glType.rewind()
                size.rewind()
                nameLength.rewind()
                nameBuffer.rewind()
                glGetActiveUniform(glProgram, index, nameLength, size, glType, nameBuffer)
                val name = MemoryUtil.memASCII(nameBuffer, nameLength.get())
                val location = glGetUniformLocation(glProgram, name)
                uniforms.add(UniformInfo(name, glType.get(), size.get(), location))
            }
        }
        this.uniforms = uniforms.unmodifiableView()
    }

    private fun readAttributes() {
        val attributes = mutableListOf<AttributeInfo>()
        MemoryStack.stackPush().use { stack ->
            val uniformCount = glGetProgrami(glProgram, GL_ACTIVE_ATTRIBUTES)
            val maxNameLength = glGetProgrami(glProgram, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH)

            val glType = stack.mallocInt(1)
            val size = stack.mallocInt(1)
            val nameLength = stack.mallocInt(1)
            val nameBuffer = stack.malloc(maxNameLength)
            for (index in 0 until uniformCount) {
                glType.rewind()
                size.rewind()
                nameLength.rewind()
                nameBuffer.rewind()
                glGetActiveAttrib(glProgram, index, nameLength, size, glType, nameBuffer)
                val name = MemoryUtil.memASCII(nameBuffer, nameLength.get())
                attributes.add(AttributeInfo(name, glType.get(), size.get(), index))
            }
        }
        this.attributes = attributes.unmodifiableView()
    }

    public data class UniformInfo(val name: String, val type: Int, val size: Int, val location: Int)
    public data class AttributeInfo(val name: String, val type: Int, val size: Int, val index: Int)

    public enum class Stage(public val glConstant: Int, public val stageName: String) {
        VERTEX(GL_VERTEX_SHADER, "vertex"),
        TESSELLATION_CONTROL(GL_TESS_CONTROL_SHADER, "tessellation control"),
        TESSELLATION_EVALUATION(GL_TESS_EVALUATION_SHADER, "tessellation evaluation"),
        GEOMETRY(GL_GEOMETRY_SHADER, "geometry"),
        FRAGMENT(GL_FRAGMENT_SHADER, "fragment");

        override fun toString(): String {
            return stageName
        }
    }

    public class Builder(public val name: String) {
        private val stages = mutableMapOf<Stage, ShaderFile>()

        public fun add(stage: Stage, shader: Identifier, vararg defines: String): Builder {
            stages[stage] = ShaderFile(Identifier(shader.namespace, "shaders/${shader.path}"), defines.toList())
            return this
        }

        public fun vertex(shader: Identifier, vararg defines: String): Builder = add(Stage.VERTEX, shader, *defines)
        public fun fragment(shader: Identifier, vararg defines: String): Builder = add(Stage.FRAGMENT, shader, *defines)
        public fun geometry(shader: Identifier, vararg defines: String): Builder = add(Stage.GEOMETRY, shader, *defines)
        public fun tessellationControl(shader: Identifier, vararg defines: String): Builder = add(Stage.TESSELLATION_CONTROL, shader, *defines)
        public fun tessellationEvaluation(shader: Identifier, vararg defines: String): Builder = add(Stage.TESSELLATION_EVALUATION, shader, *defines)

        public fun build(): Shader {
            return Shader(name, stages)
        }
    }

    public class ShaderFile(public val location: Identifier, public val defines: List<String>)

    private object ReloadListener : SimpleResourceReloadListener<Unit> {
        val allShaders = weakSetOf<Shader>()

        init {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ReloadListener)
        }

        override fun getFabricId(): Identifier {
            return Identifier("liblib-albedo:shaders")
        }

        override fun load(
            manager: ResourceManager,
            profiler: Profiler,
            executor: Executor
        ): CompletableFuture<Unit> {
            return CompletableFuture.supplyAsync { }
        }

        override fun apply(
            data: Unit,
            manager: ResourceManager,
            profiler: Profiler,
            executor: Executor
        ): CompletableFuture<Void> {
            Client.minecraft.execute {
                allShaders.forEach { shader ->
                    shader.compile(manager)
                }
            }
            return CompletableFuture.runAsync {}
        }
    }

    public companion object {
        private val logger = LibLibAlbedo.makeLogger<Shader>()

        @JvmStatic
        public fun build(name: String): Builder {
            return Builder(name)
        }
    }
}