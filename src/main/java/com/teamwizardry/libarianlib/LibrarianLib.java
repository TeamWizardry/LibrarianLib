package com.teamwizardry.libarianlib;

import com.teamwizardry.libarianlib.common.LibCommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.PacketLoggingHandler;
import org.apache.logging.log4j.Logger;

@Mod(modid = LibrarianLib.MODID, version = LibrarianLib.VERSION, name = LibrarianLib.MODNAME, useMetadata = true)
public class LibrarianLib {

    public static final String MODID = "librarianlib";
    public static final String MODNAME = "LibrarianLib";
    public static final String VERSION = "1.0";
    public static final String CLIENT = "com.teamwizardry.librarianlib.client.LibClientProxy";
    public static final String SERVER = "com.teamwizardry.librarianlib.common.LibCommonProxy";
    public static PacketLoggingHandler packetHandler;
    public static Logger logger;
    public static EventBus EVENT_BUS = new EventBus();

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static LibCommonProxy proxy;

    @Mod.Instance
    public static LibrarianLib instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }

}