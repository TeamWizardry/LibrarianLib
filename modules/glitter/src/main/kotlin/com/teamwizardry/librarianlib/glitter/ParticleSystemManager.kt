package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.util.ConcurrentModificationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * This object is responsible for the rendering and updating of particle systems, and is where new particle systems
 * are sent to be rendered and ticked.
 */
public object ParticleSystemManager: SimpleResourceReloadListener<Unit>, WorldRenderEvents.Last {

    override fun getFabricId(): Identifier = Identifier("liblib-glitter:particle_system_manager")

    private val _systems: MutableList<ParticleSystem> = mutableListOf()
    public val systems: List<ParticleSystem> = _systems.unmodifiableView()

    public fun add(system: ParticleSystem) {
        if (!_systems.contains(system)) {
            _systems.add(system)
        }
    }

    public fun remove(system: ParticleSystem) {
        _systems.remove(system)
    }

    internal fun tickParticles() {
        if (Client.minecraft.isPaused)
            return
        if (Client.minecraft.world == null)
            return

        val profiler = Client.minecraft.profiler
        profiler.push("liblib_particles")
        try {
            _systems.forEach {
                profiler.push(it.javaClass.simpleName)
                it.update()
                profiler.pop()
            }
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }
        profiler.pop()
    }

//    @SubscribeEvent
//    fun debug(event: RenderGameOverlayEvent.Text) {
//        if (!Minecraft.getInstance().gameSettings.showDebugInfo)
//            return
//
//        if (systems.isNotEmpty()) {
//            event.left.add("LibrarianLib Glitter:")
//            var total = 0
//            systems.forEach { system ->
//                if (system.particles.isNotEmpty()) {
//
//                    total += system.particles.size
//                }
//            }
//            event.left.add(" - $total")
//        }
//    }

    override fun onLast(context: WorldRenderContext) {
        val profiler = context.profiler()

        profiler.push("liblib_glitter")

        context.matrixStack().push()
        val viewPos = Client.minecraft.gameRenderer.camera.pos
        context.matrixStack().translate(-viewPos.x, -viewPos.y, -viewPos.z)

//        val entity = Minecraft.getInstance().renderViewEntity
//        RenderSystem.disableLighting()
//        if (entity != null) {
        try {
            _systems.forEach {
                it.renderDirect(context)
            }
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }
//        }
        context.matrixStack().pop()

        profiler.pop()
    }

    internal fun clearParticles() {
        _systems.forEach { it.clear() }
    }

    override fun load(manager: ResourceManager?, profiler: Profiler?, executor: Executor?): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync { Unit }
    }

    override fun apply(
        data: Unit?,
        manager: ResourceManager?,
        profiler: Profiler?,
        executor: Executor?
    ): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            for(system in _systems) {
                system.reload()
            }
        }
    }
}
