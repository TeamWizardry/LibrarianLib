package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

internal object LibLibMosaic : LibLibModule("liblib-mosaic", "Mosaic") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibMosaic.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibMosaic.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(MosaicLoader)
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibMosaic.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
