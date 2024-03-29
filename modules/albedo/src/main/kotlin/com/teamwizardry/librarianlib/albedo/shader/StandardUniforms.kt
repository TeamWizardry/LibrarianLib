package com.teamwizardry.librarianlib.albedo.shader

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.mixin.LightmapTextureManagerMixin
import com.teamwizardry.librarianlib.albedo.mixin.RenderSystemMixin
import com.teamwizardry.librarianlib.albedo.shader.uniform.*
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.mixinCast
import org.lwjgl.opengl.GL11

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

    @JvmStatic
    public fun setLightmap(uniform: SamplerUniform) {
        uniform.set(getLightmapId())
    }

    @JvmStatic
    public fun getLightmapId(): Int {
        val lightmap = mixinCast<LightmapTextureManagerMixin>(Client.minecraft.gameRenderer.lightmapTextureManager).texture
        GlStateManager._bindTexture(lightmap.glId)
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        return lightmap.glId
    }
}