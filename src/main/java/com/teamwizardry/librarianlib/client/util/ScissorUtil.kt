package com.teamwizardry.librarianlib.client.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object ScissorUtil {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun updateResolution(event: InitGuiEvent.Pre?) {
        screenScale = ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }

    private var screenScale = -1

    @JvmStatic
    fun push() {
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT)
    }

    @JvmStatic
    fun pop() {
        GL11.glPopAttrib()
    }

    @JvmStatic
    fun enable(): Boolean {
        val wasEnabled = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        return wasEnabled
    }

    @JvmStatic
    fun disable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }

    @JvmStatic
    fun set(left: Int, top: Int, width: Int, height: Int) {
        if (screenScale == -1)
            updateResolution(null)
        GL11.glScissor(left * screenScale, Minecraft.getMinecraft().displayHeight - (top + height) * screenScale,
                width * screenScale, height * screenScale)
    }
}
