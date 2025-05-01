package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.glitter.ParticleGlobalUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding

/**
 * A simple module that performs a depth sort based on the player's look vector.
 *
 * The sort first stores the depth in the provided [depth] binding, allowing the depth to be calculated once per
 * particle and not repeatedly with each comparison, then sorts based upon the values in that binding.
 */
public class DepthSortModule(
    /**
     * The binding for the position of a given particle. Any tomfoolery with renderers not drawing at this position
     * means that information can't be factored into the sorting
     */
    @JvmField public val position: ReadParticleBinding,
    /**
     * A temporary binding used to cache the distance from the player before sorting.
     */
    @JvmField public val depth: StoredBinding
): ParticleGlobalUpdateModule {
    init {
        position.require(3)
        depth.require(1)
    }

    override fun update(particles: MutableList<DoubleArray>) {
        val player = Client.player!!
        val normal = player.rotationVector
        val eyeX = Client.worldTime.interp(player.prevX, player.x)
        val eyeY = Client.worldTime.interp(player.prevY, player.y) + player.standingEyeHeight
        val eyeZ = Client.worldTime.interp(player.prevZ, player.z)

//        Client.minecraft.gameRenderer.camera.pos

        //dot(particle-eye,normal)

        for (particle in particles) {
            position.load(particle)
            val distance = (position.contents[0] - eyeX) * normal.x +
                (position.contents[1] - eyeY) * normal.y +
                (position.contents[2] - eyeZ) * normal.z
            particle[depth.index] = distance
        }

        particles.sortByDescending {
            it[depth.index]
        }
    }
}