package com.teamwizardry.librarianlib.glitter.bindings

import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.glitter.ParticlePath
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding

/**
 * A 3D binding that reads tangent values from a ParticlePath.
 *
 * By default the path is traversed from 0-1 across the lifetime of the particle, however using [timescale] and [offset]
 * this timing can be adjusted. The timing equation is: `pathFraction = (age/lifetime + offset) * timescale % 1`, where
 * [timescale] and [offset] are replaced with 1 and 0 respectively if they are null
 *
 * Once the time is determined, the tangent returned from the passed path will be multiplied by [speed], allowing paths
 * to be moved within the world
 *
 * @see ParticlePath
 */
class PathVelocityBinding(
        /**
         * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
         */
        override val lifetime: ReadParticleBinding,
        /**
         * The age binding for the particle. Generally [ParticleSystem.age]
         */
        override val age: ReadParticleBinding,
        /**
         * The multiplier for the normalized age. If this value is > 1 the movement will loop, and if this value is < 1
         * the movement will end before the end of the path.
         */
        override val timescale: ReadParticleBinding?,
        /**
         * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s value,
         * if the offset is 0.5, the animation will begin halfway along the path
         */
        override val offset: ReadParticleBinding?,
        /**
         * The speed multiplier for the returned velocity.
         */
        @JvmField val speed: ReadParticleBinding?,
        /**
         * The path object to use for the tangent vectors.
         */
        @JvmField val path: ParticlePath,
        /**
         * The easing to use when generating values for the binding.
         */
        override val easing: Easing = Easing.linear
): AbstractTimeBinding(lifetime, age, timescale, offset, easing) {

    override var contents: DoubleArray = DoubleArray(path.value.size)

    init {
        lifetime.require(1)
        age.require(1)
        timescale?.require(1)
        offset?.require(1)
    }

    override fun load(particle: DoubleArray) {
        super.load(particle)
        path.computeTangent(particle, time * easing(time.toFloat()))
        path.value.copyInto(contents)
    }
}
