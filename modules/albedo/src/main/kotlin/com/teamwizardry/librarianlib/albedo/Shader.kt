package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import java.io.IOException

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

    fun bind() {
        GlStateManager.useProgram(glProgram)
        currentlyBound = this
    }

    fun unbind() {
        GlStateManager.useProgram(0)
        currentlyBound = null
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

    init {
        @Suppress("LeakingThis")
        allShaders.add(this)
        compile(Client.resourceManager)
    }

    private fun compile(resourceManager: IResourceManager) {
        var vertexHandle = 0
        var fragmentHandle = 0
        try {
            if(vertexName != null) {
                logger.info("Compiling vertex shader $vertexName")
                vertexHandle = compileShader(GL_VERTEX_SHADER, "vertex", Client.getResourceText(resourceManager, vertexName))
            }
            if(fragmentName != null) {
                logger.info("Compiling fragment shader $fragmentName")
                fragmentHandle = compileShader(GL_FRAGMENT_SHADER, "fragment", Client.getResourceText(resourceManager, fragmentName))
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

    private fun compileShader(type: Int, typeName: String, source: String): Int {
        checkVersion(source)
        val shader = GlStateManager.createShader(type)
        if(shader == 0)
            fail("could not create shader object")
        GlStateManager.shaderSource(shader, source)
        GlStateManager.compileShader(shader)

        val status = GlStateManager.getShader(shader, GL_COMPILE_STATUS)
        if(status == GL_FALSE) {
            val logLength = GlStateManager.getShader(shader, GL_INFO_LOG_LENGTH)
            val log = GlStateManager.getShaderInfoLog(shader, logLength)
            GlStateManager.deleteShader(shader)
            fail("Could not compile $typeName shader: $log")
        }

        return shader
    }

    /**
     * Check the GLSL version directive
     */
    private fun checkVersion(source: String) {
        val match = """#version\s+(\d+)""".toRegex().find(source) ?: return
        val version = match.groupValues[1].toInt()
        if(version > 120) // Apple's OpenGL drivers shit themselves with anything > 120
            throw RuntimeException("Maximum GLSL version supported by LibrarianLib is 1.20, found `${match.value}`")
    }

    private fun linkProgram(vertexHandle: Int, fragmentHandle: Int): Int {
        val program = GlStateManager.createProgram()
        if(program == 0)
            fail("could not create program object")

        // set up linking: https://www.khronos.org/opengl/wiki/Shader_Compilation#Before_linking

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
            fail("Could not compile program: $log")
        }

        return program
    }

    private fun fail(message: String): Nothing {
        throw RuntimeException("Error compiling '$shaderName': $message")
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