package com.teamwizardry.librarianlib.mirage

import net.minecraft.resources.*
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraft.util.ResourceLocation
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.function.Predicate

public object Mirage {
    @JvmField
    public val clientResources: VirtualResourceManager = VirtualResourceManager(ResourcePackType.CLIENT_RESOURCES)

    @JvmField
    public val serverData: VirtualResourceManager = VirtualResourceManager(ResourcePackType.SERVER_DATA)

    @JvmStatic
    public fun resourceManager(type: ResourcePackType): VirtualResourceManager = when (type) {
        ResourcePackType.CLIENT_RESOURCES -> clientResources
        ResourcePackType.SERVER_DATA -> serverData
    }

    @JvmField
    public val languageMap: VirtualLanguageMap = VirtualLanguageMap()
}