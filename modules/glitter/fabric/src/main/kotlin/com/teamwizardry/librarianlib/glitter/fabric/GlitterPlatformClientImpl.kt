package com.teamwizardry.librarianlib.glitter.fabric

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.glitter.ParticleRenderContext
import com.teamwizardry.librarianlib.glitter.platform.GlitterPlatformClient
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

@AutoService(GlitterPlatformClient::class)
internal class GlitterPlatformClientImpl : GlitterPlatformClient {
    override fun registerClientTickEvent(hook: () -> Unit) {
        ClientTickEvents.START_CLIENT_TICK.register { hook() }
    }

    override fun registerRenderWorldEvent(hook: (ParticleRenderContext) -> Unit) {
        WorldRenderEvents.LAST.register { context ->
            hook(ParticleRenderContext(
                context.profiler(),
                context.matrixStack()!!,
                context.camera(),
            ))
        }
    }
}