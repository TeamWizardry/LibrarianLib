package com.teamwizardry.librarianlib.gui.component

import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import java.util.LinkedList

abstract class GuiLayerFilter {

    abstract fun filter(layer: GuiLayer, layerFBO: Framebuffer, maskFBO: Framebuffer?)

    fun drawLayerQuad(layer: GuiLayer) {
        val size = layer.size
        val maxU = (size.x * layer.rasterizationScale) / Client.minecraft.displayWidth
        val maxV = (size.y * layer.rasterizationScale) / Client.minecraft.displayHeight

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        vb.pos(0.0, size.y, 0.0).tex(0.0, 1.0 - maxV).endVertex()
        vb.pos(size.x, size.y, 0.0).tex(maxU, 1.0 - maxV).endVertex()
        vb.pos(size.x, 0.0, 0.0).tex(maxU, 1.0).endVertex()
        vb.pos(0.0, 0.0, 0.0).tex(0.0, 1.0).endVertex()
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
            GlStateManager.translate(0.0f, 0.0f, -2000.0f)

            val scaleFactor = Client.resolution.scaleFactor.toDouble()
            GlStateManager.scale(scale/scaleFactor, scale/scaleFactor, 1.0)
        }

        fun revertTransform() {
            GlStateManager.popMatrix()
        }

        fun pushFramebuffer(): Framebuffer {
            var fbo = buffers.pollFirst() ?: createFramebuffer()
            if(
                fbo.framebufferWidth != Client.minecraft.displayWidth ||
                fbo.framebufferHeight != Client.minecraft.displayHeight
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
                    framebuffer.framebufferClear()
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
            val fbo = Framebuffer(Client.minecraft.displayWidth, Client.minecraft.displayHeight, true)
            fbo.enableStencil()
            fbo.framebufferColor = floatArrayOf(0f, 0f, 0f, 0f)
            createdBuffers++
            return fbo
        }
    }
}