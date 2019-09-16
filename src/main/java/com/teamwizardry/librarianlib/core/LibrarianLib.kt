package com.teamwizardry.librarianlib.core

import com.teamwizardry.librarianlib.core.common.LibCommonProxy
import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.config.EasyConfigHandler
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.hud.GuiHud
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facadecontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.tagCompound
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.structure.Structure
import com.teamwizardry.librarianlib.features.utilities.LoggerBase
import com.teamwizardry.librarianlib.features.utilities.client.F3Handler
import com.teamwizardry.librarianlib.features.utilities.getUnsafe
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent

/**
 * Out of date feature set:
 *
 * - Automatically generate missing models in development environments
 * - Automatically register Item colors, models, statemappers, and all other model-related things [ItemMod]
 * - Automatically sync tileentity fields and packet fields marked with @[Save]
 * - An easy—if slightly complicated—GUI creation framework [GuiComponent] [GuiBase] [GuiHud]
 * - A flexible container framework based on the GUI component system [ContainerBase] [GuiContainerBase]
 * - A highly customizable and easy to use particle system [ParticleBuilder] [ParticleSpawner]
 * - Automatic registration of TileEntities @[TileRegister]
 * - Unsafe extensions and reflection-free access [getUnsafe] (UnsafeKt.getUnsafeSafely in java)
 * - JSON and NBT groovy-style builders [jsonObject] [tagCompound] (JsonMaker and NBTMaker)
 * - Method Handle helpers and delegates [MethodHandleHelper]
 * - Helper class to check what mod owns a class [OwnershipHandler]
 * - Base classes for a variety of situations, for example: [Vec2d] [Matrix4] [Event]
 * - Structure API [Structure]
 * - Reliable and simple packet handler also using [Save] [PacketBase]
 * - Config Property system that is completely painless and only requires very minimal registration: [EasyConfigHandler]
 * - Container API [ContainerBase]
 * - An easy F3+key handler [F3Handler]
 * - A simple event bus implementation [Event] [EventBus]
 * - Capability which uses the [Save] scheme to save and sync fields [CapabilityMod]
 */
@Mod(modid = LibrarianLib.MODID, version = LibrarianLib.VERSION, name = LibrarianLib.MODNAME, dependencies = LibrarianLib.DEPENDENCIES, modLanguageAdapter = LibrarianLib.ADAPTER, acceptedMinecraftVersions = LibrarianLib.ALLOWED)
object LibrarianLib {

    init {
        FluidRegistry.enableUniversalBucket()
    }

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

    @Mod.EventHandler
    fun serverStarting(e: FMLServerStartingEvent) {
        PROXY.serverStarting(e)
    }

    const val MODID = "librarianlib"
    const val MODNAME = "LibrarianLib"
    const val MAJOR = "GRADLE:VERSION"
    const val MINOR = "GRADLE:BUILD"
    const val VERSION = "$MAJOR.$MINOR"
    const val ALLOWED = "[1.12,)"
    const val CLIENT = "com.teamwizardry.librarianlib.core.client.LibClientProxy"
    const val SERVER = "com.teamwizardry.librarianlib.core.common.LibCommonProxy"
    const val DEPENDENCIES = "required-after:forgelin@[1.8.0,);required-after:forge@[13.19.1.2195,)"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var PROXY: LibCommonProxy

    @JvmField
    val DEV_ENVIRONMENT = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
}

object LibrarianLog : LoggerBase("LibrarianLib")
