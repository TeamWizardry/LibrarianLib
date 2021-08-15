package com.teamwizardry.librarianlib.albedo.buffer

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.albedo.AlbedoException
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableCopy
import org.lwjgl.opengl.GL33.*
import java.nio.ByteBuffer

public class Framebuffer {
    public var fbo: Int by GlResourceGc.track(this, glGenFramebuffers()) { glDeleteFramebuffers(it) }
        private set
    public var width: Int = 0
        private set
    public var height: Int = 0
        private set
    public var attachments: List<FramebufferAttachment> by GlResourceGc.track(this, emptyList()) {
        it.forEach { attachment ->
            attachment.format.delete(attachment.glId)
        }
    }
        private set
    private var attachmentMap = mapOf<Int, FramebufferAttachment>()

    public fun initFramebuffer(
        width: Int, height: Int,
        attachments: List<FramebufferAttachment>,
    ) {
        this.attachments.forEach { it.format.delete(it.glId) }
        glDeleteFramebuffers(fbo)

        this.width = width
        this.height = height
        this.attachments = attachments.unmodifiableCopy()
        this.attachmentMap = attachments.associateBy { it.glAttachment }
        fbo = glGenFramebuffers()

        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        for (value in attachments) {
            when (val format = value.format) {
                is FramebufferAttachmentFormat.Renderbuffer -> {
                    value.glId = glGenRenderbuffers()
                    glBindRenderbuffer(GL_RENDERBUFFER, value.glId)
                    glRenderbufferStorage(GL_RENDERBUFFER, format.internalFormat, width, height)
                    glBindRenderbuffer(GL_RENDERBUFFER, 0)
                    glFramebufferRenderbuffer(GL_FRAMEBUFFER, value.glAttachment, GL_RENDERBUFFER, value.glId)
                }
                is FramebufferAttachmentFormat.Texture -> {
                    value.glId = glGenTextures()
                    glBindTexture(GL_TEXTURE_2D, value.glId)
                    glTexImage2D(
                        GL_TEXTURE_2D, 0,
                        format.internalFormat,
                        width, height, 0,
                        format.format, format.type,
                        null as ByteBuffer?
                    )
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                    glBindTexture(GL_TEXTURE_2D, 0)
                    glFramebufferTexture2D(GL_FRAMEBUFFER, value.glAttachment, GL_TEXTURE_2D, value.glId, 0)
                }
            }
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        checkStatus()
    }

    public operator fun get(glAttachment: Int): FramebufferAttachment {
        return attachmentMap.getValue(glAttachment)
    }

    public fun getOrNull(glAttachment: Int): FramebufferAttachment? {
        return attachmentMap[glAttachment]
    }

    public fun begin(setViewport: Boolean) {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        if (setViewport)
            glViewport(0, 0, this.width, this.height)
    }

    public fun end(setViewport: Boolean) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        if (setViewport)
            glViewport(0, 0, Client.window.framebufferWidth, Client.window.framebufferHeight)
    }

    private fun checkStatus() {
        val status = GlStateManager.glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            val name = when (status) {
                GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
                GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"
                GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER"
                GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER"
                GL_FRAMEBUFFER_UNSUPPORTED -> "GL_FRAMEBUFFER_UNSUPPORTED"
                GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE"
                GL_FRAMEBUFFER_UNDEFINED -> "GL_FRAMEBUFFER_UNDEFINED"
                else -> throw AlbedoException("Unknown framebuffer status 0x${status.toString(16)}")
            }
            throw AlbedoException("Incomplete framebuffer: status = $name")
        }
    }


}

public class FramebufferAttachment(
    public val glAttachment: Int,
    public val format: FramebufferAttachmentFormat
) {
    public var glId: Int = 0
}

/**
 * internal formats: https://www.khronos.org/opengl/wiki/Image_Format
 */
public sealed class FramebufferAttachmentFormat {
    public abstract fun delete(glId: Int)

    public class Renderbuffer(public val internalFormat: Int) : FramebufferAttachmentFormat() {
        override fun delete(glId: Int) {
            glDeleteRenderbuffers(glId)
        }
    }

    public class Texture(public val internalFormat: Int, public val format: Int, public val type: Int) :
        FramebufferAttachmentFormat() {
        override fun delete(glId: Int) {
            glDeleteTextures(glId)
        }
    }
}

