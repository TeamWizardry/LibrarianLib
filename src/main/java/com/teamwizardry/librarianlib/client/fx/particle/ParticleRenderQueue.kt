package com.teamwizardry.librarianlib.client.fx.particle

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.math.Vec3d

abstract class ParticleRenderQueue<T : QueuedParticle<T>>(sort: Boolean) {

    protected var renderQueue: MutableList<T> = mutableListOf()
    protected var sort = false

    init {
        this.sort = sort
        ParticleRenderDispatcher.INSTANCE.addQueue(this)
    }

    fun add(particle: T) {
        renderQueue.add(particle)
    }

    abstract fun name(): String

    fun dispatchQueuedRenders(tessellator: Tessellator) {
        if (renderQueue.size == 0) return
        if (sort) {
            val partialTicks = renderQueue[0].partialTicks
            val entity = Minecraft.getMinecraft().thePlayer

            val d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
            val d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
            val d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()

            val playerPos = Vec3d(d0, d1, d2)
            val look = playerPos.add(entity.getLook(partialTicks))
            projectToRay(playerPos, look, Vec3d.ZERO)

            for (t in renderQueue) {
                var v = t.pos
                v = projectToRay(playerPos, look, v)
                t.distFromPlayer = v.lengthVector()
            }
            renderQueue.sortByDescending { it.distFromPlayer }
        }
        renderParticles(tessellator)
    }

    abstract fun renderParticles(tessellator: Tessellator)

    private fun projectToRay(a: Vec3d, b: Vec3d, p: Vec3d): Vec3d {
        val ap = p.subtract(a)
        val ab = b.subtract(a)
        return a.add(ab.scale(ap.dotProduct(ab) / ab.dotProduct(ab)))
    }
}
