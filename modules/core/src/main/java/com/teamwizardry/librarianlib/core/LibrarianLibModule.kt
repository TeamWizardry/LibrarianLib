package com.teamwizardry.librarianlib.core

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.Logger

abstract class LibrarianLibModule(val name: String, val logger: Logger) {
    val modid: String = "librarianlib-$name"

    init {
        register(modid, this)

        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> {
            this.setup(it)
        }
        FMLJavaModLoadingContext.get().modEventBus.addListener<FMLClientSetupEvent> {
            this.clientSetup(it)
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
    }

    protected fun setup(event: FMLCommonSetupEvent) {
    }

    @OnlyIn(Dist.CLIENT)
    protected fun clientSetup(event: FMLClientSetupEvent) {
    }

    companion object {
        private val _modules = mutableMapOf<String, LibrarianLibModule>()

        val modules: Map<String, LibrarianLibModule> = _modules.unmodifiableView()

        private fun register(modid: String, module: LibrarianLibModule) {
            _modules[modid] = module
        }
    }
}
