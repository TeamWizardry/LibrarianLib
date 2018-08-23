package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

/**
 * Performs rudimentary acceleration updates.
 *
 * Even more rudimentary than the [VelocityUpdateModule], this module simply adds [acceleration] to [velocity].
 */
class AccelerationUpdateModule(
        /**
         * The velocity to be accelerated by [acceleration]
         */
        @JvmField val velocity: ReadWriteParticleBinding,
        /**
         * The acceleration to add to [velocity] every tick
         */
        @JvmField val acceleration: ReadParticleBinding
): ParticleUpdateModule {
    init {
        velocity.require(3)
        acceleration.require(3)
    }

    override fun update(particle: DoubleArray) {
        update(particle, 0)
        update(particle, 1)
        update(particle, 2)
    }

    private fun update(particle: DoubleArray, index: Int) {
        val vel = velocity[particle, index]
        velocity[particle, index] = vel + acceleration[particle, index]
    }
}
