package com.teamwizardry.librarianlib

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = LateLoader.MODID, version = LibrarianLib.VERSION, name = LateLoader.MODNAME, dependencies = LateLoader.DEPENDENCIES)
class LateLoader {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        LibrarianLib.PROXY.latePre(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        LibrarianLib.PROXY.lateInit(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        LibrarianLib.PROXY.latePost(e)
    }

    companion object {

        const val MODID = "librarianliblate"
        const val MODNAME = "LibrarianLib Stage 2"
        const val DEPENDENCIES = "after:*"
    }

}
