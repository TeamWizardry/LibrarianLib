package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.facade.logger
import dev.thecodewarrior.bitfont.data.Bitfont
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.Identifier

public object Fonts {
    public var classic: Bitfont
        private set
    public var unifont: Bitfont
        private set

    init {
        val classicLoc = loc("librarianlib:facade/fonts/mcclassicplus.bitfont")
        val unifontLoc = loc("librarianlib:facade/fonts/unifont.bitfont")
        Client.resourceReloadHandler.register(object: ISimpleReloadListener<Pair<Bitfont, Bitfont>> {
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

    private fun load(fontLocation: Identifier): Bitfont {
        try {
            logger.debug("Loading Bitfont font $fontLocation")
            val bytes = Client.minecraft.resourceManager.getResource(fontLocation).inputStream
            val font = Bitfont.unpack(bytes)
            logger.debug("Finished loading font")
            return font
        } catch (e: Exception) {
            RuntimeException("Error loading $fontLocation", e).printStackTrace()
            return Bitfont("<err>", 10, 4, 9, 6, 2)
        }
    }
}
