package com.teamwizardry.librarianlib.facade.layer.supporting

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11.*

/**
 * Easily push and pop from the stencil mask
 */
public object StencilUtil {
    /**
     * The current stencil level.
     */
    public var currentStencil: Int = 0
        private set

    /**
     * Enables the stencil buffer for MC's main framebuffer, if it isn't enabled already
     */
    @JvmStatic
    public fun enableStencilBuffer() {
        enableStencilBuffer(Client.minecraft.framebuffer)
    }

    /**
     * Enables the stencil buffer for the passed framebuffer, if it isn't enabled already
     */
    @JvmStatic
    public fun enableStencilBuffer(fbo: Framebuffer) {
        if (!fbo.isStencilEnabled)
            fbo.enableStencil()
    }

    /**
     * Clear the stencil buffer
     */
    public fun clear() {
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
    public fun enable() {
        glEnable(GL_STENCIL_TEST)
    }

    /**
     * Disable stencil tests
     */
    public fun disable() {
        glDisable(GL_STENCIL_TEST)
    }

    public fun resetTest(level: Int) {
        currentStencil = level

        RenderSystem.stencilMask(0x00)
        RenderSystem.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    public fun push(draw: Runnable) {
        currentStencil += 1

        RenderSystem.depthMask(false)
        RenderSystem.colorMask(false, false, false, false)
        RenderSystem.disableDepthTest()

        // One option here is to set the function to `GL_NEVER` and make the test fail operation `GL_INCR`
        // However, by doing it that way all the fragments are discarded, so we can't debug the mask by setting the
        // color mask in `draw`. By setting the function to `GL_ALWAYS` and disabling depth testing, we're able to
        // enable the color mask in `draw` to make the mask visible.
        RenderSystem.stencilFunc(GL_ALWAYS, 0, 0xFF)
        RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_INCR)
        RenderSystem.stencilMask(0xFF)
        draw.run()

        RenderSystem.enableDepthTest()
        RenderSystem.colorMask(true, true, true, true)
        RenderSystem.depthMask(true)

        RenderSystem.stencilMask(0x00)
        RenderSystem.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    public fun pop(draw: Runnable) {
        currentStencil -= 1

        RenderSystem.depthMask(false)
        RenderSystem.colorMask(false, false, false, false)
        RenderSystem.disableDepthTest()

        // One option here is to set the function to `GL_NEVER` and make the test fail operation `GL_DECR`
        // However, by doing it that way all the fragments are discarded, so we can't debug the mask by setting the
        // color mask in `draw`. By setting the function to `GL_ALWAYS` and disabling depth testing, we're able to
        // enable the color mask in `draw` to make the mask visible.
        RenderSystem.stencilFunc(GL_ALWAYS, 0, 0xFF)
        RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_DECR)
        RenderSystem.stencilMask(0xFF)
        draw.run()

        RenderSystem.enableDepthTest()
        RenderSystem.colorMask(true, true, true, true)
        RenderSystem.depthMask(true)

        RenderSystem.stencilMask(0x00)
        RenderSystem.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }
}
