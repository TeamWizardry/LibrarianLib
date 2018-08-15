package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.ParticleBatchUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import net.minecraft.client.Minecraft

class DepthSortModule(
        private val position: ParticleBinding,
        private val depth: ParticleBinding
): ParticleBatchUpdateModule {
    override fun update(particles: MutableList<DoubleArray>) {
        val player = Minecraft.getMinecraft().player
        val normal = player.lookVec
        val eyeX = ClientTickHandler.interpWorldPartialTicks(player.prevPosX, player.posX)
        val eyeY = ClientTickHandler.interpWorldPartialTicks(player.prevPosY, player.posY) + player.eyeHeight
        val eyeZ = ClientTickHandler.interpWorldPartialTicks(player.prevPosZ, player.posZ)

        //dot(particle-eye,normal)

        for(particle in particles) {
            val posX = position.get(particle, 0)
            val posY = position.get(particle, 1)
            val posZ = position.get(particle, 2)
            val distance = (posX-eyeX)*normal.x +
                    (posY-eyeY)*normal.y +
                    (posZ-eyeZ)*normal.z
            depth.set(particle, 0, distance)
        }

        particles.sortByDescending { depth.get(it, 0) }
    }
}