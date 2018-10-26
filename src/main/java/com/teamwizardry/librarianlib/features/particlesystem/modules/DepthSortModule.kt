package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.*
import net.minecraft.client.Minecraft

/**
 * A simple module that performs a depth sort based on the player's look vector.
 *
 * The sort first stores the depth in the provided [depth] binding, allowing the depth to be calculated once per
 * particle and not repeatedly with each comparison, then sorts based upon the values in that binding.
 */
class DepthSortModule(
        /**
         * The binding for the position of a given particle. Any tomfoolery with renderers not drawing at this position
         * means that information can't be factored into the sorting
         */
        @JvmField val position: ReadParticleBinding,
        /**
         * The binding used to cache the distance from the player before sorting. The value is in blocks from the player
         * along their look vector.
         */
        @JvmField val depth: ReadWriteParticleBinding
): ParticleGlobalUpdateModule {
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