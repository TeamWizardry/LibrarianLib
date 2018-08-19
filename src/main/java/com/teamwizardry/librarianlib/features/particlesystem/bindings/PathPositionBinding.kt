package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

/**
 * A 3D binding that reads the position values from a [ParticlePath].
 *
 * By default the path is traversed from 0-1 across the lifetime of the particle, however using [timescale] and [offset]
 * this timing can be adjusted. The timing equation is: `pathFraction = (age/lifetime + offset) * retime % 1`, where
 * [timescale] and [offset] are replaced with 1 and 0 respectively if they are null
 *
 * Once the time is determined, the position returned from the passed path will be added to [origin], allowing paths
 * to be moved within the world
 */
class PathPositionBinding(
        /**
         * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
         */
        @JvmField val lifetime: ReadParticleBinding,
        /**
         * The age binding for the particle. Generally [ParticleSystem.age]
         */
        @JvmField val age: ReadParticleBinding,
        /**
         * The multiplier for the normalized age. If this value is > 1 the movement will loop, and if this value is < 1
         * the movement will end before the end of the path.
         */
        @JvmField val timescale: ReadParticleBinding?,
        /**
         * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s value,
         * if the offset is 0.5, the animation will begin halfway along the path
         */
        @JvmField val offset: ReadParticleBinding?,
        /**
         * The origin for the path. Allows the path to be freely moved as opposed to being relative to the world origin.
         */
        @JvmField val origin: ReadParticleBinding,
        /**
         * The path object to use for the positioning.
         */
        @JvmField val path: ParticlePath
): ReadParticleBinding {
    init {
        lifetime.require(1)
        age.require(1)
        timescale?.require(1)
        offset?.require(1)
        origin.require(3)
    }

    override val size: Int = 3

    override fun get(particle: DoubleArray, index: Int): Double {
        var time = age[particle, 0]/lifetime[particle, 0]
        if(offset != null) time += offset[particle, 0]
        if(timescale != null) time *= timescale[particle, 0]
        time %= 1
        return origin[particle, index] + path.getPosition(particle, time, index)
    }
}
