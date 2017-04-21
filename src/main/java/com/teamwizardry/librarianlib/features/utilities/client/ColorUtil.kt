@file:SideOnly(Side.CLIENT)
@file:JvmName("ColorUtils")

package com.teamwizardry.librarianlib.features.utilities.client

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

@JvmName("pulseColor")
@JvmOverloads
fun Color.pulseColor(variance: Int = 24, pulseSpeed: Float = 0.2f): Color {
    val add = (MathHelper.sin(ClientTickHandler.ticksInGame * pulseSpeed) * variance).toInt()
    val newColor = Color(
            Math.max(Math.min(red + add, 255), 0),
            Math.max(Math.min(green + add, 255), 0),
            Math.max(Math.min(blue + add, 255), 0))
    return newColor
}

fun rainbow(saturation: Float) = Color(Color.HSBtoRGB(((ClientTickHandler.ticksInGame * 2L) % 360L).toFloat() / 360.0f, saturation, 1.0f))

fun rainbow(speed: Float, saturation: Float): Color {
    val time = ClientTickHandler.ticksInGame.toFloat() + ClientTickHandler.partialTicks
    return Color.getHSBColor(time * speed, saturation, 1.0f)
}

fun rainbow(pos: BlockPos, saturation: Float): Color {
    val ticks = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks
    val seed = (pos.x xor pos.y xor pos.z) * 255 xor pos.hashCode()
    return Color(Color.HSBtoRGB((seed + ticks) * 0.005F, saturation, 1F))
}
