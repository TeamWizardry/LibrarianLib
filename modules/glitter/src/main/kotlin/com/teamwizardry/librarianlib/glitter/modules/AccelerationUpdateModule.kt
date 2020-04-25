package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadWriteParticleBinding

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
        velocity.load(particle)
        for(i in 0 until 3) {
            velocity.contents[i] += acceleration.contents[i]
        }
        velocity.store(particle)
    }
}
