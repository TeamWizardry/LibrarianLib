package com.teamwizardry.librarianlib.platform

import com.teamwizardry.librarianlib.LibLibInternal
import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import kotlin.getValue

@LibLibInternal
public interface LibLibPlatformCommon {
    public fun registerResourceReloadListener(type: ResourceType, loader: ResourceReloader, identifier: Identifier)

    public companion object {
        public val instance: LibLibPlatformCommon by ServiceLoaderHelper.required()
    }
}