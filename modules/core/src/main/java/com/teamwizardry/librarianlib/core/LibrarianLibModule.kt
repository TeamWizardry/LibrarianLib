package com.teamwizardry.librarianlib.core

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.Logger

abstract class LibrarianLibModule(val name: String, val logger: Logger) {
    val modid: String = "librarianlib-$name"

    init {
        register(this)

        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> {
            this.setup(it)
        }
        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLClientSetupEvent> {
            this.clientSetup(it)
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        FMLJavaModLoadingContext.get().modEventBus.register(this)
    }

    protected open fun setup(event: FMLCommonSetupEvent) {
    }

    @OnlyIn(Dist.CLIENT)
    protected open fun clientSetup(event: FMLClientSetupEvent) {
    }

    protected open fun registerBlocks(blockRegistryEvent: RegistryEvent.Register<Block>) {
    }

    protected open fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
    }

    protected open fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
    }

    @SubscribeEvent
    internal fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
        registerBlocks(blockRegistryEvent)
    }

    @SubscribeEvent
    internal fun onItemRegister(itemRegistryEvent: RegistryEvent.Register<Item>) {
        registerItems(itemRegistryEvent)
    }

    @SubscribeEvent
    internal fun onEntityRegister(entityRegistryEvent: RegistryEvent.Register<@JvmSuppressWildcards EntityType<*>>) {
        registerEntities(entityRegistryEvent)
    }

    companion object {
        private val _modules = mutableListOf<LibrarianLibModule>()

        val modules: List<LibrarianLibModule> = _modules.unmodifiableView()

        private fun register(module: LibrarianLibModule) {
            _modules.add(module)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T: LibrarianLibModule> findByName(name: String): T? {
            return modules.find { it.name == name } as T?
        }

        @Suppress("UNCHECKED_CAST")
        fun <T: LibrarianLibModule> findByModId(modid: String): T? {
            return modules.find { it.modid == modid } as T?
        }

        inline fun <reified T: LibrarianLibModule> get(): T {
            return find<T>()
                ?: error("Couldn't find a LibrarianLib module with the type ${T::class.simpleName}. " +
                    "Maybe it hasn't loaded yet?")
        }

        inline fun <reified T: LibrarianLibModule> find(): T? {
            return modules.find { it is T } as T?
        }

        inline fun <reified T: LibrarianLibModule> current(): T {
            val currentModId = ModLoadingContext.get().activeContainer.modId
            return findByModId(currentModId)
                ?: error("The currently loading mod `$currentModId` is not a LibrarianLib Module")
        }
    }
}
