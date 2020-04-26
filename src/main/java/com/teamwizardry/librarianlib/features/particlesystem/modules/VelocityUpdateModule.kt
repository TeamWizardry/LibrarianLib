package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.*

/**
 * Performs rudimentary velocity updates.
 *
 * The process of this module is simple but useful, first it copies [position] into [previousPosition],
 * then adds [velocity] to [position] and stores it back in [position]
 */
class VelocityUpdateModule(
        /**
         * The position to be moved based upon [velocity]
         */
        @JvmField val position: ReadWriteParticleBinding,
        /**
         * The velocity to moved [position] by
         */
        @JvmField val velocity: ReadParticleBinding,
        /**
         * The binding to store the position in before modifying it, if desired
         */
        @JvmField val previousPosition: WriteParticleBinding? = null
): ParticleUpdateModule {
    init {
        position.require(3)
        velocity.require(3)
        previousPosition?.require(3)
    }

    override fun update(particle: DoubleArray) {
        position.load(particle)
        if(previousPosition != null) {
            position.contents.copyInto(previousPosition.contents)
            previousPosition.store(particle)
        }
        velocity.load(particle)
        for(i in 0 until 3) {
            position.contents[i] += velocity.contents[i]
        }
        position.store(particle)
    }
}