package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * The soon-to-be-renamed central hub for particle systems.
 *
 * This object is responsible for the rendering and updating of particle systems, and is where new particle systems
 * are sent to be rendered and ticked.
 */
internal object GameParticleSystems {

    var needsReload: Boolean = false

    val systems: MutableList<ParticleSystem> = mutableListOf()

    init {
        MinecraftForge.EVENT_BUS.register(this)
        ClientRunnable.registerReloadHandler {
            systems.forEach { it.reload() }
        }
    }

    fun add(system: ParticleSystem) {
        if (!systems.contains(system)) {
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
        if (Minecraft.getMinecraft().currentScreen?.doesGuiPauseGame() == true)
            return
        if (Minecraft.getMinecraft().world == null)
            return

        val profiler = Minecraft.getMinecraft().profiler
        profiler.startSection("liblib_new_particles")
        try {
            if (needsReload) {
                needsReload = false
                systems.forEach {
                    it.reload()
                }
            }
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
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return

        if (systems.isNotEmpty()) {
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
    @Suppress("UNUSED_PARAMETER")
    fun render(event: CustomWorldRenderEvent) {
        val profiler = Minecraft.getMinecraft().profiler

        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1 / 256f)
        GlStateManager.disableLighting()

        profiler.startSection("liblib_particles")

        val entity = Minecraft.getMinecraft().renderViewEntity
        if (entity != null) {
            try {
                systems.forEach {
                    if (!it.manuallyRender)
                        it.render()
                }

            } catch (e: ConcurrentModificationException) {
                e.printStackTrace()
            }
        }

        profiler.endSection()

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun unloadWorld(event: WorldEvent.Unload) {
        systems.forEach { it.particles.clear() }
    }
}

