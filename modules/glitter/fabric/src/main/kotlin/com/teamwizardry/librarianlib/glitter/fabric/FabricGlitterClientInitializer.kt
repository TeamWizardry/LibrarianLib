package com.teamwizardry.librarianlib.glitter.fabric

import com.teamwizardry.librarianlib.glitter.GlitterClientInitializer
import net.fabricmc.api.ClientModInitializer

internal object FabricGlitterClientInitializer : ClientModInitializer {
    override fun onInitializeClient() {
        GlitterClientInitializer.initializeClient()
    }
}
