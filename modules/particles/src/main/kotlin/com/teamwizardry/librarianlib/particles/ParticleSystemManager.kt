package com.teamwizardry.librarianlib.particles

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Vector3f
import net.minecraft.client.renderer.Vector4f
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.ConcurrentModificationException

/**
 * This object is responsible for the rendering and updating of particle systems, and is where new particle systems
 * are sent to be rendered and ticked.
 */
@Mod.EventBusSubscriber(value = [Dist.CLIENT])
internal object ParticleSystemManager: ISimpleReloadListener<Unit> {

    val systems: MutableList<ParticleSystem> = mutableListOf()

    init {
        Client.resourceReloadHandler.register(this)
    }

    fun add(system: ParticleSystem) {
        if(!systems.contains(system)) {
            systems.add(system)
        }
    }

    fun remove(system: ParticleSystem) {
        systems.remove(system)
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START)
            return
        if (Minecraft.getInstance().currentScreen?.isPauseScreen == true)
            return
        if (Minecraft.getInstance().world == null)
            return

        val profiler = Minecraft.getInstance().profiler
        profiler.startSection("liblib_particles")
        try {
            systems.forEach {
                it.update()
            }
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }
        profiler.endSection()
    }

    @SubscribeEvent
    fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getInstance().gameSettings.showDebugInfo)
            return

        if(systems.isNotEmpty()) {
            event.left.add("LibrarianLib Particle Systems:")
            var total = 0
            systems.forEach { system ->
                if (system.particles.isNotEmpty()) {
                    event.left.add(" - ${system.javaClass.simpleName}: ${system.particles.size}")
                    total += system.particles.size
                }
            }
            event.left.add(" - $total")
        }
    }

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {
        val profiler = Minecraft.getInstance().profiler

        profiler.startSection("liblib_particles")

        event.matrixStack.push()
        val viewPos = Client.minecraft.gameRenderer.activeRenderInfo.projectedView
        event.matrixStack.translate(-viewPos.x, -viewPos.y, -viewPos.z)

        val entity = Minecraft.getInstance().renderViewEntity
        if (entity != null) {
            try {
                systems.forEach {
                    it.render(event.matrixStack, event.projectionMatrix)
                }
            } catch (e: ConcurrentModificationException) {
                e.printStackTrace()
            }
        }
        event.matrixStack.pop()

        profiler.endSection()
    }

    //TODO forge event fires every frame
    @SubscribeEvent
    fun unloadWorld(event: WorldEvent.Unload) {
//        systems.forEach { it.particles.clear() }
    }

    override fun prepare(resourceManager: IResourceManager, profiler: IProfiler) {
        // nop
    }

    override fun apply(result: Unit, resourceManager: IResourceManager, profiler: IProfiler) {
        systems.forEach {
            it.reload()
        }
    }
}
