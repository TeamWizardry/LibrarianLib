package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.features.utilities.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by TheCodeWarrior
 */
object Profiler {
    @JvmStatic
    fun profile(name: String, targetSide: Side, code: () -> Unit) {
        if (targetSide == Side.CLIENT) ClientRunnable.run {
            Minecraft.getMinecraft().mcProfiler.startSection(name)
        }
        else FMLCommonHandler.instance().minecraftServerInstance.theProfiler.startSection(name)
        code()
        if (targetSide == Side.CLIENT) ClientRunnable.run {
            Minecraft.getMinecraft().mcProfiler.endSection()
        }
        else FMLCommonHandler.instance().minecraftServerInstance.theProfiler.endSection()
    }
}

fun profile(name: String, targetSide: Side, code: () -> Unit) {
    Profiler.profile(name, targetSide, code)
}
