package com.teamwizardry.librarianlib.client.util

import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

// Color ===============================================================================================================

@SideOnly(Side.CLIENT)
fun Color.glColor() = GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
