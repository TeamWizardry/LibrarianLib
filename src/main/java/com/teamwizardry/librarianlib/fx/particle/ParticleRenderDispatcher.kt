package com.teamwizardry.librarianlib.fx.particle

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.profiler.Profiler
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import java.util.ArrayList

enum class ParticleRenderDispatcher private constructor() {
    INSTANCE;

    internal var queues: MutableList<ParticleRenderQueue<*>> = ArrayList()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun addQueue(queue: ParticleRenderQueue<*>) {
        queues.add(queue)
    }

    @SubscribeEvent
    fun renderWorldLast(event: RenderWorldLastEvent) {
        val profiler = Minecraft.getMinecraft().mcProfiler

        profiler.startSection("wizardry-fx")

        for (queue in queues) {
            profiler.startSection(queue.name())
            queue.dispatchQueuedRenders(Tessellator.getInstance())
            profiler.endSection()
        }

        profiler.endSection()

    }

}
