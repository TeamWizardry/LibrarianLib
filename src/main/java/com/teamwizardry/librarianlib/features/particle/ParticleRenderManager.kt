package com.teamwizardry.librarianlib.features.particle

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object ParticleRenderManager {

    @JvmStatic
    val LAYER_BLOCK_MAP_ADDITIVE = object : ParticleRenderLayer("blockMap+", true) {
        override fun setup() {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

//            GlStateManager.depthMask(false);
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F)
            GlStateManager.disableLighting()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)


            val tessellator = Tessellator.getInstance()
            val vertexbuffer = tessellator.buffer
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP)
        }

        override fun teardown() {
            val tessellator = Tessellator.getInstance()
            tessellator.draw()

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
            GlStateManager.disableBlend()
//            GlStateManager.depthMask(true);
            GlStateManager.enableLighting()
        }

    }

    @JvmStatic
    val LAYER_BLOCK_MAP = object : ParticleRenderLayer("blockMap", false) {
        override fun setup() {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            GlStateManager.enableBlend()
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F)
            GlStateManager.disableLighting()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)


            val tessellator = Tessellator.getInstance()
            val vertexbuffer = tessellator.buffer
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP)
        }

        override fun teardown() {
            val tessellator = Tessellator.getInstance()
            tessellator.draw()

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
            GlStateManager.disableBlend()
            GlStateManager.enableLighting()
        }

    }

    val layers: MutableList<ParticleRenderLayer> = mutableListOf()
    private val queuedParticleAdditions: Deque<ParticleBase> = ArrayDeque()

    init {
        register(LAYER_BLOCK_MAP)
        register(LAYER_BLOCK_MAP_ADDITIVE)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @JvmStatic
    fun spawn(particle: ParticleBase) {
        queuedParticleAdditions.add(particle)
    }

    @JvmStatic
    fun register(layer: ParticleRenderLayer) {
        layers.add(layer)
    }

    @SubscribeEvent
    fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return

        event.left.add("LibrarianLib Particles:")
        layers.forEach {
            event.left.add("  " + it.name + " - " + it.particleList.size)
        }
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START)
            return
        if (Minecraft.getMinecraft().currentScreen?.doesGuiPauseGame() == true)
            return
        val profiler = Minecraft.getMinecraft().profiler
        profiler.startSection("liblib_particles")

        profiler.startSection("clean")
        layers.forEach {
            profiler.startSection(it.name)
            it.clean()
            profiler.endSection()
        }

        profiler.endStartSection("update")

        layers.forEach {
            profiler.startSection(it.name)
            it.update()
            profiler.endSection()
        }

        profiler.endStartSection("sort")

        layers.forEach {
            profiler.startSection(it.name)
            it.sort()
            profiler.endSection()
        }

        profiler.endSection()

        profiler.endSection()
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun unloadWorld(event: WorldEvent.Unload) {
        layers.forEach(ParticleRenderLayer::clear)
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun render(event: RenderWorldLastEvent) {
        val profiler = Minecraft.getMinecraft().profiler

        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1 / 256f)
        GlStateManager.disableLighting()

        profiler.startSection("liblib_particles")

        queuedParticleAdditions.forEach {
            it.renderFunc.getLayer().add(it)
        }
        queuedParticleAdditions.clear()

        val entity = Minecraft.getMinecraft().renderViewEntity
        val partialTicks = if (Minecraft.getMinecraft().isGamePaused)
            Minecraft.getMinecraft().renderPartialTicksPaused
        else
            ClientTickHandler.partialTicks
        if (entity != null) {
            Particle.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
            Particle.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
            Particle.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()
            Particle.cameraViewDir = entity.getLook(partialTicks)

            val renderInfo = ParticleRenderInfo(
                    entity,
                    partialTicks,
                    ActiveRenderInfo.getRotationX(),
                    ActiveRenderInfo.getRotationXZ(),
                    ActiveRenderInfo.getRotationZ(),
                    ActiveRenderInfo.getRotationYZ(),
                    ActiveRenderInfo.getRotationXY()
            )

            layers.forEach {
                profiler.startSection(it.name)
                it.render(renderInfo)
                profiler.endSection()
            }
        }

        profiler.endSection()

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.disableBlend()
        GlStateManager.depthMask(true)
        GlStateManager.enableLighting()
    }

    internal fun projectToRay(a: Vec3d, b: Vec3d, p: Vec3d): Vec3d {
        val ap = p.subtract(a)
        val ab = b.subtract(a)
        return a.add(ab.scale(ap.dotProduct(ab) / ab.dotProduct(ab)))
    }

}

data class ParticleRenderInfo(val entityIn: Entity?, val partialTicks: Float, val rotationX: Float, val rotationZ: Float, val rotationYZ: Float, val rotationXY: Float, val rotationXZ: Float)

abstract class ParticleRenderLayer(val name: String, val shouldSort: Boolean) {

    var particleList: MutableList<ParticleBase> = mutableListOf()
        private set

    abstract fun setup()

    abstract fun teardown()

    fun add(particle: ParticleBase) {
        particleList.add(particle)
    }

    fun update() {
        particleList.forEach { it.onUpdate() }
    }

    fun render(renderInfo: ParticleRenderInfo) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        setup()

        particleList.forEach {
            it.render(vb, renderInfo)
        }

        teardown()

    }

    fun clear() {
        particleList.clear()
    }

    fun clean() {
        if (particleList.size > 100000)
            clear() // sometimes the system sprials for no aparent reason


        val iter = particleList.iterator()
        for (particle in iter) {
            if (!particle.isAlive()) {
                iter.remove()
            }
        }
    }

    fun sort() {
        if (!shouldSort)
            return
        val partialTicks = ClientTickHandler.partialTicks
        // render entity may be null because this method isn't called in the render function
        val entity = Minecraft.getMinecraft().renderViewEntity ?: return

        val d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
        val d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
        val d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()

        val playerPos = Vec3d(d0, d1, d2)
        val look = playerPos.add(entity.getLook(partialTicks))

        for (particle in particleList) {
            particle.depthSquared = ParticleRenderManager.projectToRay(playerPos, look, particle.pos).lengthSquared()
        }
        particleList.sortByDescending { it.depthSquared }
    }

}

val Minecraft.renderPartialTicksPaused by MethodHandleHelper.delegateForReadOnly<Minecraft, Float>(Minecraft::class.java, "renderPartialTicksPaused", "field_193996_ah")
