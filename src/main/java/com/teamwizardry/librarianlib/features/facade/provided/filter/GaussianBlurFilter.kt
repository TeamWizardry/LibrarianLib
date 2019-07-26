package com.teamwizardry.librarianlib.features.facade.provided.filter

import com.teamwizardry.librarianlib.features.facade.component.GuiLayerFilter
import com.teamwizardry.librarianlib.features.facade.value.RMValueInt
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

    override fun filter(layerFBO: Framebuffer, maskFBO: Framebuffer?) {
        blur(layerFBO)
        if(blurMask)
            maskFBO?.also { blur(it) }
    }

    private fun blur(framebuffer: Framebuffer) {

        val intermediateFBO = useFramebuffer(true, 1) {
            GaussianBlurShader.sigma = sigma
            GaussianBlurShader.horizontal = true
            ShaderHelper.useShader(GaussianBlurShader)

            GaussianBlurShader.bindTexture(framebuffer.framebufferTexture)

            val size = vec(Client.minecraft.displayWidth, Client.minecraft.displayHeight)
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            vb.pos(0.0, size.y, 0.0).endVertex()
            vb.pos(size.x, size.y, 0.0).endVertex()
            vb.pos(size.x, 0.0, 0.0).endVertex()
            vb.pos(0.0, 0.0, 0.0).endVertex()
            tessellator.draw()

            ShaderHelper.releaseShader()
        }

        useFramebuffer(true, 1, true, framebuffer) {
            GaussianBlurShader.sigma = sigma
            GaussianBlurShader.horizontal = false
            ShaderHelper.useShader(GaussianBlurShader)

            GaussianBlurShader.bindTexture(intermediateFBO.framebufferTexture)

            val size = vec(Client.minecraft.displayWidth, Client.minecraft.displayHeight)
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            vb.pos(0.0, size.y, 0.0).endVertex()
            vb.pos(size.x, size.y, 0.0).endVertex()
            vb.pos(size.x, 0.0, 0.0).endVertex()
            vb.pos(0.0, 0.0, 0.0).endVertex()
            tessellator.draw()

            ShaderHelper.releaseShader()
            releaseFramebuffer(intermediateFBO)
        }

        GL11.glDisable(GL11.GL_TEXTURE_1D)
        ShaderHelper.releaseShader()
    }
}