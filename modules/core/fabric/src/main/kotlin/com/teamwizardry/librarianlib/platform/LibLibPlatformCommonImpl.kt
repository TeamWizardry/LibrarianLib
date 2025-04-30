package com.teamwizardry.librarianlib.platform

import com.google.auto.service.AutoService
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@AutoService(LibLibPlatformCommon::class)
internal class LibLibPlatformCommonImpl : LibLibPlatformCommon {
    override fun registerResourceReloadListener(type: ResourceType, loader: ResourceReloader, identifier: Identifier) {
        ResourceManagerHelper.get(type).registerReloadListener(object : IdentifiableResourceReloadListener {
            override fun getFabricId(): Identifier = identifier

            override fun reload(
                synchronizer: ResourceReloader.Synchronizer,
                manager: ResourceManager,
                prepareProfiler: Profiler,
                applyProfiler: Profiler,
                prepareExecutor: Executor,
                applyExecutor: Executor
            ): CompletableFuture<Void> {
                return loader.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor)
            }
        });
    }
}