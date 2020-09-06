package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.resources.IResource
import net.minecraft.resources.ResourcePackType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ExistingFileHelper

internal class TextureExistsExistingFileHelper(val wrapped: ExistingFileHelper): ExistingFileHelper(listOf(), false) {

    override fun exists(loc: ResourceLocation, type: ResourcePackType, pathSuffix: String, pathPrefix: String): Boolean {
        if(pathSuffix == ".png")
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