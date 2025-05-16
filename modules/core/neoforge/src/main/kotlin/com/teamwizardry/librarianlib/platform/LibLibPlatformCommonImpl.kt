package com.teamwizardry.librarianlib.platform

import com.google.auto.service.AutoService
import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ReloadableResourceManagerImpl
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@AutoService(LibLibPlatformCommon::class)
internal class LibLibPlatformCommonImpl : LibLibPlatformCommon {
    override fun registerResourceReloadListener(type: ResourceType, loader: ResourceReloader, identifier: Identifier) {
        when (type) {
            ResourceType.CLIENT_RESOURCES -> ClientReloadListenerManager.registerClient(loader)
            ResourceType.SERVER_DATA -> ServerReloadListenerManager.registerServer(loader)
        }
    }
}

@OnlyIn(Dist.CLIENT)
internal object ClientReloadListenerManager {
    fun registerClient(listener: ResourceReloader) {
        (MinecraftClient.getInstance().resourceManager as ReloadableResourceManagerImpl).registerReloader(listener)
    }
}

internal object ServerReloadListenerManager {
    val resourceReloaders = mutableListOf<ResourceReloader>()

    fun registerServer(listener: ResourceReloader) {
        resourceReloaders.add(listener)
    }
}
