package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.LibLibFacade
import dev.thecodewarrior.bitfont.data.Bitfont
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

public object Fonts {
    public var classic: Bitfont
        private set
    public var unifont: Bitfont
        private set

    init {
        val classicLoc = Identifier("liblib-facade:fonts/mcclassicplus.bitfont")
        val unifontLoc = Identifier("liblib-facade:fonts/unifont.bitfont")
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(object: SimpleResourceReloadListener<Pair<Bitfont, Bitfont>> {
            override fun getFabricId(): Identifier = Identifier("liblib-facade:bitfont-fonts")

            override fun load(
                manager: ResourceManager,
                profiler: Profiler,
                executor: Executor
            ): CompletableFuture<Pair<Bitfont, Bitfont>> {
                return CompletableFuture.supplyAsync {
                    load(manager, classicLoc) to load(manager, unifontLoc)
                }
            }

            override fun apply(
                data: Pair<Bitfont, Bitfont>,
                manager: ResourceManager,
                profiler: Profiler,
                executor: Executor
            ): CompletableFuture<Void> {
                return CompletableFuture.runAsync {
                    classic = data.first
                    unifont = data.second
                }
            }
        })

        classic = load(Client.minecraft.resourceManager, classicLoc)
        unifont = load(Client.minecraft.resourceManager, unifontLoc)
    }

    private fun load(manager: ResourceManager, fontLocation: Identifier): Bitfont {
        try {
            logger.debug("Loading Bitfont font $fontLocation")
            val bytes = manager.getResource(fontLocation).inputStream
            val font = Bitfont.unpack(bytes)
            logger.debug("Finished loading font")
            return font
        } catch (e: Exception) {
            RuntimeException("Error loading $fontLocation", e).printStackTrace()
            return Bitfont("<err>", 10, 4, 9, 6, 2)
        }
    }

    private val logger = LibLibFacade.makeLogger<Fonts>()
}
