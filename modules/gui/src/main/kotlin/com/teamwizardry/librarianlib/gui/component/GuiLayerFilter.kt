package com.teamwizardry.librarianlib.gui.component

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import java.util.LinkedList

abstract class GuiLayerFilter {

    /*
    abstract fun filter(component: GuiComponent, layerFBO: Framebuffer, maskFBO: Framebuffer?)

    fun drawLayerQuad(component: GuiComponent) {
        val size = component.size
        val maxU = (size.x * component.rasterizationScale) / Client.window.framebufferWidth
        val maxV = (size.y * component.rasterizationScale) / Client.window.framebufferHeight

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        vb.pos(0.0, size.y, 0.0).tex(0f, 1f - maxV.toFloat()).endVertex()
        vb.pos(size.x, size.y, 0.0).tex(maxU.toFloat(), 1f - maxV.toFloat()).endVertex()
        vb.pos(size.x, 0.0, 0.0).tex(maxU.toFloat(), 1f).endVertex()
        vb.pos(0.0, 0.0, 0.0).tex(0f, 1f).endVertex()
        tessellator.draw()
    }

    companion object {
        val maxFramebufferCount = 16
        var createdBuffers = 0
        val buffers = LinkedList<Framebuffer>()

        val bufferStack = LinkedList<Framebuffer>()
        val currentFramebuffer: Framebuffer? = bufferStack.peekFirst()

        fun resetTransform(scale: Int) {
            GlStateManager.pushMatrix()
            GlStateManager.loadIdentity()
            GlStateManager.translatef(0.0f, 0.0f, -2000.0f)

            val scaleFactor = Client.guiScaleFactor
            GlStateManager.scaled(scale/scaleFactor, scale/scaleFactor, 1.0)
        }

        fun revertTransform() {
            GlStateManager.popMatrix()
        }

        fun pushFramebuffer(): Framebuffer {
            var fbo = buffers.pollFirst() ?: createFramebuffer()
            if(
                fbo.framebufferWidth != Client.window.framebufferWidth ||
                fbo.framebufferHeight != Client.window.framebufferHeight
            ) {
                fbo.deleteFramebuffer()
                createdBuffers--
                fbo = createFramebuffer()
            }
            bufferStack.addFirst(fbo)

            return fbo
        }

        fun popFramebuffer() {
            val newFbo = currentFramebuffer
            if(newFbo == null) {
                Client.minecraft.framebuffer.bindFramebuffer(true)
            } else {
                newFbo.bindFramebuffer(true)
            }
        }

        fun releaseFramebuffer(framebuffer: Framebuffer) {
            buffers.addFirst(framebuffer)
        }

        fun useFramebuffer(loadIdentity: Boolean, rasterizationScale: Int, callback: () -> Unit): Framebuffer {
            val framebuffer = pushFramebuffer()
            useFramebuffer(loadIdentity, rasterizationScale, true, framebuffer, callback)
            bufferStack.removeFirst()
            return framebuffer
        }

        fun useFramebuffer(loadIdentity: Boolean, rasterizationScale: Int, clear: Boolean, framebuffer: Framebuffer, callback: () -> Unit) {
            val stencilLevel = StencilUtil.currentStencil
            if(loadIdentity)
                resetTransform(rasterizationScale)

            try {
                if(clear)
                    framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC)
                framebuffer.bindFramebuffer(true)

                StencilUtil.clear()

                callback()

            } finally {
                if(loadIdentity)
                    revertTransform()
                popFramebuffer()
                StencilUtil.resetTest(stencilLevel)
            }
        }

        fun createFramebuffer(): Framebuffer {
            if(createdBuffers == maxFramebufferCount)
                throw IllegalStateException("Exceeded maximum of $maxFramebufferCount nested framebuffers")
            val fbo = Framebuffer(Client.window.framebufferWidth, Client.window.framebufferHeight, true, Minecraft.IS_RUNNING_ON_MAC)
            // fbo.enableStencil() // TODO: https://github.com/MinecraftForge/MinecraftForge/pull/6543
            fbo.setFramebufferColor(0f, 0f, 0f, 0f)
            createdBuffers++
            return fbo
        }
    }
     */
}