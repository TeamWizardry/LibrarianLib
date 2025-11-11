package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.facade.LibLibFacade
import dev.thecodewarrior.bitfont.data.Bitfont
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

public object Fonts : ResourceReloader {
    public lateinit var classic: Bitfont
        private set
    public lateinit var unifont: Bitfont
        private set

    override fun reload(
        synchronizer: ResourceReloader.Synchronizer,
        manager: ResourceManager,
        prepareProfiler: Profiler,
        applyProfiler: Profiler,
        prepareExecutor: Executor,
        applyExecutor: Executor
    ): CompletableFuture<Void> {
        val classicLoc = Identifier.of("liblib_facade:fonts/mcclassicplus.bitfont")
        val unifontLoc = Identifier.of("liblib_facade:fonts/unifont.bitfont")

        return CompletableFuture.supplyAsync({
            load(manager, classicLoc) to load(manager, unifontLoc)
        }, prepareExecutor)
            .thenCompose(synchronizer::whenPrepared)
            .thenAccept {
                classic = it.first
                unifont = it.second
            }
    }

    private fun load(manager: ResourceManager, fontLocation: Identifier): Bitfont {
        try {
            logger.debug("Loading Bitfont font $fontLocation")
            val bytes = manager.getResourceOrThrow(fontLocation).inputStream
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
