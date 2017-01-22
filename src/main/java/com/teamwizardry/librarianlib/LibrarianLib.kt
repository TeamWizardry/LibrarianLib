package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner
import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.GuiOverlay
import com.teamwizardry.librarianlib.client.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.client.util.F3Handler
import com.teamwizardry.librarianlib.common.base.ModAchievement
import com.teamwizardry.librarianlib.common.base.multipart.PartMod
import com.teamwizardry.librarianlib.common.container.ContainerBase
import com.teamwizardry.librarianlib.common.core.LibCommonProxy
import com.teamwizardry.librarianlib.common.core.LoggerBase
import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.structure.Structure
import com.teamwizardry.librarianlib.common.util.ConfigPropertyInt
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.event.EventBus
import com.teamwizardry.librarianlib.common.util.getUnsafe
import com.teamwizardry.librarianlib.common.util.math.Matrix4
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent


/**
 * Current featureset:
 *
 * - Automatically generate missing models in development environments
 * - Automatically register Item colors, models, statemappers, and all other model-related things
 * - Automatically sync tileentity fields and packet fields marked with @[Save]
 * - An easy—if slightly complicated—GUI creation framework [GuiComponent] [GuiBase] [GuiOverlay]
 * - A flexable container framework based on the GUI component system [ContainerBase] [GuiContainerBase]
 * - A highly customizable and easy to use particle system [ParticleBuilder] [ParticleSpawner]
 * - Automatic registration of TileEntities @[TileRegister]
 * - Unsafe extensions and reflection-free access [getUnsafe] (UnsafeKt.getUnsafe in java)
 * - JSON and NBT groovy-style builders
 * - Method Handle helpers and delegates [MethodHandleHelper]
 * - Helper class to check what mod owns a class [OwnershipHandler]
 * - Base classes for a variety of situations, for example: [Vec2d] [Matrix4] [Event] [ModAchievement]
 * - Structure API [Structure]
 * - Reliable and simple packet handler also using [Save] [PacketBase]
 * - Config Property system that is completely painless and only requires very minimal registration: [ConfigPropertyInt]
 * - Container API [ContainerBase]
 * - Multipart API [PartMod]
 * - An easy F3+key handler [F3Handler]
 * - A simple event bus implementation [Event] [EventBus]
 */
@Mod(modid = LibrarianLib.MODID, version = LibrarianLib.VERSION, name = LibrarianLib.MODNAME, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object LibrarianLib {

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


    const val MODID = "librarianlib"
    const val MODNAME = "LibrarianLib"
    const val VERSION = "1.7"
    const val CLIENT = "com.teamwizardry.librarianlib.client.core.LibClientProxy"
    const val SERVER = "com.teamwizardry.librarianlib.common.core.LibCommonProxy"

    @JvmStatic
    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var PROXY: LibCommonProxy

    @JvmField
    val DEV_ENVIRONMENT = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

    val isClient: Boolean
        get() = PROXY.isClient
    val isDedicatedServer: Boolean
        get() = PROXY.isDedicatedServer
    internal val unsafeAllowedModIds = mutableListOf<String>()

    @Mod.EventHandler
    fun onImcMessage(e: FMLInterModComms.IMCEvent) {
        val modids = e.messages.filter { it.key.toLowerCase() == "unsafe" }.map { it.stringValue }
        if(DEV_ENVIRONMENT && modids.isNotEmpty()) {
            println(MODID + " | Unsafe-allowed mod IDs:")
            modids.forEach { " ".repeat(MODID.length) + " | $it" }
        }
        unsafeAllowedModIds.addAll(modids)
    }
}

object LibrarianLog : LoggerBase("LibrarianLib")
