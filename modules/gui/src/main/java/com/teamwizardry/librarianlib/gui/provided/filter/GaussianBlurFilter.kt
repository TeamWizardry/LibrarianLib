package com.teamwizardry.librarianlib.gui.provided.filter

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerFilter
import com.teamwizardry.librarianlib.gui.value.RMValueInt
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11

class GaussianBlurFilter(sigma: Int): GuiLayerFilter() {
    val sigma_rm: RMValueInt = RMValueInt(sigma)
    var sigma: Int by sigma_rm
    var blurMask: Boolean = true

    override fun filter(layer: GuiLayer, layerFBO: Framebuffer, maskFBO: Framebuffer?) {
        blur(layer, layerFBO)
        if(blurMask)
            maskFBO?.also { blur(layer, it) }
    }

    private fun blur(layer: GuiLayer, framebuffer: Framebuffer) {

        val intermediateFBO = useFramebuffer(false, 1) {
            GaussianBlurShader.sigma = sigma
            GaussianBlurShader.horizontal = true
            ShaderHelper.useShader(GaussianBlurShader)

            GaussianBlurShader.bindTexture(framebuffer.framebufferTexture)

            drawLayerQuad(layer)

            ShaderHelper.releaseShader()
        }

        useFramebuffer(false, 1, true, framebuffer) {
            GaussianBlurShader.sigma = sigma
            GaussianBlurShader.horizontal = false
            ShaderHelper.useShader(GaussianBlurShader)

            GaussianBlurShader.bindTexture(intermediateFBO.framebufferTexture)

            drawLayerQuad(layer)

            ShaderHelper.releaseShader()
            releaseFramebuffer(intermediateFBO)
        }

        GL11.glDisable(GL11.GL_TEXTURE_1D)
    }
}