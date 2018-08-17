package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.*
import net.minecraft.client.Minecraft

class DepthSortModule(
        private val position: ReadParticleBinding,
        private val depth: ReadWriteParticleBinding
): ParticleBatchUpdateModule {
    init {
        position.require(3)
        depth.require(1)
    }
    override fun update(particles: MutableList<DoubleArray>) {
        val player = Minecraft.getMinecraft().player
        val normal = player.lookVec
        val eyeX = ClientTickHandler.interpWorldPartialTicks(player.prevPosX, player.posX)
        val eyeY = ClientTickHandler.interpWorldPartialTicks(player.prevPosY, player.posY) + player.eyeHeight
        val eyeZ = ClientTickHandler.interpWorldPartialTicks(player.prevPosZ, player.posZ)

        //dot(particle-eye,normal)

        for(particle in particles) {
            val posX = position[particle, 0]
            val posY = position[particle, 1]
            val posZ = position[particle, 2]
            val distance = (posX-eyeX)*normal.x +
                    (posY-eyeY)*normal.y +
                    (posZ-eyeZ)*normal.z
            depth[particle, 0] = distance
        }

        particles.sortByDescending { depth[it, 0] }
    }
}