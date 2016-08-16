package com.teamwizardry.librarianlib.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

class ScissorUtil private constructor() {

    @SubscribeEvent
    fun updateResolution(event: InitGuiEvent.Pre?) {
        screenScale = ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }

    companion object {
        val INSTANCE = ScissorUtil()
        private var screenScale = -1

        fun push() {
            GL11.glPushAttrib(GL11.GL_SCISSOR_BIT)
        }

        fun pop() {
            GL11.glPopAttrib()
        }

        fun enable(): Boolean {
            val wasEnabled = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST)
            GL11.glEnable(GL11.GL_SCISSOR_TEST)
            return wasEnabled
        }

        fun disable() {
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
        }

        fun set(left: Int, top: Int, width: Int, height: Int) {
            if (screenScale == -1)
                INSTANCE.updateResolution(null)
            GL11.glScissor(left * screenScale, Minecraft.getMinecraft().displayHeight - (top + height) * screenScale,
                    width * screenScale, height * screenScale)
        }
    }

}
