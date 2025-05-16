package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.LibLibInternal
import com.teamwizardry.librarianlib.glitter.platform.GlitterPlatformClient
import com.teamwizardry.librarianlib.platform.LibLibPlatformCommon
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

@LibLibInternal
public object GlitterClientInitializer {
    public fun initializeClient() {
        LibLibPlatformCommon.instance.registerResourceReloadListener(
            ResourceType.CLIENT_RESOURCES,
            ParticleSystemManager,
            Identifier.of("liblib_glitter:particle_system_manager")
        )
        GlitterPlatformClient.instance.registerClientTickEvent {
            GlitterLightingCache.tickCache()
            GlitterWorldCollider.tickCaches()
            ParticleSystemManager.tickParticles()
        }
        GlitterPlatformClient.instance.registerRenderWorldEvent(ParticleSystemManager::renderSystems)
    }
}