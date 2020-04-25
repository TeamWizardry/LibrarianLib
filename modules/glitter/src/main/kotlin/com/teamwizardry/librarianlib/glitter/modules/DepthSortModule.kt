package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.glitter.ParticleGlobalUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding
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
         * A temporary binding used to cache the distance from the player before sorting.
         */
        @JvmField val depth: StoredBinding
): ParticleGlobalUpdateModule {
    init {
        position.require(3)
        depth.require(1)
    }

    override fun update(particles: MutableList<DoubleArray>) {
        val player = Minecraft.getInstance().player!!
        val normal = player.lookVec
        val eyeX = Client.worldTime.interp(player.prevPosX, player.posX)
        val eyeY = Client.worldTime.interp(player.prevPosY, player.posY) + player.eyeHeight
        val eyeZ = Client.worldTime.interp(player.prevPosZ, player.posZ)

        //dot(particle-eye,normal)

        for(particle in particles) {
            position.load(particle)
            val distance = (position.contents[0]-eyeX)*normal.x +
                    (position.contents[1]-eyeY)*normal.y +
                    (position.contents[2]-eyeZ)*normal.z
            particle[depth.index] = distance
        }

        particles.sortByDescending {
            it[depth.index]
        }
    }
}