package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
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
object ParticleRenderManager {

    /**
     * Set this to true to reload particle systems at the next opportune moment.
     */
    var needsReload: Boolean = false
    /**
     * The list of registered particle systems, in the order they will tick/render.
     */
    val systems: MutableList<ParticleSystem> = mutableListOf()
    /**
     * The list of reload handlers for particle systems. This is stopgap until particle systems are designed to be
     * subclassed and thus have a reload method built in.
     */
    val reloadHandlers: MutableList<Runnable> = mutableListOf()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    internal fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START)
            return
        if (Minecraft.getMinecraft().currentScreen?.doesGuiPauseGame() == true)
            return
        val profiler = Minecraft.getMinecraft().profiler
        profiler.startSection("liblib_new_particles")
        try {
            if(needsReload) {
                needsReload = false
                reloadHandlers.forEach {
                    it.run()
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
    internal fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return

        event.left.add("LibrarianLib New Particles:")
        event.left.add(" - " + systems.sumBy { it.particles.size })
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    internal fun render(event: CustomWorldRenderEvent) {
        val profiler = Minecraft.getMinecraft().profiler

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT)
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
        GL11.glPopAttrib()
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    internal fun unloadWorld(event: WorldEvent.Unload) {
        systems.forEach { it.particles.clear() }
    }
}

