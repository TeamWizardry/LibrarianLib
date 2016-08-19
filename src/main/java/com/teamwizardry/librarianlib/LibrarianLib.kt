package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.common.core.Config
import com.teamwizardry.librarianlib.client.gui.TickCounter
import com.teamwizardry.librarianlib.common.core.LibCommonProxy
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.core.LoggerBase
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.PacketLoggingHandler
import org.apache.logging.log4j.Logger

@Mod(modid = LibrarianLib.MODID, version = LibrarianLib.VERSION, name = LibrarianLib.MODNAME, useMetadata = true)
class LibrarianLib {

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        Config.initConfig(event.suggestedConfigurationFile)
        proxy.preInit()
        javaClass // load the class
        MinecraftForge.EVENT_BUS.register(TickCounter())
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
    }

    companion object {

        const val MODID = "librarianlib"
        const val MODNAME = "LibrarianLib"
        const val VERSION = "1.0"
        const val CLIENT = "com.teamwizardry.librarianlib.client.core.LibClientProxy"
        const val SERVER = "com.teamwizardry.librarianlib.common.core.LibCommonProxy"
        var packetHandler: PacketLoggingHandler? = null
        lateinit var logger: Logger

        @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
        lateinit var proxy: LibCommonProxy

        @Mod.Instance
        lateinit var instance: LibrarianLib
        lateinit var guide: Book // won't be initialized on the server, and will scream if you try to access it.
    }

}

object LibrarianLog : LoggerBase("LibrarianLib")
