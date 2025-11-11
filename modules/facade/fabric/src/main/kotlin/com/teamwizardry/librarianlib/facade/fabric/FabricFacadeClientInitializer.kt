package com.teamwizardry.librarianlib.facade.fabric

import com.teamwizardry.librarianlib.facade.FacadeClientInitializer
import net.fabricmc.api.ClientModInitializer

internal object FabricFacadeClientInitializer : ClientModInitializer {
    override fun onInitializeClient() {
        FacadeClientInitializer.initializeClient()
    }
}
