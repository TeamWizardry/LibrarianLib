package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.resources.IResource
import net.minecraft.resources.ResourcePackType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.client.model.generators.ModelProvider

internal class FoundationExistingFileHelper(val wrapped: ExistingFileHelper): ExistingFileHelper(listOf(), false) {
    val modelProviders: MutableList<ModelProvider<*>> = mutableListOf()

    override fun exists(loc: ResourceLocation, type: ResourcePackType, pathSuffix: String, pathPrefix: String): Boolean {
        if(pathSuffix == ".png")
            return true
        if(modelProviders.any { it.generatedModels.containsKey(loc) })
            return true
        return wrapped.exists(loc, type, pathSuffix, pathPrefix)
    }

    override fun getResource(loc: ResourceLocation, type: ResourcePackType, pathSuffix: String, pathPrefix: String): IResource {
        return wrapped.getResource(loc, type, pathSuffix, pathPrefix)
    }

    override fun isEnabled(): Boolean {
        return wrapped.isEnabled
    }
}