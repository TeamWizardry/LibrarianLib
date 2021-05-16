package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.LibLibModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

internal object LibLibGlitter : LibLibModule("liblib-glitter", "Glitter") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibGlitter.makeLogger<CommonInitializer>()

        override fun onInitialize() {
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibGlitter.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ParticleSystemManager)
            ClientTickEvents.START_CLIENT_TICK.register {
                GlitterLightingCache.tickCache()
                GlitterWorldCollider.tickCaches()
                ParticleSystemManager.tickParticles()
            }
            WorldRenderEvents.LAST.register(ParticleSystemManager)
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibGlitter.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
