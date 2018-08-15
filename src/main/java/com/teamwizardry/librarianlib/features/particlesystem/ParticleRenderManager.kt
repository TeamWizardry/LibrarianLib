package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object ParticleRenderManager {

    var needsReload: Boolean = false
    val emitters: MutableList<ParticleSystem> = mutableListOf()
    val reloadHandlers: MutableList<Runnable> = mutableListOf()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START)
            return
        if (Minecraft.getMinecraft().currentScreen?.doesGuiPauseGame() == true)
            return
        val profiler = Minecraft.getMinecraft().mcProfiler
        profiler.startSection("liblib_new_particles")
        try {
            if(needsReload) {
                needsReload = false
                reloadHandlers.forEach {
                    it.run()
                }
            }
            emitters.forEach {
                it.update()
            }
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }
        profiler.endSection()
//
//        profiler.startSection("clean")
//        layers.forEach {
//            profiler.startSection(it.name)
//            it.clean()
//            profiler.endSection()
//        }
//
//        profiler.endStartSection("update")
//
//        layers.forEach {
//            profiler.startSection(it.name)
//            it.update()
//            profiler.endSection()
//        }
//
//        profiler.endStartSection("sort")
//
//        layers.forEach {
//            profiler.startSection(it.name)
//            it.sort()
//            profiler.endSection()
//        }
//
//        profiler.endSection()
//
    }

    @SubscribeEvent
    fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return

        event.left.add("LibrarianLib New Particles:")
        event.left.add(" - " + emitters.sumBy { it.particles.size })
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun render(event: CustomWorldRenderEvent) {
        val profiler = Minecraft.getMinecraft().mcProfiler

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT)
//        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1 / 256f)
        GlStateManager.disableLighting()

        profiler.startSection("liblib_particles")

        val entity = Minecraft.getMinecraft().renderViewEntity
        val partialTicks = if (Minecraft.getMinecraft().isGamePaused)
            Minecraft.getMinecraft().renderPartialTicksPaused
        else
            ClientTickHandler.partialTicks
        if (entity != null) {
            try {
            emitters.forEach {
//                profiler.startSection(it.name)
                it.render()
//                profiler.endSection()
            }

            } catch (e: ConcurrentModificationException) {
                e.printStackTrace()
            }
        }

        profiler.endSection()

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.disableBlend()
//        GlStateManager.depthMask(true)
        GL11.glPopAttrib()
    }

    internal fun projectToRay(a: Vec3d, b: Vec3d, p: Vec3d): Vec3d {
        val ap = p.subtract(a)
        val ab = b.subtract(a)
        return a.add(ab.scale(ap.dotProduct(ab) / ab.dotProduct(ab)))
    }

}

val Minecraft.renderPartialTicksPaused by MethodHandleHelper.delegateForReadOnly<Minecraft, Float>(Minecraft::class.java, "renderPartialTicksPaused", "field_193996_ah")
