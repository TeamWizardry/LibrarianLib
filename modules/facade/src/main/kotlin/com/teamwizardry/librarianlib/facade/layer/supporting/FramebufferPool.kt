package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import java.util.*
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
            fbo.viewportWidth != Client.window.framebufferWidth ||
            fbo.viewportHeight != Client.window.framebufferHeight
        ) {
            fbo.resize(Client.window.framebufferWidth, Client.window.framebufferHeight, MinecraftClient.IS_SYSTEM_MAC)
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
            Client.minecraft.framebuffer.beginWrite(true)
        } else {
            framebuffer.beginWrite(true)
        }
        current = framebuffer
    }

    private fun createFramebuffer(): Framebuffer {
        if (createdBuffers == maxFramebufferCount)
            throw IllegalStateException("Exceeded maximum of $maxFramebufferCount nested framebuffers")
        val fbo = Framebuffer(Client.window.framebufferWidth, Client.window.framebufferHeight, true, MinecraftClient.IS_SYSTEM_MAC)
        fbo.enableStencil()
        fbo.setFramebufferColor(0f, 0f, 0f, 0f)
        createdBuffers++
        return fbo
    }
}
