package com.teamwizardry.librarianlib.glitter.neoforge

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.glitter.ParticleRenderContext
import com.teamwizardry.librarianlib.glitter.platform.GlitterPlatformClient
import net.minecraft.client.MinecraftClient
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

@AutoService(GlitterPlatformClient::class)
internal class GlitterPlatformClientImpl : GlitterPlatformClient {
    override fun registerClientTickEvent(hook: () -> Unit) {
        GlitterClientEvents.tickEvents.add(hook)
    }

    override fun registerRenderWorldEvent(hook: (ParticleRenderContext) -> Unit) {
        GlitterClientEvents.renderWorldEvents.add(hook)
    }
}

internal object GlitterClientEvents {
    val tickEvents = mutableListOf<() -> Unit>()
    val renderWorldEvents = mutableListOf<(ParticleRenderContext) -> Unit>()

    @SubscribeEvent
    fun clientTick(event: ClientTickEvent.Pre) {
        tickEvents.forEach { it() }
    }

    @SubscribeEvent
    fun worldRenderLast(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            val context = ParticleRenderContext(
                profiler = MinecraftClient.getInstance().profiler,
                matrixStack = event.poseStack,
                camera = event.camera,
            )
            renderWorldEvents.forEach { it(context) }
        }
    }
}