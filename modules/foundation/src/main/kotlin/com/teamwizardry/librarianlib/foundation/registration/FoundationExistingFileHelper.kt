package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.resources.IResource
import net.minecraft.resources.ResourcePackType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

internal class FoundationExistingFileHelper(val wrapped: ExistingFileHelper): ExistingFileHelper(listOf(), setOf(), false) {
    val modelProviders: MutableList<ModelProvider<*>> = mutableListOf()

    override fun exists(loc: ResourceLocation?, packType: ResourcePackType?): Boolean {
        return wrapped.exists(loc, packType)
    }

    override fun exists(loc: ResourceLocation?, type: IResourceType?): Boolean {
        if(type?.suffix == ".png")
            return true
        return wrapped.exists(loc, type)
    }

    override fun exists(loc: ResourceLocation, type: ResourcePackType, pathSuffix: String, pathPrefix: String): Boolean {
        if(pathSuffix == ".png")
            return true
        if(modelProviders.any { it.generatedModels.containsKey(loc) })
            return true
        return wrapped.exists(loc, type, pathSuffix, pathPrefix)
    }

    override fun trackGenerated(loc: ResourceLocation?, type: IResourceType?) {
        wrapped.trackGenerated(loc, type)
    }

    override fun trackGenerated(
        loc: ResourceLocation?,
        packType: ResourcePackType?,
        pathSuffix: String?,
        pathPrefix: String?
    ) {
        wrapped.trackGenerated(loc, packType, pathSuffix, pathPrefix)
    }

    override fun getResource(
        loc: ResourceLocation?,
        packType: ResourcePackType?,
        pathSuffix: String?,
        pathPrefix: String?
    ): IResource {
        return wrapped.getResource(loc, packType, pathSuffix, pathPrefix)
    }

    override fun getResource(loc: ResourceLocation?, packType: ResourcePackType?): IResource {
        return wrapped.getResource(loc, packType)
    }

    override fun isEnabled(): Boolean {
        return wrapped.isEnabled()
    }
}