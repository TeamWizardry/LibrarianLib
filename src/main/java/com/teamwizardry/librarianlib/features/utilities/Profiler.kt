package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.FMLCommonHandler

/**
 * Created by TheCodeWarrior
 */
object Profiler {
    @JvmStatic
    inline fun profile(name: String, code: () -> Unit) {
        ClientRunnable.run { Minecraft.getMinecraft().mcProfiler.startSection(name) }
        FMLCommonHandler.instance().minecraftServerInstance.profiler.startSection(name)
        code()
        ClientRunnable.run { Minecraft.getMinecraft().mcProfiler.endSection() }
        FMLCommonHandler.instance().minecraftServerInstance.profiler.endSection()
    }
}

inline fun profile(name: String, code: () -> Unit) {
    Profiler.profile(name, code)
}
