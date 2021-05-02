package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

internal object LibLibMosaicClient : ClientModInitializer {
    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(MosaicLoader)
    }
}

internal object LibLibMosaic : LibLibModule("liblib-mosaic", "Mosaic")
