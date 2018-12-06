package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * The soon-to-be-renamed central hub for particle systems.
 *
 * This object is responsible for the rendering and updating of particle systems, and is where new particle systems
 * are sent to be rendered and ticked.
 */
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = LibrarianLib.MODID)
internal object GameParticleSystems {

    var needsReload: Boolean = false

    val systems: MutableList<ParticleSystem> = mutableListOf()

    init {
        ClientRunnable.registerReloadHandler {
            systems.forEach { it.reload() }
        }
    }

    fun add(system: ParticleSystem) {
        if(!systems.contains(system)) {
            systems.add(system)
        }
    }

    fun remove(system: ParticleSystem) {
        systems.remove(system)
    }

    @JvmStatic
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
            if(needsReload) {
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

    @JvmStatic
    @SubscribeEvent
    fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
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

    @JvmStatic
    @SubscribeEvent
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

    @JvmStatic
    @SubscribeEvent
    fun unloadWorld(event: WorldEvent.Unload) {
        systems.forEach { it.particles.clear() }
    }
}

