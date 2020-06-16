package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import com.teamwizardry.librarianlib.core.util.resolveSibling
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.util.LinkedList

abstract class Shader(val shaderName: String, val vertexName: ResourceLocation?, val fragmentName: ResourceLocation?) {
    /**
     * The OpenGL handle for the shader program
     */
    var glProgram: Int by GlResourceGc.Value(0).also { track(it) }
        private set

    /**
     * True if this shader is currently bound. This only tracks calls to [bind] and [unbind], so modifications of the
     * bound shader outside of that will not be reflected. Binding another LibrarianLib shader will unbind this one.
     */
    val isBound: Boolean
        get() = currentlyBound === this

    /**
     * Whether the uniform objects have been bound internally to their respective locations
     */
    private var areUniformsBound: Boolean = false
    private var uniforms: List<GLSL>? = null

    fun bind() {
        GlStateManager.useProgram(glProgram)
        currentlyBound = this
        if(uniforms == null && glProgram != 0) {
            uniforms = UniformBinder.bindAllUniforms(this, glProgram)
        }
    }

    fun unbind() {
        GlStateManager.useProgram(0)
        currentlyBound = null
    }

    fun pushUniforms() {
        uniforms?.forEach { it.push() }
    }

    fun delete() {
        GlStateManager.deleteProgram(glProgram)
        glProgram = 0
    }

    private fun track(glHandle: GlResourceGc.Value<Int>) {
        GlResourceGc.track(this) {
            GlStateManager.deleteProgram(glHandle.value)
            glHandle.value = 0
        }
    }

    /**
     * The base source string number. Sufficiently high that _hopefully_ a substitution in the error log will be
     * correct, sufficiently low so even a signed short won't overflow, and sufficiently different from the max signed
     * short value that anyone using that max value in their code won't have collisions
     */
    private val sourceNumberBase = 31500

    init {
        @Suppress("LeakingThis")
        allShaders.add(this)
        compile()
    }

    @JvmSynthetic
    internal fun compile() {
        compile(Client.resourceManager)
    }

    private fun compile(resourceManager: IResourceManager) {
        if(areUniformsBound) {
            UniformBinder.unbindAllUniforms(this)
            uniforms = null
        }
        var vertexHandle = 0
        var fragmentHandle = 0
        try {
            if(vertexName != null) {
                logger.info("Compiling vertex shader $vertexName")
                val files = mutableMapOf<ResourceLocation, Int>()
                vertexHandle = compileShader(GL_VERTEX_SHADER, "vertex",
                    readShader(resourceManager, vertexName, files), vertexName, files)
            }
            if(fragmentName != null) {
                logger.info("Compiling fragment shader $fragmentName")
                val files = mutableMapOf<ResourceLocation, Int>()
                fragmentHandle = compileShader(GL_FRAGMENT_SHADER, "fragment",
                    readShader(resourceManager, fragmentName, files), fragmentName, files)
            }
            GlStateManager.deleteProgram(glProgram)
            glProgram = linkProgram(vertexHandle, fragmentHandle)
        } finally {
            if(glProgram != 0) {
                glDetachShader(glProgram, vertexHandle)
                glDetachShader(glProgram, fragmentHandle)
            }
            GlStateManager.deleteShader(vertexHandle)
            GlStateManager.deleteShader(fragmentHandle)
        }
    }

