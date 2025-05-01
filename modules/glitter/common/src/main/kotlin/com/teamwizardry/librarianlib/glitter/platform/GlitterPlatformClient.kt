package com.teamwizardry.librarianlib.glitter.platform

import com.teamwizardry.librarianlib.LibLibInternal
import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import com.teamwizardry.librarianlib.glitter.ParticleRenderContext

@LibLibInternal
public interface GlitterPlatformClient {
    public fun registerClientTickEvent(hook: () -> Unit)
    public fun registerRenderWorldEvent(hook: (ParticleRenderContext) -> Unit)

    public companion object {
        public val instance: GlitterPlatformClient by ServiceLoaderHelper.required()
    }
}