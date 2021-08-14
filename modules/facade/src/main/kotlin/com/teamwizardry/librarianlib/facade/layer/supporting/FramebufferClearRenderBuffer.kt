package com.teamwizardry.librarianlib.facade.layer.supporting

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.BaseRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.state.DefaultRenderStates
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.state.RenderState
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11

/**
 * Clears every pixel it's drawn over to be transparent black, with a depth reset to 1, and stencil reset to 0.
 */
internal class FramebufferClearRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FramebufferClearRenderBuffer>(vbo) {

    init {
        bind(shader)
    }

    companion object {
        val shader = Shader.build("framebuffer_clear")
            .vertex(Identifier("liblib-facade:shaders/framebuffer_clear.vert"))
            .fragment(Identifier("liblib-facade:shaders/framebuffer_clear.frag"))
            .build()
        val stencilState = object : RenderState.State(Identifier("liblib-facade:framebuffer_clear_stencil")) {
            override fun apply() {
                StencilUtil.enable()
                RenderSystem.stencilFunc(GL11.GL_ALWAYS, 0x00, 0x00)
                RenderSystem.stencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO)
                RenderSystem.stencilMask(0xFF)
            }

            override fun cleanup() {
                StencilUtil.resetTest(StencilUtil.currentStencil)
                StencilUtil.disable()
            }
        }
        val renderState = RenderState.normal.extend(DefaultRenderStates.DepthTest.DISABLED, DefaultRenderStates.Blend.OVERWRITE, stencilState)
        val SHARED = FramebufferClearRenderBuffer(VertexBuffer.SHARED)
    }
}
