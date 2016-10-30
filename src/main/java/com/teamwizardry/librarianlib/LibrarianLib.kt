package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.common.core.LibCommonProxy
import com.teamwizardry.librarianlib.common.core.LoggerBase
import com.teamwizardry.librarianlib.common.util.bitsaving.BitwiseStorageManager
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Referencing any LibrarianLib class from a static context or as a field in dependant [@Mod] files or proxies will crash.
 * This is because LibrarianLib loads after dependant mods. To fix this, reference all LibLib classes in separate classes,
 * and/or don't initialize anything related to LibLib before preinit.
 */
@Mod(modid = LibrarianLib.MODID, version = LibrarianLib.VERSION, name = LibrarianLib.MODNAME, dependencies = LibrarianLib.DEPENDENCIES, useMetadata = true)
class LibrarianLib {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        PROXY.pre(e)
        BitwiseStorageManager
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

        const val MODID = "librarianlib"
        const val MODNAME = "LibrarianLib"
        const val VERSION = "1.2"
        const val CLIENT = "com.teamwizardry.librarianlib.client.core.LibClientProxy"
        const val SERVER = "com.teamwizardry.librarianlib.common.core.LibCommonProxy"
        const val DEPENDENCIES = "after:*"

        @JvmStatic
        @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
        lateinit var PROXY: LibCommonProxy

        @JvmField
        val DEV_ENVIRONMENT = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
    }

}

object LibrarianLog : LoggerBase("LibrarianLib")
