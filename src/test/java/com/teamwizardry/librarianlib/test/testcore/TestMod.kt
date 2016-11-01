package com.teamwizardry.librarianlib.test.testcore

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.core.LoggerBase
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
@Mod(modid = LibrarianLib.MODID, version = LibrarianLib.VERSION, name = LibrarianLib.MODNAME, dependencies = LibrarianLib.DEPENDENCIES, useMetadata = true)
class TestMod {
    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        PROXY.pre(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        PROXY.post(e)
    }

    companion object {

        const val MODID = "librarianlibtest"
        const val MODNAME = "LibrarianLib Test"
        const val VERSION = "0.0"
        const val CLIENT = "com.teamwizardry.librarianlib.test.testcore.LibTestClientProxy"
        const val SERVER = "com.teamwizardry.librarianlib.test.testcore.LibTestCommonProxy"
        const val DEPENDENCIES = "before:librarianlib"

        @JvmStatic
        @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
        lateinit var PROXY: LibTestCommonProxy
    }

}

object TestLog : LoggerBase("LibrarianLibTest")
