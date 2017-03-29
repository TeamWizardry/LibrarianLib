@file:JvmName("ClientUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

// Color ===============================================================================================================

@SideOnly(Side.CLIENT)
fun Color.glColor() = GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)

// VertexBuffer ========================================================================================================

fun VertexBuffer.pos(pos: Vec3d): VertexBuffer = this.pos(pos.xCoord, pos.yCoord, pos.zCoord)

fun VertexBuffer.color(color: Color): VertexBuffer = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
