package com.teamwizardry.librarianlib.albedo

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
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

public abstract class Shader(
    /**
     * An arbitrary name used for logging
     */
    public val shaderName: String,
    /**
     * The location of the vertex shader
     */
    public val vertexName: Identifier,
    /**
     * The location of the fragment shader
     */
    public val fragmentName: Identifier
) {
    /**
     * The OpenGL handle for the shader program
     */
    public var glProgram: Int by GlResourceGc.track(this, 0) { glDeleteProgram(it) }
        private set

    private val _uniforms = mutableListOf<UniformInfo>()
    private val _attributes = mutableListOf<AttributeInfo>()

    public val uniforms: List<UniformInfo> = _uniforms.unmodifiableView()
    public val attributes: List<AttributeInfo> = _attributes.unmodifiableView()

    /**
     * Binds this program.
     */
    public fun use() {
        glUseProgram(glProgram)
    }

    public fun delete() {
        glDeleteProgram(glProgram)
        glProgram = 0
    }

    init {
        @Suppress("LeakingThis")
        allShaders.add(this)
        compile()
    }

    @JvmSynthetic
    internal fun compile() {
        compile(Client.resourceManager)
    }

    private fun compile(resourceManager: ResourceManager) {
        logger.info("Creating shader program $shaderName:")
        glDeleteProgram(glProgram)
        glProgram = 0
        var vertexHandle = 0
        var fragmentHandle = 0
        try {
            vertexHandle = compileShader(GL_VERTEX_SHADER, "vertex", ShaderSource(resourceManager, vertexName))
            fragmentHandle = compileShader(GL_FRAGMENT_SHADER, "fragment", ShaderSource(resourceManager, fragmentName))
            glDeleteProgram(glProgram)
            glProgram = linkProgram(vertexHandle, fragmentHandle)
        } finally {
            if (glProgram != 0) {
                glDetachShader(glProgram, vertexHandle)
                glDetachShader(glProgram, fragmentHandle)
            }
            glDeleteShader(vertexHandle)
            glDeleteShader(fragmentHandle)
        }
        logger.debug("Finished compiling shader program")
        readUniforms()
        logger.debug("Found ${uniforms.size} uniforms: [${uniforms.joinToString { it.name }}]")
        readAttributes()
        logger.debug("Found ${attributes.size} attributes: [${attributes.joinToString { it.name }}]")
    }

    private fun compileShader(type: Int, typeName: String, source: ShaderSource): Int {
        logger.debug("Compiling $typeName shader ${source.location}")
        val shader = glCreateShader(type)
        if (shader == 0)
            throw ShaderCompilationException("Could not create shader object")
        glShaderSource(shader, source.source)
        glCompileShader(shader)

        val status = glGetShaderi(shader, GL_COMPILE_STATUS)
        if (status == GL_FALSE) {
            glDeleteShader(shader)

            val logLength = glGetShaderi(shader, GL_INFO_LOG_LENGTH)
            var log = glGetShaderInfoLog(shader, logLength)
            log = source.replaceFilenames(log)

            logger.error("Error compiling $typeName shader. Shader source text:\n${source.source}")
            throw ShaderCompilationException("Error compiling $typeName shader `${source.location}`:\n$log")
        }

        return shader
    }

    private fun linkProgram(vertexHandle: Int, fragmentHandle: Int): Int {
        logger.debug("Linking shader")
        val program = glCreateProgram()
        if (program == 0)
            throw ShaderCompilationException("could not create program object")

        if (vertexHandle != 0)
            glAttachShader(program, vertexHandle)
        if (fragmentHandle != 0)
            glAttachShader(program, fragmentHandle)

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
        _uniforms.clear()
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
                _uniforms.add(UniformInfo(name, glType.get(), size.get(), location))
            }
        }
    }

    private fun readAttributes() {
        _attributes.clear()
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
                _attributes.add(AttributeInfo(name, glType.get(), size.get(), index))
            }
        }
    }

    public data class UniformInfo(val name: String, val type: Int, val size: Int, val location: Int)
    public data class AttributeInfo(val name: String, val type: Int, val size: Int, val index: Int)

    private companion object {
        private val allShaders = weakSetOf<Shader>()

        init {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(object : SimpleResourceReloadListener<Unit> {
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
                        return CompletableFuture.runAsync {
                            allShaders.forEach { shader ->
                                shader.compile(manager)
                            }
                        }
                    }
                })
        }

        private val logger = LibLibAlbedo.makeLogger<Shader>()
    }
}