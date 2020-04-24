package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import dev.thecodewarrior.bitfont.data.Bitfont
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation

object Fonts {
    var classic: Bitfont
        private set
    var unifont: Bitfont
        private set

    init {
        val classicLoc = "librarianlib:facade/fonts/mcclassicplus.bitfont".toRl()
        val unifontLoc = "librarianlib:facade/fonts/unifont.bitfont".toRl()
        Client.resourceReloadHandler.register(object : ISimpleReloadListener<Pair<Bitfont, Bitfont>> {
            override fun prepare(resourceManager: IResourceManager, profiler: IProfiler): Pair<Bitfont, Bitfont> {
                return load(classicLoc) to load(unifontLoc)
            }

            override fun apply(result: Pair<Bitfont, Bitfont>, resourceManager: IResourceManager, profiler: IProfiler) {
                classic = result.first
                unifont = result.second
            }
        })

        classic = load(classicLoc)
        unifont = load(unifontLoc)
    }

    private fun load(fontLocation: ResourceLocation): Bitfont {
        try {
            val bytes = Client.minecraft.resourceManager.getResource(fontLocation).inputStream.readBytes()
            return Bitfont.unpack(bytes)
        } catch(e: Exception) {
            RuntimeException("Error loading $fontLocation", e).printStackTrace()
            return Bitfont("<err>", 10, 4, 9, 6, 2)
        }
    }
}