    private fun readShader(
        resourceManager: IResourceManager, name: ResourceLocation,
        files: MutableMap<ResourceLocation, Int>,
        stack: LinkedList<ResourceLocation> = LinkedList()
    ): String {
        if(name in stack) {
            val cycleString = stack.reversed().joinToString(" -> ") { if(it == name) "[$it" else "$it" } + " -> $name]"
            throw ShaderCompilationException("#import cycle: $cycleString")
        }
        stack.push(name)
        val sourceNumber = files.getOrPut(name) { sourceNumberBase + files.size }
        val importRegex = """^\s*#pragma\s*import\s*<\s*(\S*)\s*>\s*$""".toRegex()

        val text = Client.getResourceText(resourceManager, name)
        var out = ""
        text.lineSequence().forEachIndexed { i, line ->
            val lineNumber = i+1
            val importMatch = importRegex.matchEntire(line)
            if(lineNumber == 1 && "#version" !in text) {
                out += "#line 0 $sourceNumber\n"
            }

            if(importMatch != null) {
                val importName = importMatch.groupValues[1]
                val importLocation = if(':' !in importName) {
                    name.resolveSibling(importName)
                } else {
                    ResourceLocation(importName)
                }

                out += readShader(resourceManager, importLocation, files, stack)
                out += "\n#line $lineNumber $sourceNumber\n"
            } else {
                out += "$line\n"
            }

            if("#version" in line) {
                out += "#line ${lineNumber-1} $sourceNumber\n"
            }
        }

        stack.pop()

        return out
    }

    private fun compileShader(type: Int, typeName: String, source: String, location: ResourceLocation, files: Map<ResourceLocation, Int>): Int {
        checkVersion(source)
        val shader = GlStateManager.createShader(type)
        if(shader == 0)
            throw ShaderCompilationException("could not create shader object")
        GlStateManager.shaderSource(shader, source)
        GlStateManager.compileShader(shader)

        val status = GlStateManager.getShader(shader, GL_COMPILE_STATUS)
        if(status == GL_FALSE) {
            val logLength = GlStateManager.getShader(shader, GL_INFO_LOG_LENGTH)
            var log = GlStateManager.getShaderInfoLog(shader, logLength)
            GlStateManager.deleteShader(shader)
            files.forEach { (key, value) ->
                log = log.replace(Regex("\\b$value\\b"), "${key.path}")
            }
            throw ShaderCompilationException("Error compiling $typeName shader `$location`:\n$log")
        }

        return shader
    }

    /**
     * Check the GLSL version directive
     */
    private fun checkVersion(source: String) {
        val match = """#version\s+(\d+)""".toRegex().find(source) ?: return
        val version = match.groupValues[1].toInt()
        if(version > 120) // Apple doesn't support OpenGL 3.0+ properly, so we're stuck with OpenGL 2.1 shaders
            throw ShaderCompilationException("Maximum GLSL version supported by LibrarianLib is 1.20, found `${match.value}`")
    }

    private fun linkProgram(vertexHandle: Int, fragmentHandle: Int): Int {
        val program = GlStateManager.createProgram()
        if(program == 0)
            throw ShaderCompilationException("could not create program object")

        // todo set up linking: https://www.khronos.org/opengl/wiki/Shader_Compilation#Before_linking

        if(vertexHandle != 0)
            GlStateManager.attachShader(program, vertexHandle)
        if(fragmentHandle != 0)
            GlStateManager.attachShader(program, fragmentHandle)

        GlStateManager.linkProgram(program)

        val status = GlStateManager.getProgram(program, GL_LINK_STATUS)
        if(status == GL_FALSE) {
            val logLength = GlStateManager.getProgram(program, GL_INFO_LOG_LENGTH)
            val log = GlStateManager.getProgramInfoLog(program, logLength)
            GlStateManager.deleteProgram(program)
            throw ShaderCompilationException("Could not link program: $log")
        }

        return program
    }

    private fun fail(message: String): Nothing {
        throw ShaderCompilationException("Error compiling '$shaderName': $message")
    }

    companion object {
        private var currentlyBound: Shader? = null
        private val allShaders = weakSetOf<Shader>()
        init {
            Client.resourceReloadHandler.register(object: ISimpleReloadListener<Any?> {
                override fun prepare(resourceManager: IResourceManager, profiler: IProfiler): Any? = null

                override fun apply(result: Any?, resourceManager: IResourceManager, profiler: IProfiler) {
                    allShaders.forEach { shader ->
                        shader.compile(resourceManager)
                    }
                }
            })
        }
    }
}