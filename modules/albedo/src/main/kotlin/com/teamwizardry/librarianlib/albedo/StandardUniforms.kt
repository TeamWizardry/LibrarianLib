package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.mixin.RenderSystemMixin
import com.teamwizardry.librarianlib.albedo.uniform.*
import com.teamwizardry.librarianlib.core.util.Client

/**
 * Standard uniform values (modelview matrix, projection matrix, fog parameters, etc.)
 *
 * Pass your uniforms to these in your [RenderBuffer.setupState] method.
 */
public object StandardUniforms {
    /** Populates the given uniform with the current model view matrix */
    @JvmStatic
    public fun setModelViewMatrix(uniform: Mat4x4Uniform) {
        uniform.set(RenderSystem.getModelViewMatrix())
    }

    /** Populates the given uniform with the current projection matrix */
    @JvmStatic
    public fun setProjectionMatrix(uniform: Mat4x4Uniform) {
        uniform.set(RenderSystem.getProjectionMatrix())
    }

    /** Populates the given uniforms with the current fog parameters */
    @JvmStatic
    public fun setFogParameters(fogStart: FloatUniform, fogEnd: FloatUniform, fogColor: FloatVec4Uniform) {
        fogStart.set(RenderSystem.getShaderFogStart())
        fogEnd.set(RenderSystem.getShaderFogEnd())
        val color = RenderSystem.getShaderFogColor()
        fogColor.set(color[0], color[1], color[2], color[3])
    }

    /** Populates the given uniforms with the current game time */
    @JvmStatic
    public fun setGameTime(uniform: FloatUniform) {
        uniform.set(RenderSystem.getShaderGameTime())
    }

    /** Populates the given uniform with the current screen size */
    @JvmStatic
    public fun setScreenSize(uniform: FloatVec2Uniform) {
        val window = Client.window
        uniform.set(window.framebufferWidth.toFloat(), window.framebufferHeight.toFloat())
    }

    /** Populates the given uniforms with the current standard light directions */
    @JvmStatic
    public fun setLights(light0Direction: FloatVec3Uniform, light1Direction: FloatVec3Uniform) {
        val directions = RenderSystemMixin.getShaderLightDirections()
        light0Direction.set(directions[0].x, directions[0].y, directions[0].z)
        light1Direction.set(directions[1].x, directions[1].y, directions[1].z)
    }
}