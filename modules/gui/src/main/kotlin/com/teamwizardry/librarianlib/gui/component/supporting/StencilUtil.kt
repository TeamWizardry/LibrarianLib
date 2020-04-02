package com.teamwizardry.librarianlib.gui.component.supporting

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11.*

// TODO: https://github.com/MinecraftForge/MinecraftForge/pull/6543
object StencilUtil {
    var currentStencil = 0
        private set

    fun clear() {
        currentStencil = 0

//        glEnable(GL_STENCIL_TEST)
//
//        GlStateManager.stencilFunc(GL_ALWAYS, 0x00, 0x00)
//        GlStateManager.stencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
//        GlStateManager.stencilMask(0xFF)
//        GlStateManager.clearStencil(0)
//        GlStateManager.clear(GL_STENCIL_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC)
    }

    fun resetTest(level: Int) {
        currentStencil = level

//        GlStateManager.stencilMask(0x00)
//        GlStateManager.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun push(draw: Runnable) {
        currentStencil += 1

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

//        GlStateManager.stencilFunc(GL_NEVER, 0, 0xFF)
//        GlStateManager.stencilOp(GL_INCR, GL_KEEP, GL_KEEP)
//
//        GlStateManager.stencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

//        GlStateManager.stencilMask(0x00)
//        GlStateManager.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun pop(draw: Runnable) {
        currentStencil -= 1

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

//        GlStateManager.stencilFunc(GL_NEVER, 0, 0xFF)
//        GlStateManager.stencilOp(GL_DECR, GL_KEEP, GL_KEEP)

//        GlStateManager.stencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

//        GlStateManager.stencilMask(0x00)
//        GlStateManager.stencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    inline fun push(crossinline kotlin: () -> Unit) = push(Runnable { kotlin() })
    inline fun pop(crossinline kotlin: () -> Unit) = pop(Runnable { kotlin() })
}
