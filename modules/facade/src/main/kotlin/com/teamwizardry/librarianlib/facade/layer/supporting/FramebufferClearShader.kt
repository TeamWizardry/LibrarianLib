package com.teamwizardry.librarianlib.facade.layer.supporting

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.Shader
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11

/**
 * Clears every pixel it's drawn over to be transparent black, with a depth reset to 1, and stencil reset to 0.
 */
internal object FramebufferClearShader: Shader("framebuffer_clear", null, Identifier("librarianlib:facade/shaders/framebuffer_clear.frag")) {

    override fun setupState() {
        RenderSystem.disableAlphaTest()
        RenderSystem.disableDepthTest()
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        )
        StencilUtil.enable()
        RenderSystem.stencilFunc(GL11.GL_ALWAYS, 0x00, 0x00)
        RenderSystem.stencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO)
        RenderSystem.stencilMask(0xFF)
    }

    override fun teardownState() {
        RenderSystem.disableAlphaTest()
        RenderSystem.disableDepthTest()
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        StencilUtil.resetTest(StencilUtil.currentStencil)
        StencilUtil.disable()
    }
}
