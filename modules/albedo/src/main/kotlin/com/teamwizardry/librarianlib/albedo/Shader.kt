package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.GlResourceGc
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
     * bound shader outside of that will not be reflected.
     */
    var isBound: Boolean = false
        private set

    fun bind() {
        GlStateManager.useProgram(glProgram)
        isBound = true
    }

    fun unbind() {
        GlStateManager.useProgram(0)
        isBound = false
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

    private fun compile() {
        var vertexHandle = 0
        var fragmentHandle = 0
        try {
            if(vertexName != null) {
                vertexHandle = compileShader(GL_VERTEX_SHADER, "vertex shader", Client.getResourceText(vertexName))
            }
            if(fragmentName != null) {
                fragmentHandle = compileShader(GL_FRAGMENT_SHADER, "fragment shader", Client.getResourceText(fragmentName))
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
            fail("Could not compile $typeName: $log")
        }

        return shader
    }

    /**
     * Check the GLSL version directive
     */
    private fun checkVersion(source: String) {
        val match = """#version\s+(\d+)""".toRegex().find(source) ?: return
        val version = match.groupValues[1].toInt()
        if(version > 120)
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
}