package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.util.lambdainterfs.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by TheCodeWarrior
 */
inline fun section(name: String, targetSide: Side, code: () -> Unit) {
    if (targetSide == Side.CLIENT) LibrarianLib.PROXY.runIfClient(StartProfiler(name))
    else FMLCommonHandler.instance().minecraftServerInstance.theProfiler.startSection(name)
    code()
    if (targetSide == Side.CLIENT) LibrarianLib.PROXY.runIfClient(EndProfiler)
    else FMLCommonHandler.instance().minecraftServerInstance.theProfiler.endSection()
}

class StartProfiler(val name: String) : ClientRunnable {
    @SideOnly(Side.CLIENT)
    override fun runIfClient() {
        Minecraft.getMinecraft().mcProfiler.startSection(name)
    }
}

object EndProfiler : ClientRunnable {
    @SideOnly(Side.CLIENT)
    override fun runIfClient() {
        Minecraft.getMinecraft().mcProfiler.endSection()
    }
}
