package com.teamwizardry.librarianlib.features.utilities.client

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import scala.inline

/**
 * Created by Elad on 2/7/2017.
 */
@SideOnly(Side.CLIENT)
object GlUtils {
    inline fun pushAttrib(lambda: () -> Unit) {
        GlStateManager.pushAttrib()
        lambda()
        GlStateManager.popAttrib()
    }

    inline fun pushMatrix(lambda: () -> Unit) {
        GlStateManager.pushMatrix()
        lambda()
        GlStateManager.popMatrix()
    }

    inline fun useLightmap(x: Float, y: Float, lambda: () -> Unit) {
        val prevX = OpenGlHelper.lastBrightnessX
        val prevY = OpenGlHelper.lastBrightnessY
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, x, y)
        lambda()
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY)
    }


    inline fun useLightmap(packed: Int, lambda: () -> Unit) {
        val mask = 1 shl 16
        val x = (packed % mask).toFloat()
        val y = (packed / mask).toFloat()

        useLightmap(x, y, lambda)
    }

    inline fun withLighting(state: Boolean, lambda: () -> Unit) {
        val prevState = GL11.glIsEnabled(GL11.GL_LIGHTING)
        if (state) GlStateManager.enableLighting() else GlStateManager.disableLighting()
        lambda()
        if (prevState) GlStateManager.enableLighting() else GlStateManager.disableLighting()
    }
}
