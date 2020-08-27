package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.shader.Framebuffer
import java.util.LinkedList
import java.util.function.Consumer

internal object FramebufferPool {
    private val maxFramebufferCount = 16
    private var createdBuffers = 0
    private val bufferPool = LinkedList<Framebuffer>()

    private var current: Framebuffer? = null

    /**
     * Gets a framebuffer out of the pool, creating one if necessary. You *must* pass the returned buffer to
     * [releaseFramebuffer] after you are done with it.
     *
     * Note: there is a hard limit of 16 framebuffers currently in use.
     */
    fun getFramebuffer(): Framebuffer {
        val fbo = bufferPool.pollFirst() ?: createFramebuffer()
        if (
            fbo.framebufferWidth != Client.window.framebufferWidth ||
            fbo.framebufferHeight != Client.window.framebufferHeight
        ) {
            fbo.resize(Client.window.framebufferWidth, Client.window.framebufferHeight, Minecraft.IS_RUNNING_ON_MAC)
        }

        return fbo
    }

    fun releaseFramebuffer(framebuffer: Framebuffer) {
        bufferPool.addFirst(framebuffer)
    }

    /**
     * Renders to and returns a framebuffer. You *must* pass the returned buffer to [releaseFramebuffer] after you
     * are done with it.
     *
     * Note: there is a hard limit of 16 framebuffers currently in use.
     */
    fun renderToFramebuffer(callback: Consumer<Framebuffer>): Framebuffer {
        val stencilLevel = StencilUtil.currentStencil
        val existing = current // store the current framebuffer so we can reset it later

        val framebuffer = getFramebuffer()
        useFramebuffer(framebuffer)
        try {
            callback.accept(framebuffer)
        } finally {
            useFramebuffer(existing)
            StencilUtil.resetTest(stencilLevel)
        }

        return framebuffer
    }

    private fun useFramebuffer(framebuffer: Framebuffer?) {
        if (framebuffer == null) {
            Client.minecraft.framebuffer.bindFramebuffer(true)
        } else {
            framebuffer.bindFramebuffer(true)
        }
        current = framebuffer
    }

    private fun createFramebuffer(): Framebuffer {
        if (createdBuffers == maxFramebufferCount)
            throw IllegalStateException("Exceeded maximum of $maxFramebufferCount nested framebuffers")
        val fbo = Framebuffer(Client.window.framebufferWidth, Client.window.framebufferHeight, true, Minecraft.IS_RUNNING_ON_MAC)
        fbo.enableStencil()
        fbo.setFramebufferColor(0f, 0f, 0f, 0f)
        createdBuffers++
        return fbo
    }
}
