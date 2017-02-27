package com.teamwizardry.librarianlib.client.util

import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
}