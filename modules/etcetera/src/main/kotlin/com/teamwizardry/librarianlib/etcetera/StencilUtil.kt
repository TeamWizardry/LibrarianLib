package com.teamwizardry.librarianlib.etcetera

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SidedRunnable
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

/**
 * Easily push and pop from the stencil mask
 */
object StencilUtil {
    /**
     * The current stencil level.
     */
    var currentStencil: Int = 0
        private set

    /**
     * Enables the stencil buffer for MC's main framebuffer, if it isn't enabled already
     */
    @JvmStatic
    fun enableStencilBuffer() {
        enableStencilBuffer(Client.minecraft.framebuffer)
    }

    /**
     * Enables the stencil buffer for the passed framebuffer, if it isn't enabled already
     */
    @JvmStatic
    fun enableStencilBuffer(fbo: Framebuffer) {
        if (!fbo.isStencilEnabled)
            fbo.enableStencil()
    }

    /**
     * Clear the stencil buffer
     */
    fun clear() {
        currentStencil = 0

        glEnable(GL_STENCIL_TEST)

        RenderSystem.stencilFunc(GL_ALWAYS, 0x00, 0x00)
        RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
        RenderSystem.stencilMask(0xFF)
        RenderSystem.clearStencil(0)
        glClear(GL_STENCIL_BUFFER_BIT)

        glDisable(GL_STENCIL_TEST)
    }

    /**
     * Enable stencil tests
     */
    fun enable() {
        glEnable(GL_STENCIL_TEST)
    }

    /**
     * Disable stencil tests
     */
    fun disable() {
        glDisable(GL_STENCIL_TEST)
    }

    fun resetTest(level: Int) {
        currentStencil = level

        RenderSystem.stencilMask(0x00)
        RenderSystem.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun push(draw: Runnable) {
        currentStencil += 1

        RenderSystem.depthMask(false)
        RenderSystem.colorMask(false, false, false, false)

        RenderSystem.stencilFunc(GL_NEVER, 0, 0xFF)
        RenderSystem.stencilOp(GL_INCR, GL_KEEP, GL_KEEP)
        RenderSystem.stencilMask(0xFF)
        draw.run()

        RenderSystem.colorMask(true, true, true, true)
        RenderSystem.depthMask(true)

        RenderSystem.stencilMask(0x00)
        RenderSystem.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun pop(draw: Runnable) {
        currentStencil -= 1

        RenderSystem.depthMask(false)
        RenderSystem.colorMask(false, false, false, false)

        RenderSystem.stencilFunc(GL_NEVER, 0, 0xFF)
        RenderSystem.stencilOp(GL_DECR, GL_KEEP, GL_KEEP)
        RenderSystem.stencilMask(0xFF)
        draw.run()

        RenderSystem.colorMask(true, true, true, true)
        RenderSystem.depthMask(true)

        RenderSystem.stencilMask(0x00)
        RenderSystem.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    fun push(kotlin: () -> Unit) = push(Runnable(kotlin))
    fun pop(kotlin: () -> Unit) = pop(Runnable(kotlin))
}
