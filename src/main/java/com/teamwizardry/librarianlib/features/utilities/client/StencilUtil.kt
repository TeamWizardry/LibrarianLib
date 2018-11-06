package com.teamwizardry.librarianlib.features.utilities.client

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

/**
 * TODO: Document file StencilUtil
 *
 * Created by TheCodeWarrior
 */
object StencilUtil {
    var currentStencil = 0
        private set

    fun clear() {
        glEnable(GL_STENCIL_TEST)
        currentStencil = 0

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_ALWAYS, 0, 0xFF)
        glStencilOp(GL_ZERO, GL_ZERO, GL_ZERO)

        glStencilMask(0xFF)

        GlStateManager.disableTexture2D()
        GlStateManager.color(1f, 0f, 1f, 0.5f)

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        val s = 100_000.0

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        vb.pos(-s, -s, 0.0).endVertex()
        vb.pos( s, -s, 0.0).endVertex()
        vb.pos( s,  s, 0.0).endVertex()
        vb.pos(-s,  s, 0.0).endVertex()

        tessellator.draw()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        glDisable(GL_STENCIL_TEST)
    }

    @JvmStatic
    fun push(draw: Runnable) {
        currentStencil += 1

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_NEVER, 0, 0xFF)
        glStencilOp(GL_INCR, GL_KEEP, GL_KEEP)

        glStencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun pop(draw: Runnable) {
        currentStencil -= 1

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_NEVER, 0, 0xFF)
        glStencilOp(GL_DECR, GL_KEEP, GL_KEEP)

        glStencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    fun push(kotlin: () -> Unit) = push(Runnable(kotlin))
    fun pop(kotlin: () -> Unit) = pop(Runnable(kotlin))
}
