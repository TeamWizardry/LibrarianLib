package com.teamwizardry.librarianlib.test.testcore

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.util.getUnsafe
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by Elad on 1/19/2017.
 */
object UnsafeTest : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {

    }

    override fun init(event: FMLInitializationEvent) {
        FMLInterModComms.sendMessage(LibrarianLib.MODID, "unsafe", "librarianliblate")
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        println("${TestMod.MODID}: ${getUnsafe()}")
    }
}