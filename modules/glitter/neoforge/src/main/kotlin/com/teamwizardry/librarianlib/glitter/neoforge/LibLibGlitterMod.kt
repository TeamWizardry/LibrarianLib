package com.teamwizardry.librarianlib.glitter.neoforge

import com.teamwizardry.librarianlib.glitter.GlitterClientInitializer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge

@Mod(value = "liblib_glitter", dist = [Dist.CLIENT])
internal class LibLibGlitterMod {
    init {
        GlitterClientInitializer.initializeClient()
        NeoForge.EVENT_BUS.register(GlitterClientEvents)
    }
}
