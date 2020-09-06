package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.glitter.*

/**
 * Performs rudimentary velocity updates.
 *
 * The process of this module is simple but useful, first it copies [position] into [previousPosition],
 * then adds [velocity] to [position] and stores it back in [position]
 */
public class VelocityUpdateModule(
    /**
     * The position to be moved based upon [velocity]
     */
    @JvmField public val position: ReadWriteParticleBinding,
    /**
     * The velocity to moved [position] by
     */
    @JvmField public val velocity: ReadParticleBinding,
    /**
     * The binding to store the position in before modifying it, if desired
     */
    @JvmField public val previousPosition: WriteParticleBinding? = null
): ParticleUpdateModule {
    init {
        position.require(3)
        velocity.require(3)
        previousPosition?.require(3)
    }

    override fun update(particle: DoubleArray) {
        position.load(particle)
        if (previousPosition != null) {
            position.contents.copyInto(previousPosition.contents)
        }
        velocity.load(particle)
        for (i in 0 until 3) {
            position.contents[i] += velocity.contents[i]
        }
        position.store(particle)
    }
}