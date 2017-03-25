@file:JvmName("ClientUtilMethods")

package com.teamwizardry.librarianlib.client.util

import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

private class LibClientExtensions // so we can jump to this file with "go to class"

// Color ===============================================================================================================

@SideOnly(Side.CLIENT)
fun Color.glColor() = GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
