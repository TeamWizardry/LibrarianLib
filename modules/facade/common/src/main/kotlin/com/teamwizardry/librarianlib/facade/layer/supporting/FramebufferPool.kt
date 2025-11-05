package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.albedo.buffer.Framebuffer
import com.teamwizardry.librarianlib.albedo.buffer.FramebufferAttachment
import com.teamwizardry.librarianlib.albedo.buffer.FramebufferAttachmentFormat
import com.teamwizardry.librarianlib.core.util.Client
import org.lwjgl.opengl.GL30.*
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
            fbo.width != Client.window.framebufferWidth ||
            fbo.height != Client.window.framebufferHeight
        ) {
            fbo.initFramebuffer(
                Client.window.framebufferWidth,
                Client.window.framebufferHeight,
                listOf(
                    FramebufferAttachment(
                        GL_COLOR_ATTACHMENT0,
                        FramebufferAttachmentFormat.Texture(GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE)
                    ),
                    FramebufferAttachment(
                        GL_DEPTH_STENCIL_ATTACHMENT,
                        FramebufferAttachmentFormat.Renderbuffer(GL_DEPTH24_STENCIL8)
                    )
                )
            )
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
            Client.minecraft.framebuffer.beginWrite(false)
        } else {
            framebuffer.begin(false)
        }
        current = framebuffer
    }

    private fun createFramebuffer(): Framebuffer {
        if (createdBuffers == maxFramebufferCount)
            throw IllegalStateException("Exceeded maximum of $maxFramebufferCount nested framebuffers")
        createdBuffers++
        return Framebuffer()
    }
}
