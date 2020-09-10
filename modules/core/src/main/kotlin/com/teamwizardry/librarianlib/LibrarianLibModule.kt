package com.teamwizardry.librarianlib

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator

public abstract class LibrarianLibModule(public val name: String, public val humanName: String) {
    protected val modEventBus: IEventBus = FMLKotlinModLoadingContext.get().modEventBus

    /**
     * Whether debugging is enabled for this module.
     */
    public var debugEnabled: Boolean = false
        private set

    /**
     * Loggers to update based on the debug flag.
     */
    private val registeredLoggers = mutableListOf<Logger>()
    private val modLoggers = mutableMapOf<String?, Logger>()

    init {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        modEventBus.register(this)
    }

    public fun enableDebugging() {
        debugEnabled = true

        modLoggers.forEach { (_, logger) ->
            Configurator.setLevel(logger.name, Level.DEBUG)
        }
    }

    /**
     * Create a logger for this module.
     */
    public fun makeLogger(clazz: Class<*>): Logger {
        return makeLogger(clazz.simpleName)
    }

    /**
     * Create a logger for this module.
     */
    public inline fun <reified T> makeLogger(): Logger {
        return makeLogger(T::class.java)
    }

    /**
     * Create a logger for this module.
     */
    public fun makeLogger(label: String?): Logger {
        return modLoggers.getOrPut(label) {
            val labelSuffix = label?.let { " ($it)" } ?: ""
            val logger = LogManager.getLogger("LibrarianLib: $humanName$labelSuffix")
            registerLogger(logger)
            logger
        }
    }

    /**
     * Registers a logger which should be controlled by this module's debug flag
     */
    public fun registerLogger(logger: Logger) {
        if(debugEnabled)
            Configurator.setLevel(logger.name, Level.DEBUG)
        else
            Configurator.setLevel(logger.name, Level.INFO)
        registeredLoggers.add(logger)
    }

    @SubscribeEvent
    protected open fun setup(event: FMLCommonSetupEvent) {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    protected open fun clientSetup(event: FMLClientSetupEvent) {
    }

    protected open fun registerBlocks(blockRegistryEvent: RegistryEvent.Register<Block>) {
    }

    protected open fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
    }

    protected open fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
        registerBlocks(blockRegistryEvent)
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun onItemRegister(itemRegistryEvent: RegistryEvent.Register<Item>) {
        registerItems(itemRegistryEvent)
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun onEntityRegister(entityRegistryEvent: RegistryEvent.Register<@JvmSuppressWildcards EntityType<*>>) {
        registerEntities(entityRegistryEvent)
    }
}
